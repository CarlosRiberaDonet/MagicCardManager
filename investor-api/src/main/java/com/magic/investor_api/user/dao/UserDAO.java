package com.magic.investor_api.user.dao;

import com.magic.investor_api.scryfall.dto.ScryfallCardDTO;
import com.magic.investor_api.cardmarketPrice.model.CardmarketPrice;
import com.magic.investor_api.user.dto.UserCollectionDTO;
import com.magic.investor_api.user.dto.UserDTO;
import com.magic.investor_api.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository

public class UserDAO {

    @Autowired
    private DataSource dataSource;
    private final PasswordEncoder passwordEncoder;

    public UserDAO(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // Registrar usuario
    public boolean insertNewUser(UserDTO userDTO){

        String query = "INSERT INTO user (email, password, role) VALUES(?, ?, ?)";
        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setString(1, userDTO.getEmail());
            stmt.setString(2, userDTO.getPassword());
            stmt.setString(3, "ADMIN");

            int filasAfectadas = stmt.executeUpdate();
            if(filasAfectadas > 0){
                // Si el usuario se inserta correctamente en la BD
                System.out.println("Usuario: " + userDTO.getEmail() + " Registrado correctamente.");
                return true;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    // Comprobar si el email y el password existen en la BD
    public User finfByEmail(String email){
        String query = "SELECT id, email, password, role FROM user WHERE email = ?";
        
        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                // Si el usuario existe
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                return user;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null; // null si no existe
    }

    // Insertar carta en la tabla user_collection
    public boolean insertCollectionCard(UserCollectionDTO dto){

        // Compruebo si la carta ya está en la colección del usuario
        if (selectCollectionCardQuantity(dto) > 0) {
            System.out.println("La carta ya está en la colección " );
            // Compruebo si el precio de compra es el mismo
            if(selectCollectionCardPrice(dto.getUserId(), dto.getCardId()) == dto.getPurchasePrice().doubleValue()){
                System.out.println("El precio de compra es el mismo");
                return updateQuantityCollection(dto.getUserId(), dto.getCardId(), +1); // Sumo +1 a la cantidad de la carta
            }
        }

        System.out.println("Insertando carta");
        String query = "INSERT INTO user_collection (user_id, card_id, purchase_price, quantity) " +
                "VALUES(?, ?, ?, ?)";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setLong(1, dto.getUserId());
            stmt.setLong(2, dto.getCardId());
            stmt.setDouble(3, dto.getPurchasePrice());
            stmt.setInt(4, dto.getQuantity());

            int filasAfectadas = stmt.executeUpdate();
            if(filasAfectadas > 0){
                System.out.println("Guardando: " + dto.toString());
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    // Eliminar una carta de user_collection
    public boolean deleteCollectionCard(UserCollectionDTO dto){

        int quantity = selectCollectionCardQuantity(dto);
        
        // Si quantity > 1
        if(quantity > 1){
            updateQuantityCollection(dto.getUserId(), dto.getCardId(), -1); // resto -1 a quantity
            return true;
        }
        else if(quantity == 1) { // Si quantity == 1, elimino la fila
            String query = "DELETE FROM user_collection WHERE user_id = ? AND card_id = ?";
            try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setLong(1, dto.getUserId());
                stmt.setLong(2, dto.getCardId());
                int filasAfectadas = stmt.executeUpdate();
                return filasAfectadas > 0;
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    // Insertar carta en la tabla user_watchlist
    public boolean insertWatchlistCard(UserCollectionDTO dto){

        String query = "INSERT INTO user_watchlist (user_id, card_id) VALUES (?, ?)";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setLong(1, dto.getUserId());
            stmt.setLong(2, dto.getCardId());

            int filasAfectadas = stmt.executeUpdate();
            if(filasAfectadas > 0){
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    // Eliminar una carta de user_watchlist
    public boolean deleteWatchlistCard(UserCollectionDTO dto){

        String query = "DELETE FROM user_watchlist WHERE user_id = ? AND card_id = ?";
        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, dto.getUserId());
            stmt.setLong(2, dto.getCardId());
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        }catch (SQLException e){
            e.printStackTrace();
        }

    return false;
    }

    // Obtener cantidad total de una carta en user_collection (sin diferenciar purchase_price)
    public int selectCollectionCardQuantity(UserCollectionDTO dto){

        int total = 0;

        String query = "SELECT quantity FROM user_collection WHERE user_id = ? AND card_id = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setLong(1, dto.getUserId());
            stmt.setLong(2, dto.getCardId());

            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                total += rs.getInt("quantity");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return total;
    }

    // Obtener cantidad de una carta en user_collection diferenciando purchase_price
    public int selectCollectionCardPriceQuantity(UserCollectionDTO dto){
        String query = "SELECT quantity FROM user_collection WHERE user_id = ? AND card_id = ? AND purchase_price = ?";
        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setLong(1, dto.getUserId());
            stmt.setLong(2, dto.getCardId());
            stmt.setDouble(3, dto.getPurchasePrice());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getInt("quantity");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    // Obtener precio de una carta en user_collection
    public Double selectCollectionCardPrice(Long userId, Long cardId){
        String query = "SELECT purchase_price FROM user_collection WHERE user_id = ? AND card_id = ?";
        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setLong(1, userId);
            stmt.setLong(2, cardId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getDouble("purchase_price");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0.0;
    }

    // Comprobar si la carta ya está en user_watchlist
    public boolean selectWatchlistCardId(Long userId, Long cardId){
        String query = "SELECT id FROM user_watchlist WHERE user_id = ? AND card_id = ?";
        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setLong(1, userId);
            stmt.setLong(2, cardId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    // Actualiza la cantidad de una carta en user_collection
    public boolean updateQuantityCollection(Long userId, Long cardId, int quantity){
        String query = "UPDATE user_collection SET quantity = quantity + ? WHERE user_id = ? AND card_id = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, quantity);
            stmt.setLong(2, userId);
            stmt.setLong(3, cardId);

            int filasAfectadas = stmt.executeUpdate();
            if(filasAfectadas > 0){
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    // Obtener todas las cartas del usuario de user_watchlist
    public List<UserCollectionDTO> selectCollectionCards(Long userId){
        List<UserCollectionDTO> userCollectionDTOList = new ArrayList<>();
        String query = "SELECT user_id, card_id, purchase_price, quantity, added_at " +
                "FROM user_collection WHERE user_id= ?";
        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                UserCollectionDTO dto = new UserCollectionDTO();
                dto.setUserId(rs.getLong("user_id"));
                dto.setCardId(rs.getLong("card_id"));
                dto.setQuantity(rs.getInt("quantity"));
                dto.setAddedAt(rs.getDate("added_ad").toLocalDate().atStartOfDay().toLocalDate());
                userCollectionDTOList.add(dto);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return userCollectionDTOList;
    }

    // Obtener cartas de la colección del usuario de user_collection
    public List<UserCollectionDTO> selectMyCollection(Long userId){

        List<UserCollectionDTO> userCollectionDTO = new ArrayList<>();

        String query = "SELECT uc.user_id, uc.card_id, uc.purchase_price, uc.quantity, uc.card_condition, uc.added_at, " +
                "sc.scryfall_id, sc.name, sc.printed_name, sc.lang, sc.image_url, sc.rarity, sc.set_name, " +
                "sc.collector_number, sc.type_line, sc.border_color, sc.frame, sc.is_reprint, " +
                "sc.released_at, cp.low, cp.trend, cp.low_foil, cp.trend_foil, cp.updated_at, " +
                "s.icon_svg_uri " +
                "FROM user_collection uc " +
                "JOIN scryfall_card sc ON uc.card_id = sc.id " +
                "JOIN scryfall_set s ON sc.set_code = s.set_code " +
                "JOIN cardmarket_price cp ON cp.cardmarket_id = sc.cardmarket_id " +
                "WHERE user_id = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setLong(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CardmarketPrice cardmarketPrice = new CardmarketPrice();
                cardmarketPrice.setLow(rs.getBigDecimal("low"));
                cardmarketPrice.setTrend(rs.getBigDecimal("trend"));
                cardmarketPrice.setLowFoil(rs.getBigDecimal("low_foil"));
                cardmarketPrice.setTrendFoil(rs.getBigDecimal("trend_foil"));
                cardmarketPrice.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

                ScryfallCardDTO scryfallCardDTO = new ScryfallCardDTO();
                scryfallCardDTO.setScryfallId(rs.getString("scryfall_id"));
                scryfallCardDTO.setName(rs.getString("name"));
                scryfallCardDTO.setPrintedName(rs.getString("printed_name"));
                scryfallCardDTO.setLang(rs.getString("lang"));
                scryfallCardDTO.setImageUrl(rs.getString("image_url"));
                scryfallCardDTO.setRarity(rs.getString("rarity"));
                scryfallCardDTO.setSetName(rs.getString("set_name"));
                scryfallCardDTO.setCollectorNumber(rs.getString("collector_number"));
                scryfallCardDTO.setTypeLine(rs.getString("type_line"));
                scryfallCardDTO.setBorderColor(rs.getString("border_color"));
                scryfallCardDTO.setFrame(rs.getString("frame"));
                scryfallCardDTO.setReprint(rs.getBoolean("is_reprint"));
                scryfallCardDTO.setReleasedAt(rs.getDate("released_at") != null ? rs.getDate("released_at").toLocalDate() : null);
                scryfallCardDTO.setCardPrice(cardmarketPrice);
                scryfallCardDTO.setIconSvgUri(rs.getString("icon_svg_uri"));

                UserCollectionDTO collectionDTO = new UserCollectionDTO();
                collectionDTO.setUserId(rs.getLong("user_id"));
                collectionDTO.setCardId(rs.getLong("card_id"));
                collectionDTO.setPurchasePrice(rs.getDouble("purchase_price"));
                collectionDTO.setQuantity(rs.getInt("quantity"));
                collectionDTO.setCardCondition(rs.getString("card_condition"));
                collectionDTO.setAddedAt(rs.getDate("added_at").toLocalDate());
                collectionDTO.setScryfallCard(scryfallCardDTO);

                userCollectionDTO.add(collectionDTO);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return userCollectionDTO;
    }
}