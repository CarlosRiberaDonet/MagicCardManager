package com.magic.investor_api.cardtraderPrice.dao;

import com.magic.investor_api.cardtraderListing.model.CardtraderListing;
import com.magic.investor_api.cardtraderPrice.dto.CardtraderPriceDTO;
import com.magic.investor_api.scryfall.dto.ScryfallCardDTO;
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
                dtoPrice.setUpdatedAt(rs.getDate("updated_at").toLocalDate());

                return dtoPrice;
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return null;
    }
}
