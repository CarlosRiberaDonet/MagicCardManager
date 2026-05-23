package com.magic.investor_api.dao;

import com.magic.investor_api.model.CardPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class CardPriceDAO {

    @Autowired
    private DataSource dataSource;

    // Borrar datos de la tabla card_price
    public void truncateCardPrice() {
        String sql = "TRUNCATE TABLE card_price";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.executeUpdate();
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    // Obtener precios de card_price
    public Map<Long, CardPrice.PricePair> getCardPriceMap(){

        Map<Long, CardPrice.PricePair> priceMap = new HashMap<>();
        String query = "SELECT cardmarket_id, avg, avg_foil FROM card_price";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Long cardmarketId = rs.getLong("cardmarket_id");

                CardPrice.PricePair price = new CardPrice.PricePair();
                price.setAvg(rs.getBigDecimal("avg"));
                price.setAvgFoil(rs.getBigDecimal("avg_foil"));

                priceMap.put(cardmarketId, price);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }

        return priceMap;
    }

}
