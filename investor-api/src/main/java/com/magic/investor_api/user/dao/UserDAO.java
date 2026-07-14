package com.magic.investor_api.user.dao;

import com.magic.investor_api.cardtrader.dao.CardtraderDAO;
import com.magic.investor_api.cardtraderListing.model.CardtraderListing;
import com.magic.investor_api.cardtraderPrice.dao.CardtraderPriceDAO;
import com.magic.investor_api.cardtraderPrice.dto.CardtraderPriceDTO;
import com.magic.investor_api.scryfall.dto.ScryfallCardDTO;
import com.magic.investor_api.cardmarketPrice.model.CardmarketPrice;
import com.magic.investor_api.user.dto.UserCollectionDTO;
import com.magic.investor_api.user.dto.UserWatchlistDTO;
import com.magic.investor_api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository

public class UserDAO {


    @Autowired
    private DataSource dataSource;
    private PasswordEncoder passwordEncoder;
    private final CardtraderDAO cardtraderDAO;
    private final CardtraderPriceDAO cardtraderPriceDAO;

    public UserDAO(CardtraderDAO cardtraderDAO, CardtraderPriceDAO cardtraderPriceDAO){
        this.cardtraderDAO = cardtraderDAO;
        this.cardtraderPriceDAO = cardtraderPriceDAO;
    }

