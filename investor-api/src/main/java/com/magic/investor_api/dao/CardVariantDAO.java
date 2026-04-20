package com.magic.investor_api.dao;

import com.magic.investor_api.model.Card;
import com.magic.investor_api.model.CardVariant;
import com.magic.investor_api.model.CardVariantUnmatched;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CardVariantDAO {

    @Autowired
    private DataSource dataSource;

    // Insertar carta en la tabla card_variant
    public void insertCardVariant(List<CardVariant> cardVarians){

        String INSERT_CARD_VARIANT = "INSERT IGNORE INTO card_variant(card_id, cardtrader_id, " +
                "cardmarket_id, scryfall_id, expansion_id, " +
                "version, collector_number) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_CARD_VARIANT)){

            for(CardVariant c : cardVarians){
                stmt.setLong(1, c.getCard().getId()); // cardId de la BD
                stmt.setObject(2, c.getCardtraderId()); // cardtraderId
                stmt.setObject(3, c.getCardmarketId()); // carmaketId
                stmt.setObject(4, c.getScryfallId()); // scryfall UUID
                stmt.setObject(5, c.getExpansionId()); // Código expansión cardtrader
                stmt.setObject(6, c.getVersion());
                stmt.setObject(7,c.getCollectorNumber());

                stmt.addBatch();
            }

            stmt.executeBatch();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Insertar carta en la tabla card_variant_unmatched
    public void insertCardVarianUnmatched(List<CardVariantUnmatched> cardVariansUnmatched) {

        String INSERT_CARD_VARIANT_UNMATCHED = "INSERT IGNORE INTO card_variant_unmatched(name, expansion_id, collector_number, cardtrader_id, cardmarket_id, scryfall_id) " +
                "VALUES(?, ?, ?, ?, ?, ?)";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_CARD_VARIANT_UNMATCHED)){
            for(CardVariantUnmatched c : cardVariansUnmatched){
                stmt.setString(1, c.getName());
                stmt.setLong(2, c.getExpansionId());
                stmt.setString(3, c.getCollectorNumber());
                stmt.setLong(4, c.getCardtraderId());
                stmt.setObject(5, c.getCardmarketId()); // setObject por si es null
                stmt.setString(6, c.getScryfallId());

                stmt.addBatch();
            }

            stmt.executeBatch();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
