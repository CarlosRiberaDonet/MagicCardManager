package com.magic.investor_api.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class CardtraderDAO {

    @Autowired
    private DataSource dataSource;

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
}
