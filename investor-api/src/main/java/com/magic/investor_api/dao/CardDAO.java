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
import java.util.List;

@Repository
public class CardDAO {

    @Autowired
    private DataSource dataSource;

    // Obtener carta mediante el campo cardmarketId
    public Card getCardByCardmarketId(Long cardmarketId) {
        String SELECT_CARD_BY_ID = "SELECT id, cardmarket_id FROM card WHERE cardmarket_id = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_CARD_BY_ID)){

            stmt.setLong(1, cardmarketId);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    Card card = new Card();
                    card.setId(rs.getString("id"));
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

        String SELECT_CARD_BY_NAME = "SELECT c.id, c.cardmarket_id, c.name, c.lang, c.image_url, c.rarity, c.released_at, " +
                "c.set_code, c.set_name, c.collector_number, c.type_line, c.border_color, c.is_foil, c.is_reprint, c.cardmarket_url, " +
                "p.avg, p.low, p.trend, p.avg1, p.avg7, p.avg30, " +
                "p.avg_foil, p.low_foil, p.trend_foil, p.avg1_foil, p.avg7_foil, p.avg30_foil " +
                "FROM card c " +
                "LEFT JOIN card_price p ON c.id = p.card_id " +
                "WHERE LOWER (c.name) LIKE LOWER (?) LIMIT ? OFFSET ?";

        List<CardDTO> cardListDTO = new ArrayList<>();

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_CARD_BY_NAME)){

            stmt.setString(1, "%" + name + "%");
            stmt.setInt(2, size);
            stmt.setInt(3, (page-1) * size);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                CardDTO cardDTO = new CardDTO();
                cardDTO.setId(rs.getString("id"));
                cardDTO.setCardmarketId(rs.getLong("cardmarket_id"));
                cardDTO.setName(rs.getString("name"));
                cardDTO.setLang(rs.getString("lang"));
                cardDTO.setImageUrl(rs.getString("image_url"));
                cardDTO.setRarity(rs.getString("rarity"));
                cardDTO.setReleasedAt(rs.getDate("released_at").toLocalDate());
                cardDTO.setSetCode(rs.getString("set_code"));
                cardDTO.setSetName(rs.getString("set_name"));
                cardDTO.setCollectorNumber(rs.getString("collector_number"));
                cardDTO.setTypeLine(rs.getString("type_line"));
                cardDTO.setBorderColor(rs.getString("border_color"));
                cardDTO.setFoil(rs.getBoolean("is_foil"));
                cardDTO.setReprint(rs.getBoolean("is_reprint"));
                cardDTO.setCardmarketURL(rs.getString("cardmarket_url"));

                cardDTO.setAvg(rs.getBigDecimal("avg"));
                cardDTO.setLow(rs.getBigDecimal("low"));
                cardDTO.setTrend(rs.getBigDecimal("trend"));
                cardDTO.setAvg1(rs.getBigDecimal("avg1"));
                cardDTO.setAvg7(rs.getBigDecimal("avg7"));
                cardDTO.setAvg30(rs.getBigDecimal("avg30"));

                cardDTO.setAvgFoil(rs.getBigDecimal("avg_foil"));
                cardDTO.setLowFoil(rs.getBigDecimal("low_foil"));
                cardDTO.setTrendFoil(rs.getBigDecimal("trend_foil"));
                cardDTO.setAvg1Foil(rs.getBigDecimal("avg1_foil"));
                cardDTO.setAvg7Foil(rs.getBigDecimal("avg7_foil"));
                cardDTO.setAvg30Foil(rs.getBigDecimal("avg30_foil"));

                cardListDTO.add(cardDTO);
            }

        } catch(SQLException e){
            throw new RuntimeException(e);
        }

        return cardListDTO;
    }

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