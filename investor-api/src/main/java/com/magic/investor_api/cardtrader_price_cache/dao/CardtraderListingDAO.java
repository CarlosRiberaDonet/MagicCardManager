package com.magic.investor_api.cardtrader_price_cache.dao;

import com.magic.investor_api.cardtrader_price_cache.model.CardtraderPriceCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CardtraderListingDAO {

    @Autowired
    private DataSource dataSource;

    // Obtener datos de cardtrader_listing
    public List<CardtraderPriceCache> getCardtraderListingValues(){

        List<CardtraderPriceCache> CardtraderPriceCacheList = new ArrayList<>();

        String query = "SELECT card_id, lang, card_condition, is_foil, " +
                "MIN(price) AS low," +
                "AVG(price) AS avg " +
                "FROM cardtrader_listing " +
                "GROUP BY card_id, lang, card_condition, is_foil";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                CardtraderPriceCache cardPriceCache = new CardtraderPriceCache();

                cardPriceCache.setCardId(rs.getLong("card_id"));
                cardPriceCache.setLang(rs.getString("lang"));
                cardPriceCache.setCondition(rs.getString("card_condition"));
                cardPriceCache.setFoil(rs.getBoolean("is_foil"));
                cardPriceCache.setAvg(rs.getBigDecimal("avg"));
                cardPriceCache.setLow(rs.getBigDecimal("low"));
                cardPriceCache.setFetchedAt(LocalDateTime.now());
                CardtraderPriceCacheList.add(cardPriceCache);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return CardtraderPriceCacheList;
    }
}
