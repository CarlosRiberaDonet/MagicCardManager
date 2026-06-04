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
    public CardmarketPrice checkCardPrice(Long cardmarketId){

        CardmarketPrice cardmarketPrice = new CardmarketPrice();
        String query = "SELECT cardmarket_id, avg, low, trend, avg1, avg7, avg30 " +
                "FROM cardmarket_price " +
                "WHERE cardmarket_id = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setLong(1, cardmarketId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                cardmarketPrice.setCardmarketId(rs.getLong("cardmarket_id"));
                cardmarketPrice.setAvg(rs.getBigDecimal("avg"));
                cardmarketPrice.setLow(rs.getBigDecimal("low"));
                cardmarketPrice.setTrend(rs.getBigDecimal("trend"));
                cardmarketPrice.setAvg1(rs.getBigDecimal("avg1"));
                cardmarketPrice.setAvg7(rs.getBigDecimal("avg7"));
                cardmarketPrice.setAvg30(rs.getBigDecimal("avg30"));

                return cardmarketPrice;
            }

        }catch(SQLException e){
            throw new RuntimeException(e);
        }

        return null;
    }

}
