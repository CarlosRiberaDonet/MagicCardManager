package com.magic.investor_api.cardtrader_price_cache.dao;

import com.magic.investor_api.cardtrader_price_cache.CardtraderPriceCacheDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Repository
public class CardtraderPriceCacheDAO {

    @Autowired
    private DataSource dataSource;

    // Buscar carta mediante filtros en cardtrader_price_cache
    public CardtraderPriceCacheDTO selectFromCardtraderPriceCache(Long cardId, String lang, String condition, boolean isFoil){

        String query = "SELECT * FROM cardtrader_price_cache " +
                "WHERE card_id = ? " +
                "AND lang = ? " +
                "AND card_condition = ? " +
                "AND is_foil = ? ";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setLong(1, cardId);
            stmt.setString(2, lang);
            stmt.setString(3, condition);
            stmt.setBoolean(4, isFoil);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                CardtraderPriceCacheDTO dto = new CardtraderPriceCacheDTO();

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
