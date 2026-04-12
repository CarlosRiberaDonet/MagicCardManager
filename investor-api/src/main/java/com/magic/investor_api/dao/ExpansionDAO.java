package com.magic.investor_api.dao;

import com.magic.investor_api.model.Expansion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ExpansionDAO {

    @Autowired
    private DataSource dataSource;

    public void insertExpansion(List<Expansion> expansionList) {

        String INSERT_EXPANSION = "INSERT INTO card_trader_expansion VALUES (?, ?, ?)";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_EXPANSION)){

            for(Expansion e : expansionList){
                stmt.setLong(1, e.getId());
                stmt.setString(2, e.getCode());
                stmt.setString(3, e.getName());

                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }
}
