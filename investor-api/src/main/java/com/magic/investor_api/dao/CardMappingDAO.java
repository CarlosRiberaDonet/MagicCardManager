package com.magic.investor_api.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class CardMappingDAO {

    @Autowired
    private DataSource dataSource;

    // Inserto scryfall_id de scryfall_card en card_mapping
    public void insertScryfallId(){
        String query = "INSERT INTO card_mapping (scryfall_id) " +
                "SELECT scryfall_id FROM scryfall_card";
        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            int filasAfectadas = stmt.executeUpdate();
            if(filasAfectadas > 0){
                System.out.println("Entradas añadidas: " + filasAfectadas);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    // Enriquecer cardtrader_card desde scryfall_card
    public void updateCardmarketIdOnCardtraderCard(){

        String query = "UPDATE cardtrader_card ct " +
                "JOIN scryfall_card sc " +
                "ON sc.scryfall_id = ct.scryfall_id " +
                "SET ct.cardmarket_id = COALESCE(ct.cardmarket_id, sc.cardmarket_id), " +
                "ct.cardtrader_id = COALESCE(ct.cardtrader_id, sc.cardtrader_id);";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Entradas añadidas: " + filasAfectadas);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Enriquecer scryfall_card desde cardtrader_card
    public void updateCardmarketIdOnScryfallCard(){

        String query = "UPDATE scryfall_card sc " +
                "JOIN cardtrader_card ct " +
                "ON ct.scryfall_id = sc.scryfall_id " +
                "SET sc.cardtrader_id = COALESCE(sc.cardtrader_id, ct.cardtrader_id), " +
                "sc.cardmarket_id = COALESCE(sc.cardmarket_id, ct.cardmarket_id);";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Entradas añadidas: " + filasAfectadas);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Relaciono e inserto cardmarket_id de scryfall_card en card_mapping
    public void mappingCardmarketId() {
        String query = "UPDATE card_mapping cm " +
                "JOIN scryfall_card sc " +
                "ON sc.scryfall_id = cm.scryfall_id " +
                "SET cm.cardmarket_id = COALESCE(cm.cardmarket_id, sc.cardmarket_id), " +
                "cm.cardtrader_id = COALESCE(cm.cardtrader_id, sc.cardtrader_id);";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Entradas añadidas: " + filasAfectadas);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Relaciono e yyinserto cardmarket_id de cardtrader_card en card_mapping
    public void mappingCardtraderId() {
        String query = "UPDATE card_mapping cm " +
                "JOIN cardtrader_card ct " +
                "ON ct.scryfall_id = cm.scryfall_id " +
                "SET cm.cardmarket_id = COALESCE(cm.cardmarket_id, ct.cardmarket_id), " +
                "cm.cardtrader_id = COALESCE(cm.cardtrader_id, ct.cardtrader_id);";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Entradas añadidas: " + filasAfectadas);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // cierre por CardMarket puente (CRÍTICO)
    public void lastJoin1() {
        String query = "UPDATE cardtrader_card ct " +
                "JOIN scryfall_card sc " +
                "ON ct.cardmarket_id = sc.cardmarket_id " +
                "SET ct.scryfall_id = COALESCE(ct.scryfall_id, sc.scryfall_id) " +
                "WHERE ct.cardmarket_id IS NOT NULL;";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Entradas añadidas: " + filasAfectadas);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // cierre por CardMarket puente (CRÍTICO)
    public void lastJoin2() {
        String query = "UPDATE scryfall_card sc " +
                "JOIN cardtrader_card ct " +
                "  ON ct.cardmarket_id = sc.cardmarket_id " +
                "SET sc.scryfall_id = COALESCE(sc.scryfall_id, ct.scryfall_id) " +
                "WHERE sc.cardmarket_id IS NOT NULL;";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Entradas añadidas: " + filasAfectadas);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
