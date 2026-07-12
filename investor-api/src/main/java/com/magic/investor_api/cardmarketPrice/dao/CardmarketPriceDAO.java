package com.magic.investor_api.cardmarketPrice.dao;

import com.magic.investor_api.cardmarketPrice.model.CardmarketPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class CardmarketPriceDAO {

    @Autowired
    private DataSource dataSource;

    // Borrar datos de la tabla card_price
    public void truncateCardPrice() {
        String sql = "TRUNCATE TABLE cardmarket_price";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.executeUpdate();
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    // Obtener precios de card_price
    public CardmarketPrice selectCardmarketPrice(Long cardmarketId){

        CardmarketPrice cardmarketPrice = new CardmarketPrice();
        String query = "SELECT id, cardmarket_id, avg, low, trend, avg1, avg7, avg30, " +
                "avg_foil, low_foil, trend_foil, avg1_foil, avg7_foil, avg30_foil, updated_at " +
                "FROM cardmarket_price " +
                "WHERE cardmarket_id = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setLong(1, cardmarketId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                cardmarketPrice.setId(rs.getLong("id"));
                cardmarketPrice.setCardmarketId(rs.getLong("cardmarket_id"));
                cardmarketPrice.setAvg(rs.getBigDecimal("avg"));
                cardmarketPrice.setLow(rs.getBigDecimal("low"));
                cardmarketPrice.setTrend(rs.getBigDecimal("trend"));
                cardmarketPrice.setAvg1(rs.getBigDecimal("avg1"));
                cardmarketPrice.setAvg7(rs.getBigDecimal("avg7"));
                cardmarketPrice.setAvg30(rs.getBigDecimal("avg30"));

                cardmarketPrice.setAvgFoil(rs.getBigDecimal("avg_foil"));
                cardmarketPrice.setLowFoil(rs.getBigDecimal("low_foil"));
                cardmarketPrice.setTrendFoil(rs.getBigDecimal("trend_foil"));
                cardmarketPrice.setAvg1Foil(rs.getBigDecimal("avg1_foil"));
                cardmarketPrice.setAvg7Foil(rs.getBigDecimal("avg7_foil"));
                cardmarketPrice.setAvg30Foil(rs.getBigDecimal("avg30_foil"));

                cardmarketPrice.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

                return cardmarketPrice;
            }

        }catch(SQLException e){
            throw new RuntimeException(e);
        }

        return null;
    }
}
