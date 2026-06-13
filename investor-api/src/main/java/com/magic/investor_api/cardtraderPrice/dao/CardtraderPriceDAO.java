package com.magic.investor_api.cardtraderPrice.dao;

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

    // Buscar carta mediante filtros en cardtrader_price
    public CardtraderPriceDTO selectFromCardtraderPriceCache(Long cardTraderId, String lang, String condition, boolean isFoil){

        String query = "SELECT avg, low, trend, avg1, avg7, avg30, fetched_at " +
                "FROM cardtrader_price " +
                "WHERE card_id = ? AND lang = ? AND condition = ? AND is_foil = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setLong(1, cardTraderId);
            stmt.setString(2, lang);
            stmt.setString(3, condition);
            stmt.setBoolean(4, isFoil);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                CardtraderPriceDTO dto = new CardtraderPriceDTO();

                dto.setId(rs.getLong("id"));
                dto.setCardId(rs.getLong("card_id"));
                dto.setLang(rs.getString("lang"));
                dto.setCondition(rs.getString("card_condition"));
                dto.setFoil(rs.getBoolean("is_foil"));
                dto.setAvg(rs.getBigDecimal("avg"));
                dto.setLow(rs.getBigDecimal("low"));
                dto.setTrend(rs.getBigDecimal("trend"));
                dto.setAvg1(rs.getBigDecimal("avg1"));
                dto.setAvg7(rs.getBigDecimal("avg7"));
                dto.setAvg30(rs.getBigDecimal("avg30"));
                dto.setFetchedAt(rs.getDate("fetched_at").toLocalDate());

                return dto;
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return null;
    }
}
