package com.magic.investor_api.dao;

import com.magic.investor_api.dto.CardDTO;
import com.magic.investor_api.model.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class CardDAO {

    @Autowired
    private DataSource dataSource;

    // Obtener los campos id y cardmarket_id de la tabla card
    public Map<Long, Long> getAllCardsIds(){
        String SELECT_IDS = "SELECT id, cardmarket_id FROM card_variant";
        Map<Long, Long> cardMap = new HashMap<>(600000);

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_IDS)){

            stmt.setFetchSize(5000);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Long cardmarketId = rs.getLong("cardmarket_id");
                    if (rs.wasNull()) continue; // ignorar filas sin cardmarket_id
                    Long cardId = rs.getLong("id");
                    cardMap.put(cardmarketId, cardId);
                }
            }
        }catch(SQLException e){
            System.out.println("Error CardDAO getAllCards");
            throw new RuntimeException(e);
        }

        return cardMap;
    }

    // Obtener los campos id, cardmarket_id y scryfall_id de la tabla card
    public List<Card> getCardsIds(){
        String SELECT_IDS = "SELECT id, scryfall_id, cardmarket_id FROM card";
        List<Card> cardList = new ArrayList<>();

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_IDS)){

            stmt.setFetchSize(5000);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Card card = new Card();
                    card.setCardmarketId(rs.getLong("cardmarket_id"));
                    card.setId(rs.getLong("id"));
                    card.setScryfallId(rs.getString("scryfall_id"));

                    cardList.add(card);
                }
            }
        }catch(SQLException e){
            System.out.println("Error CardDAO getAllCards");
            throw new RuntimeException(e);
        }

        return cardList;
    }

    // Obtener carta mediante el campo cardmarketId
    public Card getCardByCardmarketId(Long cardmarketId) {
        String SELECT_CARD_BY_ID = "SELECT id, cardmarket_id FROM card WHERE cardmarket_id = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_CARD_BY_ID)){

            stmt.setLong(1, cardmarketId);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    Card card = new Card();
                    card.setId(rs.getLong("id"));
                    card.setCardmarketId(rs.getLong("cardmarket_id"));
                    return card;
                }
            }
        } catch (SQLException e){
            System.out.println("Error al recuperar la carta desde CardDAO getCardByCardmarketId");
            throw new RuntimeException(e);
        }
        return null;
    }

    // Obtener carta mediante su nombre
    public List<CardDTO> selectCardByName(String name, int page, int size, boolean foil) {

        String SELECT_CARD_BY_NAME =
                "SELECT cv.id, ca.name, ca.lang, ca.image_url, ca.rarity, " +
                        "ca.set_name, cv.collector_number, ca.cardmarket_url, " +
                        "pn.avg as avg, pn.low as low, " +
                        "pf.avg as avg_foil, pf.low as low_foil " +
                        "FROM card ca " +
                        "JOIN card_variant cv ON cv.card_id = ca.id " +
                        "LEFT JOIN card_price pn ON pn.card_variant_id = cv.id AND pn.foil = 0 " +
                        "LEFT JOIN card_price pf ON pf.card_variant_id = cv.id AND pf.foil = 1 " +
                        "WHERE (ca.name LIKE ? " +
                        "OR ca.printed_name LIKE ?) " +
                        "LIMIT ? OFFSET ?";

        List<CardDTO> cardListDTO = new ArrayList<>();

        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(SELECT_CARD_BY_NAME)){System.out.println("Buscando printed_name: [" + name + "%]");
            System.out.println("Bytes: " + Arrays.toString(name.getBytes()));

            stmt.setString(1, name + "%");
            stmt.setString(2, name + "%");
            stmt.setInt(3, size);
            stmt.setInt(4, (page - 1) * size);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                CardDTO cardDTO = new CardDTO();
                cardDTO.setName(rs.getString("name"));
                cardDTO.setLang(rs.getString("lang"));
                cardDTO.setImageUrl(rs.getString("image_url"));
                cardDTO.setRarity(rs.getString("rarity"));
                cardDTO.setSetName(rs.getString("set_name"));
                cardDTO.setCollectorNumber(rs.getString("collector_number"));
                cardDTO.setCardmarketURL(rs.getString("cardmarket_url"));
                cardDTO.setAvg(rs.getBigDecimal("avg"));
                cardDTO.setLow(rs.getBigDecimal("low"));
                cardDTO.setAvgFoil(rs.getBigDecimal("avg_foil"));
                cardDTO.setLowFoil(rs.getBigDecimal("low_foil"));
                cardListDTO.add(cardDTO);
            }

        } catch(SQLException e){
            throw new RuntimeException(e);
        }

        return cardListDTO;
    }

   // Obtener id y cardmarketId
   public Map<Long, Long> getCardmarketId(){
        Map<Long, Long> cardMap = new HashMap<>();
        String query = "SELECT id, cardmarket_id FROM card";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Long cardmarketId = rs.getLong("cardmarket_id");
                if(!rs.wasNull()){
                    Long id = rs.getLong("id");
                    cardMap.put(cardmarketId, id);
                }

            }
        } catch(SQLException e){
            throw new RuntimeException(e);
        }

        return cardMap;
   }

    // Obtener scryfallId y id de la tabla card
    public Map<String, Long> getScryfallId(){
        Map<String, Long> cardMap = new HashMap<>();
        String query = "SELECT id, scryfall_id FROM card";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                String scryfallId = rs.getString("scryfall_id");
                if(scryfallId != null && !scryfallId.trim().isEmpty()){
                    Long id = rs.getLong("id");
                    cardMap.put(scryfallId, id);
                }

            }
        } catch(SQLException e){
            throw new RuntimeException(e);
        }

        return cardMap;
    }

    // Actualizar cardmarket id en tabla card mediante scryfallUUID de la tabla card_variant
    public boolean updateCardmarketId(){
        String UPDATE_CARDMARKET_ID = "UPDATE card c " +
                "JOIN card_variant cv ON cv.cardmarket_id = c.cardmarket_id " +
                "SET c.cardmarket_id = cv.cardmarket_id " +
                "WHERE (c.cardmarket_id IS NULL OR c.cardmarket_id = 0) " +
                "AND cv.cardmarket_id IS NOT NULL AND cv.cardmarket_id > 0";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_CARDMARKET_ID)){

            int filasAfectadas = stmt.executeUpdate();

            if(filasAfectadas > 0){
                System.out.println("Cartas actualizadas en tabla card_variant: " + filasAfectadas);
                return true;
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    // Actualizar cardmarket id en tabla card mediante scryfallUUID de la tabla card_variant
    public boolean updateCardmarketIdUnmatched(){
        String UPDATE_CARDMARKET_ID_UNMATCHED = "UPDATE card c " +
                "JOIN card_variant_unmatched cvu ON cvu.scryfall_id = c.scryfall_id " +
                "SET c.cardmarket_id = cvu.cardmarket_id " +
                "WHERE (c.cardmarket_id IS NULL OR c.cardmarket_id = 0) " +
                "AND cvu.cardmarket_id IS NOT NULL AND cvu.cardmarket_id > 0";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_CARDMARKET_ID_UNMATCHED)){

            int filasAfectadas = stmt.executeUpdate();

            if(filasAfectadas > 0){
                System.out.println("Cartas actualizadas en tabla card_variant_unmatched: " + filasAfectadas);
                return true;
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public int countCardsByName(String name, boolean foil) {
        String COUNT_CARDS_BY_NAME =
                "SELECT COUNT(*) " +
                        "FROM card ca " +
                        "JOIN card_variant cv ON cv.card_id = ca.id " +
                        "JOIN card_price cp ON cp.card_variant_id = cv.id " +
                        "WHERE (ca.name LIKE ? COLLATE utf8mb4_unicode_ci " +
                        "OR ca.printed_name LIKE ? COLLATE utf8mb4_unicode_ci) " +
                        "AND cp.foil = ?";

        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(COUNT_CARDS_BY_NAME)){

            stmt.setString(1, name + "%");
            stmt.setString(2, name + "%");
            stmt.setBoolean(3, foil);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }

        } catch(SQLException e){
            throw new RuntimeException(e);
        }
        return -1;
    }
}