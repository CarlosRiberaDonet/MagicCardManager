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
        String query = "INSERT INTO card_mapping (scryfall_id, cardtrader_id) " +
                "SELECT" +
                "    scryfall_id, " +
                "    MIN(cardtrader_id) " +
                "FROM cardtrader_card " +
                "WHERE scryfall_id IS NOT NULL AND scryfall_id <> '' " +
                "GROUP BY scryfall_id;";
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
    public Long getCardtraderIdFromCardtraderCard(String scryfallId){

        long cardTraderId = 0;

        String query = "SELECT cardtrader_id " +
                "FROM cardtrader_card " +
                "WHERE scryfall_id = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1, scryfallId);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                cardTraderId = rs.getLong("cardtrader_id");
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return cardTraderId;
    }
}
