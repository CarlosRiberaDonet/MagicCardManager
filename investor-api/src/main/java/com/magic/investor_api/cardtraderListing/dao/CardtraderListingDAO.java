package com.magic.investor_api.cardtraderListing.dao;

import com.magic.investor_api.cardtraderListing.model.CardtraderListing;
import com.magic.investor_api.cardtraderPrice.model.CardtraderPrice;
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

    // Buscar cardtraderId en cardtrader_listing
    public CardtraderListing checkCardtraderIdOnCardtraderListing(Long cartraderId){

        String query = "SELECT * FROM cardtrader_listing " +
                "WHERE cardtrader_id = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setLong(1, cartraderId);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                CardtraderListing listing = new CardtraderListing();
                listing.setId(rs.getLong("id"));
                listing.setCardId(rs.getLong("card_id"));
                listing.setScryfallId(rs.getString("scryfall_id"));
                listing.setCardtraderId(rs.getLong("cardtrader_id"));
                listing.setPrice(rs.getBigDecimal("price"));
                listing.setCondition(rs.getString("card_condition"));
                listing.setLang(rs.getString("lang"));
                listing.setFoil(rs.getBoolean("is_foil"));
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return null;
    }

    // Obtener datos de cardtrader_listing
    public List<CardtraderPrice> getCardtraderListingValues(){

        List<CardtraderPrice> cardtraderPriceList = new ArrayList<>();

        String query = "SELECT cardtrader_id, lang, card_condition, is_foil, " +
                "MIN(price) AS low," +
                "AVG(price) AS avg " +
                "FROM cardtrader_listing " +
                "GROUP BY cardtrader_id, lang, card_condition, is_foil";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                CardtraderPrice cardPriceCache = new CardtraderPrice();

                cardPriceCache.setCardtraderId(rs.getLong("cardtrader_id"));
                cardPriceCache.setLang(rs.getString("lang"));
                cardPriceCache.setCondition(rs.getString("card_condition"));
                cardPriceCache.setFoil(rs.getBoolean("is_foil"));
                cardPriceCache.setAvg(rs.getBigDecimal("avg"));
                cardPriceCache.setLow(rs.getBigDecimal("low"));
                cardPriceCache.setFetchedAt(LocalDateTime.now());
                cardtraderPriceList.add(cardPriceCache);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return cardtraderPriceList;
    }
}
