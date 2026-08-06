package com.magic.investor_api.cardtraderListing.dao;

import com.magic.investor_api.cardtraderListing.model.CardtraderListing;
import com.magic.investor_api.cardtraderPrice.model.CardtraderPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CardtraderListingDAO {

    @Autowired
    private DataSource dataSource;

    // Insertar o actualizar precios en cardtrader_listing
    public void insertCardtraderListingPrices(List<CardtraderListing> listing) {
        String sql = """
        INSERT INTO cardtrader_listing (
            card_id,
            scryfall_id,
            cardtrader_id,
            price,
            card_condition,
            lang,
            is_foil,
            url,
            fetched_at
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            scryfall_id = VALUES(scryfall_id),
            cardtrader_id = VALUES(cardtrader_id),
            price = VALUES(price),
            card_condition = VALUES(card_condition),
            lang = VALUES(lang),
            is_foil = VALUES(is_foil),
            url = VALUES(url),
            fetched_at = VALUES(fetched_at)
        """;

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            for(CardtraderListing l : listing){
                stmt.setLong(1, l.getCardId());
                stmt.setString(2, l.getScryfallId());
                stmt.setLong(3, l.getCardtraderId());
                stmt.setBigDecimal(4, l.getPrice());
                stmt.setString(5, l.getCondition());
                stmt.setString(6, l.getLang());
                stmt.setBoolean(7, l.isFoil());
                stmt.setString(8, l.getUrl());
                stmt.setTimestamp(9, Timestamp.valueOf(l.getFetchedAt()));

                stmt.executeUpdate();
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

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
    public List<CardtraderPrice> getCardtraderListingValues(Long cardId){

        List<CardtraderPrice> cardtraderPriceList = new ArrayList<>();

        String query = "SELECT card_id, cardtrader_id, lang, card_condition, is_foil, " +
                "MIN(price) AS low," +
                "AVG(price) AS avg " +
                "FROM cardtrader_listing " +
                "WHERE card_id = ? " +
                "GROUP BY cardtrader_id, lang, card_condition, is_foil";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setLong(1, cardId);

            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                CardtraderPrice cardPriceCache = new CardtraderPrice();

                cardPriceCache.setCardId(cardId);
                cardPriceCache.setCardtraderId(rs.getLong("cardtrader_id"));
                cardPriceCache.setLang(rs.getString("lang"));
                cardPriceCache.setCondition(rs.getString("card_condition"));
                cardPriceCache.setFoil(rs.getBoolean("is_foil"));
                cardPriceCache.setAvg(rs.getBigDecimal("avg"));
                cardPriceCache.setLow(rs.getBigDecimal("low"));
                cardPriceCache.setUpdatedAt(LocalDateTime.now());
                cardtraderPriceList.add(cardPriceCache);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return cardtraderPriceList;
    }
}
