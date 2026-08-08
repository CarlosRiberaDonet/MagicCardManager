package com.magic.investor_api.cardtrader.dao;

import com.magic.investor_api.cardtrader.model.CardtraderCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CardtraderDAO {

    @Autowired
    private DataSource dataSource;

    // Insertar cartas de cardtrader en cardtrader_card
    public void insertCardtraderCards(List<CardtraderCard> cards) {

        String sql = """
        INSERT IGNORE INTO cardtrader_card (
            scryfall_id,
            cardmarket_id,
            cardtrader_id,
            name,
            rarity,
            expansion_id,
            set_name,
            set_code,
            collector_number
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (CardtraderCard card : cards) {

                stmt.setString(1, card.getScryfallId());
                stmt.setObject(2, card.getCardmarketId());
                stmt.setObject(3, card.getCardtraderId());
                stmt.setString(4, card.getName());
                stmt.setString(5, card.getRarity());
                stmt.setLong(6, card.getExpansionId());
                stmt.setString(7, card.getSetName());
                stmt.setString(8, card.getSetCode());
                stmt.setString(9, card.getCollectorNumber());

                stmt.addBatch();
            }

            stmt.executeBatch();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error insertando cartas de CardTrader",
                    e
            );
        }
    }

    // Añadir set_code y set_name a cardtrader_card relacionando expansion_id con tabla cardtrader_set
    public void mappingCardtraderSets(){
        String query = "UPDATE cardtrader_card ct " +
                "JOIN cardtrader_set cs ON cs.id = ct.expansion_id " +
                "SET ct.set_code = cs.code, ct.set_name = cs.name";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Entradas añadidas: " + filasAfectadas);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Obtener cardtrader_id de la tabla cardtrader_card
    public long selectCardTraderId(String scryfallId){

        String query = "SELECT cardtrader_id " +
                        "FROM cardtrader_card " +
                        "WHERE scryfall_id = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, scryfallId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getLong("cardtrader_id");
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return -1;
    }
}
