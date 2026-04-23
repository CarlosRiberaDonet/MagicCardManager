package com.magic.investor_api.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class CardPriceDAO {

    @Autowired
    private DataSource dataSource;

    // En el DAO
    public void truncateCardPrice() {
        String sql = "TRUNCATE TABLE card_price";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.executeUpdate();
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
}
