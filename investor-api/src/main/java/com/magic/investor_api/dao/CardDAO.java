package com.magic.investor_api.dao;

import com.magic.investor_api.model.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class CardDAO {

    @Autowired
    private DataSource dataSource;

    public Card getCardByCardmarketId(Long cardmarketId) {
        String GET_CARD_BT_ID = "SELECT id, cardmarket_id FROM card WHERE cardmarket_id = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(GET_CARD_BT_ID)){

            stmt.setLong(1, cardmarketId);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    Card card = new Card();
                    card.setId(rs.getString("id"));
                    card.setCardmarketId(rs.getLong("cardmarket_id"));
                    return card;
                }
            }
        } catch (SQLException e){
            System.out.println("Error al recuperar la carta desde CardDAO getCardByCardmarketId");
        }
        return null;
    }
}