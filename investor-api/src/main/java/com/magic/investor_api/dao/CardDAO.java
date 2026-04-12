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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CardDAO {

    @Autowired
    private DataSource dataSource;

    // Obtener todas las cartas de la tabla card
    public Map<Long, Long> getAllCardsIds(){
        String SELECT_IDS = "SELECT id, cardmarket_id FROM card WHERE cardmarket_id IS NOT NULL AND cardmarket_id > 0";
        Map<Long, Long> cardMap = new HashMap<>(600000);

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_IDS)){

            stmt.setFetchSize(5000);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Suponiendo que tu ID de Scryfall es String o Long
                    Long cardId = rs.getLong("id");
                    Long cardmarketId = rs.getLong("cardmarket_id");
                    cardMap.put(cardmarketId, cardId);
                }
            }
        }catch(SQLException e){
            System.out.println("Error CardDAO getAllCards");
            throw new RuntimeException(e);
        }

        return cardMap;
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
    public List<CardDTO> selectCardByName(String name, int page, int size){

        String SELECT_CARD_BY_NAME = "SELECT c.id, c.name, c.lang, c.image_url, c.rarity, " +
                "c.set_name, c.collector_number, c.cardmarket_url, " +
                "p.low, p.trend " +
                "FROM card c " +
                "LEFT JOIN card_price p ON c.id = p.card_id " +
                "WHERE c.name LIKE ? " +
                "LIMIT ? OFFSET ?";

        List<CardDTO> cardListDTO = new ArrayList<>();

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_CARD_BY_NAME)){

            stmt.setString(1, "%" + name.toLowerCase() + "%");
            stmt.setInt(2, size);
            stmt.setInt(3, (page-1) * size);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                CardDTO cardDTO = new CardDTO();
                cardDTO.setId(rs.getLong("id"));
                cardDTO.setName(rs.getString("name"));
                cardDTO.setLang(rs.getString("lang"));
                cardDTO.setImageUrl(rs.getString("image_url"));
                cardDTO.setRarity(rs.getString("rarity"));
                cardDTO.setSetName(rs.getString("set_name"));
                cardDTO.setCollectorNumber(rs.getString("collector_number"));
                cardDTO.setCardmarketURL(rs.getString("cardmarket_url"));

                cardDTO.setLow(rs.getBigDecimal("low"));

                cardListDTO.add(cardDTO);
            }

        } catch(SQLException e){
            throw new RuntimeException(e);
        }

        return cardListDTO;
    }

   /* public CardDTO selectCardById(String id){

    }*/

    // Contar total de cartas mediante su nombre
    public int countCardsByName(String name){
        String COUNT_CARDS_BY_NAME = "SELECT COUNT(*) FROM card WHERE LOWER(name) LIKE LOWER(?)";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(COUNT_CARDS_BY_NAME)){

            stmt.setString(1, "%" + name + "%");
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