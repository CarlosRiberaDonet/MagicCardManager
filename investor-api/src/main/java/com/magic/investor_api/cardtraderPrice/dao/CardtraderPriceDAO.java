package com.magic.investor_api.cardtraderPrice.dao;

import com.magic.investor_api.cardtraderPrice.dto.CardtraderPriceDTO;
import com.magic.investor_api.cardtraderPrice.model.CardtraderPrice;
import com.magic.investor_api.scryfall.dto.ScryfallCardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Repository
public class CardtraderPriceDAO {

    @Autowired
    private DataSource dataSource;

    // Insertar o actualizar precios en cardtrader_price
    public void insertCardtraderPrice(List<CardtraderPrice> cardPriceList) {

        String sql = """
        INSERT INTO cardtrader_price (
            card_id,
            cardtrader_id,
            scryfall_id,
            lang,
            card_condition,
            is_foil,
            avg,
            low,
            trend,
            avg1,
            avg7,
            avg30,
            updated_at
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            cardtrader_id = VALUES(cardtrader_id),
            scryfall_id = VALUES(scryfall_id),
            avg = VALUES(avg),
            low = VALUES(low),
            trend = VALUES(trend),
            avg1 = VALUES(avg1),
            avg7 = VALUES(avg7),
            avg30 = VALUES(avg30),
            updated_at = VALUES(updated_at)
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (CardtraderPrice p : cardPriceList) {

                stmt.setLong(1, p.getCardId());
                stmt.setLong(2, p.getCardtraderId());
                stmt.setString(3, p.getScryfallId());
                stmt.setString(4, p.getLang());
                stmt.setString(5, p.getCondition());
                stmt.setBoolean(6, p.isFoil());
                stmt.setBigDecimal(7, p.getAvg());
                stmt.setBigDecimal(8, p.getLow());
                stmt.setBigDecimal(9, p.getTrend());
                stmt.setBigDecimal(10, p.getAvg1());
                stmt.setBigDecimal(11, p.getAvg7());
                stmt.setBigDecimal(12, p.getAvg30());
                stmt.setTimestamp(13, Timestamp.valueOf(p.getUpdatedAt()));

                stmt.addBatch();
            }

            stmt.executeBatch();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Obtener precios de cardtrader_price
    public CardtraderPriceDTO selectPriceFromCardtraderPrice(ScryfallCardDTO dto){

        String query = "SELECT card_id, cardtrader_id, lang, card_condition, is_foil, avg, low, trend, avg1, avg7, avg30, updated_at " +
                "FROM cardtrader_price " +
                "WHERE cardtrader_id = ? AND card_condition = ? AND lang = ?  AND is_foil = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setLong(1, dto.getCardTraderId());
            stmt.setString(2, dto.getCondition());
            stmt.setString(3, dto.getLang());
            stmt.setBoolean(4, dto.isFoil());

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                CardtraderPriceDTO dtoPrice = new CardtraderPriceDTO();
                dtoPrice.setCardId(rs.getLong("card_id"));
                dtoPrice.setCardtraderId(rs.getLong("cardtrader_id"));
                dtoPrice.setLang(rs.getString("lang"));
                dtoPrice.setCondition(rs.getString("card_condition"));
                dtoPrice.setFoil(rs.getBoolean("is_foil"));
                dtoPrice.setAvg(rs.getBigDecimal("avg"));
                dtoPrice.setLow(rs.getBigDecimal("low"));
                dtoPrice.setTrend(rs.getBigDecimal("trend"));
                dtoPrice.setAvg1(rs.getBigDecimal("avg1"));
                dtoPrice.setAvg7(rs.getBigDecimal("avg7"));
                dtoPrice.setAvg30(rs.getBigDecimal("avg30"));

                java.sql.Timestamp timestamp = rs.getTimestamp("updated_at");
                dtoPrice.setUpdatedAt(timestamp.toLocalDateTime());
                return dtoPrice;
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return null;
    }
}