    // Insertar carta en la tabla user_collection
    public boolean insertCollectionCard(UserCollectionDTO dto){

        // Compruebo si la carta ya está en la colección del usuario
        if (selectCollectionCardQuantity(dto) > 0) {
            System.out.println("La carta ya está en la colección " );
            return updateQuantityCollection(dto, +1); // Sumo +1 a la cantidad de la carta
        }

        String query = "INSERT INTO user_collection (user_id, card_id, purchase_price, lang, " +
                "quantity, card_condition, is_foil) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?)";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setLong(1, dto.getUserId());
            stmt.setLong(2, dto.getCardId());
            stmt.setDouble(3, dto.getPurchasePrice());
            stmt.setString(4,dto.getLang());
            stmt.setInt(5, 1);
            stmt.setString(6, dto.getCondition());
            stmt.setBoolean(7, dto.isFoil());

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
        int quantity = selectCollectionCardQuantity(dto);
        // Si quantity > 1
        if(quantity > 1){
            updateQuantityCollection(dto, -1); // resto -1 a quantity
            return true;
        }
        else if(quantity == 1) { // Si quantity == 1, elimino la fila
            String query = "DELETE FROM user_collection WHERE user_id = ? AND card_id = ? AND purchase_price = ? " +
                    "AND card_condition = ? AND lang = ? AND is_foil = ?";
            try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setLong(1, dto.getUserId());
                stmt.setLong(2, dto.getCardId());
                stmt.setDouble(3, dto.getPurchasePrice());
                stmt.setString(4, dto.getCondition());
                stmt.setString(5, dto.getLang());
                stmt.setBoolean(6, dto.isFoil());

                int filasAfectadas = stmt.executeUpdate();
                return filasAfectadas > 0;
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    // Insertar carta en la tabla user_watchlist
    public boolean insertWatchlistCard(UserWatchlistDTO dto){

        String query = "INSERT INTO user_watchlist (user_id, card_id, last_price, card_condition, is_foil) " +
                " VALUES (?, ?, ?, ?, ?)";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setLong(1, dto.getUserId());
            stmt.setLong(2, dto.getCardId());
            stmt.setDouble(3, dto.getLastPrice());
            stmt.setString(4, dto.getCondition());
            stmt.setBoolean(5, dto.isFoil());

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
    public boolean deleteWatchlistCard(UserWatchlistDTO dto){

        String query = "DELETE FROM user_watchlist WHERE user_id = ? AND card_id = ? AND last_price = ? " +
                "AND card_condition = ? AND is_foil = ?";
        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, dto.getUserId());
            stmt.setLong(2, dto.getCardId());
            stmt.setDouble(3, dto.getLastPrice());
            stmt.setString(4, dto.getCondition());
            stmt.setBoolean(5, dto.isFoil());

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    // Obtener cantidad de una carta en user_collection
    public int selectCollectionCardQuantity(UserCollectionDTO dto){
        int total = 0;
        String query = "SELECT quantity FROM user_collection WHERE user_id = ? AND card_id = ? " +
                "AND purchase_price = ? AND lang = ? AND card_condition = ? AND is_foil = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setLong(1, dto.getUserId());
            stmt.setLong(2, dto.getCardId());
            stmt.setDouble(3, dto.getPurchasePrice());
            stmt.setString(4, dto.getLang());
            stmt.setString(5, dto.getCondition());
            stmt.setBoolean(6, dto.isFoil());

            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                total += rs.getInt("quantity");
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return total;
    }

    // Obtener cantidad de una carta en user_collection (diferenciando is_foil)
    public int selectCollectionCardTotal(UserCollectionDTO dto){
        String query = "SELECT SUM(quantity) FROM user_collection WHERE user_id = ? AND card_id = ? " +
                "AND card_condition = ? AND lang = ? AND is_foil = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, dto.getUserId());
            stmt.setLong(2, dto.getCardId());
            stmt.setString(3, dto.getCondition());
            stmt.setString(4, dto.getLang());
            stmt.setBoolean(5, dto.isFoil());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int total = rs.getInt(1);
                return total;
            }
            return 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // Comprobar si la carta ya está en user_watchlist
    public boolean selectWatchlistCardId(UserWatchlistDTO dto){
        String query = "SELECT 1 FROM user_watchlist WHERE user_id = ? AND card_id = ? " +
                "AND card_condition = ? AND is_foil = ?";
        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setLong(1, dto.getUserId());
            stmt.setLong(2, dto.getCardId());
            stmt.setString(3, dto.getCondition());
            stmt.setBoolean(4, dto.isFoil());
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
    public boolean updateQuantityCollection(UserCollectionDTO dto, int quantity){
        String query = "UPDATE user_collection SET quantity = quantity + ? WHERE user_id = ? AND card_id = ? " +
                "AND purchase_price = ? AND card_condition = ? AND is_foil = ?";
        
        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, quantity);
            stmt.setLong(2, dto.getUserId());
            stmt.setLong(3, dto.getCardId());
            stmt.setDouble(4, dto.getPurchasePrice());
            stmt.setString(5, dto.getCondition());
            stmt.setBoolean(6, dto.isFoil());

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
    public List<UserWatchlistDTO> selectMyWatchlist(Long userId){

        List<UserWatchlistDTO> userWatchlistDTOList = new ArrayList<>();
        String query = "SELECT wl.user_id, wl.card_id, wl.last_price, wl.card_condition, wl.is_foil, wl.added_at, " +
                "sc.name, sc.printed_name, sc.lang, sc.image_url, sc.rarity, sc.set_name, sc.set_code, sc.collector_number, " +
                "s.set_code, s.icon_svg_uri, " +
                "cm.low, cm.trend, cm.low_foil, cm.trend_foil " +
                "FROM user_watchlist wl " +
                "JOIN scryfall_card sc ON wl.card_id = sc.id " +
                "JOIN scryfall_set s ON sc.set_code = s.set_code " +
                "LEFT JOIN cardmarket_price cm ON cm.cardmarket_id = sc.cardmarket_id " +
                "WHERE user_id= ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                CardmarketPrice cardmarketPrice = new CardmarketPrice();
                cardmarketPrice.setLow(rs.getBigDecimal("low"));
                cardmarketPrice.setTrend(rs.getBigDecimal("trend"));
                cardmarketPrice.setLowFoil(rs.getBigDecimal("low_foil"));
                cardmarketPrice.setTrendFoil(rs.getBigDecimal("trend_foil"));

                UserWatchlistDTO dto = new UserWatchlistDTO();
                dto.setUserId(rs.getLong("user_id"));
                dto.setCardId(rs.getLong("card_id"));
                dto.setLastPrice(rs.getDouble("last_price"));
                dto.setCondition(rs.getString("card_condition"));
                dto.setFoil(rs.getBoolean("is_foil"));
                dto.setAddedAt(rs.getDate("added_at").toLocalDate().atStartOfDay().toLocalDate());

                ScryfallCardDTO card = new ScryfallCardDTO();
                card.setName(rs.getString("name"));
                card.setPrintedName(rs.getString("printed_name"));
                card.setLang(rs.getString("lang"));
                card.setImageUrl(rs.getString("image_url"));
                card.setRarity(rs.getString("rarity"));
                card.setSetName(rs.getString("set_name"));
                card.setScryfallId(rs.getString("set_code"));
                card.setIconSvgUri(rs.getString("icon_svg_uri"));
                card.setCollectorNumber(rs.getString("collector_number"));
                card.setCardPrice(cardmarketPrice);

                if(!dto.getCondition().equals("NM") || card.getCardPrice() == null){
                    CardtraderListing listing = new CardtraderListing();
                    listing.setCardId(dto.getCardId());
                    listing.setCondition(dto.getCondition());
                    listing.setFoil(dto.isFoil());
                }

                dto.setScryfallCardDTO(card);

                userWatchlistDTOList.add(dto);
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }

        for(UserWatchlistDTO d : userWatchlistDTOList){
            System.out.println(d.toString());
        }
        return userWatchlistDTOList;
    }

    // Obtener todas las cartas de la colección del usuario en user_collection
    public List<UserCollectionDTO> selectMyCollection(Long userId){

        List<UserCollectionDTO> userCollectionDTO = new ArrayList<>();

        String query = "SELECT uc.user_id, uc.card_id, uc.purchase_price, uc.quantity, uc.card_condition, uc.is_foil, uc.lang, uc.added_at, " +
                "sc.id, sc.scryfall_id, sc.cardmarket_id, sc.name, sc.printed_name, sc.lang AS card_lang, sc.image_url, sc.rarity, sc.set_name, " +
                "sc.set_code, sc.collector_number, sc.is_foil," +
                "s.icon_svg_uri, " +
                "cp.avg, cp.low, cp.trend, cp.avg_foil, cp.low_foil, cp.trend_foil, updated_at " +
                "FROM user_collection uc " +
                "JOIN scryfall_card sc ON uc.card_id = sc.id " +
                "LEFT JOIN scryfall_set s ON sc.set_code = s.set_code " +
                "LEFT JOIN cardmarket_price cp ON cp.cardmarket_id = sc.cardmarket_id " +
                "WHERE uc.user_id = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setLong(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UserCollectionDTO collectionDTO = new UserCollectionDTO();
                ScryfallCardDTO scryfallCardDTO = new ScryfallCardDTO();
                CardmarketPrice cardmarketPrice = new CardmarketPrice();

                collectionDTO.setUserId(rs.getLong("user_id"));
                collectionDTO.setCardId(rs.getLong("card_id"));
                collectionDTO.setPurchasePrice(rs.getDouble("purchase_price"));
                collectionDTO.setQuantity(rs.getInt("quantity"));
                collectionDTO.setCondition(rs.getString("card_condition"));
                collectionDTO.setFoil(rs.getBoolean("is_foil"));
                collectionDTO.setLang(rs.getString("lang"));
                collectionDTO.setAddedAt(rs.getDate("added_at").toLocalDate());
                collectionDTO.setCard(scryfallCardDTO);

                scryfallCardDTO.setId(rs.getLong("card_id"));
                scryfallCardDTO.setScryfallId(rs.getString("scryfall_id"));
                scryfallCardDTO.setName(rs.getString("name"));
                scryfallCardDTO.setPrintedName(rs.getString("printed_name"));
                scryfallCardDTO.setLang(rs.getString("card_lang"));
                scryfallCardDTO.setImageUrl(rs.getString("image_url"));
                scryfallCardDTO.setRarity(rs.getString("rarity"));
                scryfallCardDTO.setSetName(rs.getString("set_name"));
                scryfallCardDTO.setCollectorNumber(rs.getString("collector_number"));
                scryfallCardDTO.setCardPrice(cardmarketPrice);
                scryfallCardDTO.setIconSvgUri(rs.getString("icon_svg_uri"));

                cardmarketPrice.setLow(rs.getBigDecimal("low"));
                cardmarketPrice.setTrend(rs.getBigDecimal("trend"));
                cardmarketPrice.setLowFoil(rs.getBigDecimal("low_foil"));
                cardmarketPrice.setTrendFoil(rs.getBigDecimal("trend_foil"));
                Timestamp ts = rs.getTimestamp("updated_at");
                cardmarketPrice.setUpdatedAt(ts != null ? ts.toLocalDateTime() : null);

                // Si el campo updated_at es null, la carta no tiene precio en la tabla cardmarket_price
                // Trato de obtener los precios de cardtrader_price
                if(cardmarketPrice.getUpdatedAt() == null){
                    // Obtengo cardtraderId
                    Long cardTraderId = cardtraderDAO.selectCardTraderId(scryfallCardDTO.getScryfallId());
                    System.out.println("cardtraderId obtenido: " + cardTraderId);
                    // Si existe cardtraderId
                    if(cardTraderId > 0) {
                        // Creo objeto CardTraderListing
                        ScryfallCardDTO dto = new ScryfallCardDTO();
                        dto.setCardTraderId(cardTraderId);
                        // Utilizo ENUM para equiparar el valor del campo condition recibido con el de la tabla de la BD
                        dto.setCondition(Utils.CardCondition.valueOf(collectionDTO.getCondition()).getCardTraderValue());
                        dto.setLang(collectionDTO.getLang());
                        dto.setFoil(collectionDTO.isFoil());

                        // Trato de obtener precios de cardtrader_price
                        CardtraderPriceDTO cardtraderPrice = cardtraderPriceDAO.selectPriceFromCardtraderPrice(dto);
                        if(cardtraderPrice != null){
                            scryfallCardDTO.setCardPrice(cardtraderPrice);
                            scryfallCardDTO.setPriceSource("CARDTRADER");
                        }
                    }
                }else{
                    scryfallCardDTO.setPriceSource("CARDMARKET");
                }
                System.out.println(collectionDTO);

                userCollectionDTO.add(collectionDTO);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return userCollectionDTO;
    }
}