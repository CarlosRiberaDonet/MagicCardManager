package com.magic.investor_api.cardmapping.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class CardMappingDAO {

    @Autowired
    private DataSource dataSource;

    // Inserto cardtraderId, cardmarket_id y scryfall_id de cardtrader_card en card_mapping
    public void insertCardmarketIdAndCardtraderIdOnCardMapping(){
        String query = "INSERT INTO card_mapping (cardtrader_id) " +
                "SELECT cardtrader_id " +
                "FROM cardtrader_card";
        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            int filasAfectadas = stmt.executeUpdate();
            if(filasAfectadas > 0){
                System.out.println("Entradas añadidas: " + filasAfectadas);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    // Mapeo scryfall_id cardtrader_card en card_mapping
    public void updateScryfallIdOnCardMapping(){
        String query =
                "UPDATE card_mapping cm " +
                        "JOIN cardtrader_card ct " +
                        "ON cm.cardtrader_id = ct.cardtrader_id " +
                        "SET cm.scryfall_id = ct.scryfall_id " +
                        "WHERE ct.scryfall_id IS NOT NULL ";
        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            int filasAfectadas = stmt.executeUpdate();
            if(filasAfectadas > 0){
                System.out.println("Entradas añadidas: " + filasAfectadas);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    // Mapeo scryfall_id cardtrader_card en card_mapping
    public void updateCardmarketIdOnCardMapping(){
        String query =
                "UPDATE card_mapping cm " +
                        "JOIN cardtrader_card ct " +
                        "ON cm.cardtrader_id = ct.cardtrader_id " +
                        "SET cm.cardmarket_id = ct.cardmarket_id " +
                        "WHERE ct.cardmarket_id IS NOT NULL";
        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            int filasAfectadas = stmt.executeUpdate();
            if(filasAfectadas > 0){
                System.out.println("Entradas añadidas: " + filasAfectadas);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    // Obtiene cardmarket_id y cardtrader_id asociado a scryfall_id
    public Long[] getCardmarketAndCardtraderId(String scryfallId){

        Long[] cardIds = new Long[2];
        String query = "SELECT cardmarket_id, cardtrader_id " +
                "FROM card_mapping " +
                "WHERE scryfall_id = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1, scryfallId);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){

                cardIds[0] = rs.getLong("cardmarket_id");
                cardIds[1] = rs.getLong("cardtrader_id");
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return cardIds;
    }
}
