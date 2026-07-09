package com.magic.investor_api.cardtraderPrice.dao;

import com.magic.investor_api.cardtraderListing.model.CardtraderListing;
import com.magic.investor_api.cardtraderPrice.dto.CardtraderPriceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class CardtraderPriceDAO {

    @Autowired
    private DataSource dataSource;

    // Obtener precios de cardtrader_price
    public CardtraderPriceDTO selectPriceFromCardtraderPrice(CardtraderListing dto){

        String query = "SELECT id, avg, low, trend, avg1, avg7, avg30, fetched_at " +
                "FROM cardtrader_price " +
                "WHERE cardtrader_id = ? AND card_condition = ? AND lang = ?  AND is_foil = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setLong(1, dto.getCardtraderId());
            stmt.setString(2, dto.getCondition());
            stmt.setString(3, dto.getLang());
            stmt.setBoolean(4, dto.isFoil());

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                CardtraderPriceDTO dtoPrice = new CardtraderPriceDTO();
                dtoPrice.setId(rs.getLong("id"));
                dtoPrice.setAvg(rs.getBigDecimal("avg"));
                dtoPrice.setLow(rs.getBigDecimal("low"));
                dtoPrice.setTrend(rs.getBigDecimal("trend"));
                dtoPrice.setAvg1(rs.getBigDecimal("avg1"));
                dtoPrice.setAvg7(rs.getBigDecimal("avg7"));
                dtoPrice.setAvg30(rs.getBigDecimal("avg30"));
                dtoPrice.setFetchedAt(rs.getDate("fetched_at").toLocalDate());

                return dtoPrice;
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return null;
    }
}
