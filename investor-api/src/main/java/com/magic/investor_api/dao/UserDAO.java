package com.magic.investor_api.dao;

import com.magic.investor_api.dto.CardPageDTO;
import com.magic.investor_api.dto.ScryfallCardDTO;
import com.magic.investor_api.dto.UserCollectionDTO;
import com.magic.investor_api.dto.UserDTO;
import com.magic.investor_api.model.CardPrice;
import com.magic.investor_api.model.User;
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

        if (selectCollectionCardQuantity(dto.getUserId(), dto.getCardId()) > 0) {
            return updateQuantityCollection(dto.getUserId(), dto.getCardId(), +1);
        }

        String query = "INSERT INTO user_collection (user_id, card_id, purchase_price, quantity) " +
                "VALUES(?, ?, ?, ?)";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setLong(1, dto.getUserId());
            stmt.setLong(2, dto.getCardId());
            if (dto.getPurchasePrice() != null) {
                stmt.setDouble(3, dto.getPurchasePrice());
            } else {
                stmt.setNull(3, java.sql.Types.DECIMAL);
            }
            stmt.setInt(4, 1);

            int filasAfectadas = stmt.executeUpdate();
            if(filasAfectadas > 0){
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    // Eliminar una carta de user_collection
    public boolean deleteCollectionCard(UserCollectionDTO dto){

        int quantity = selectCollectionCardQuantity(dto.getUserId(), dto.getCardId());
        
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

    // Obtener cantidad de una carta en user_collection
    public int selectCollectionCardQuantity(Long userId, Long cardId){
        String query = "SELECT quantity FROM user_collection WHERE user_id = ? AND card_id = ?";
        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setLong(1, userId);
            stmt.setLong(2, cardId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getInt("quantity");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
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
    public List<Long> selectCollectionCards(Long userId){
        List<Long> collectionIdList = new ArrayList<>();
        String query = "SELECT card_id FROM user_watchlist WHERE user_id= ?";
        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                collectionIdList.add(rs.getLong("card_id"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return collectionIdList;
    }

    // Obtener cartas de la colección del usuario de user_collection
    public List<ScryfallCardDTO> selectMyCollection(Long userId){
        List<ScryfallCardDTO> cardListDTO = new ArrayList<>();
        String query = "SELECT uc.*,  sc.id AS card_id, sc.cardmarket_id, sc.cardtrader_id, " +
                "sc.name, sc.printed_name, sc.lang, sc.image_url, sc.rarity, sc.set_name, " +
                "sc.collector_number, sc.cardmarket_url, sc.type_line, " +
                "sc.border_color, sc.frame, sc.is_reprint, sc.released_at, " +
                "cp.low, cp.trend, cp.avg1, cp.avg7, cp.avg30, cp.low_foil, cp.trend_foil, " +
                "cp.avg1_foil, cp.avg7_foil, cp.avg30_foil, cp.updated_at " +
                "FROM user_collection uc " +
                "JOIN scryfall_card sc ON uc.card_id = sc.id " +
                "LEFT JOIN card_price cp ON cp.cardmarket_id = sc.cardmarket_id " +
                "WHERE user_id = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setLong(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ScryfallCardDTO cardDTO = new ScryfallCardDTO();
                cardDTO.setId(rs.getLong("card_id"));
                cardDTO.setScryfallId(rs.getString("scryfall_id"));
                cardDTO.setCardmarketId(rs.getLong("cardmarket_id"));
                cardDTO.setCardtraderId(rs.getLong("cardtrader_id"));
                cardDTO.setName(rs.getString("name"));
                cardDTO.setPrintedName(rs.getString("printed_name"));
                cardDTO.setLang(rs.getString("lang"));
                cardDTO.setImageUrl(rs.getString("image_url"));
                cardDTO.setRarity(rs.getString("rarity"));
                cardDTO.setSetName(rs.getString("set_name"));
                cardDTO.setCollectorNumber(rs.getString("collector_number"));
                cardDTO.setCardmarketURL(rs.getString("cardmarket_url"));
                cardDTO.setTypeLine(rs.getString("type_line"));
                cardDTO.setBorderColor(rs.getString("border_color"));
                cardDTO.setFrame(rs.getString("frame"));
                cardDTO.setReprint(rs.getBoolean("is_reprint"));
                cardDTO.setReleasedAt(rs.getDate("released_at") != null ? rs.getDate("released_at").toLocalDate() : null);

                CardPrice cardPrice = new CardPrice();
                cardPrice.setLow(rs.getBigDecimal("low"));
                cardPrice.setTrend(rs.getBigDecimal("trend"));
                cardPrice.setAvg1(rs.getBigDecimal("avg1"));
                cardPrice.setAvg7(rs.getBigDecimal("avg7"));
                cardPrice.setAvg30(rs.getBigDecimal("avg30"));
                cardPrice.setLowFoil(rs.getBigDecimal("low_foil"));
                cardPrice.setTrendFoil(rs.getBigDecimal("trend_foil"));
                cardPrice.setAvg1Foil(rs.getBigDecimal("avg1_foil"));
                cardPrice.setAvg7Foil(rs.getBigDecimal("avg7_foil"));
                cardPrice.setAvg30Foil(rs.getBigDecimal("avg30_foil"));
                if (rs.getTimestamp("updated_at") != null)
                    cardPrice.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

                cardDTO.setCardPrice(cardPrice);
                cardListDTO.add(cardDTO);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return cardListDTO;
    }
}