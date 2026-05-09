package com.magic.investor_api.dao;

import com.magic.investor_api.model.Expansion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ExpansionDAO {

    @Autowired
    private DataSource dataSource;

    // Inserta las expansiones en la tabla card_trader_expansion
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

    // Obtiene los id de las expansiones de la tabla card_trader_expansion
    public List<Long> getExpansionListId(){

        String SELECT_EXPANSION = "SELECT id FROM card_trader_expansion";

        List<Long> expansionList = new ArrayList<>();

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_EXPANSION)){

            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Long expansionId = rs.getLong("id");
                expansionList.add(expansionId);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return expansionList;
    }

    // Obtener progreso de la tabla sync_progress
    // La utilizo como checkpoint para saber la última expansión de la que estoy descargando
    // las cartas de cardtrader
    public Long getLastExpansionId() {

        String sql = "SELECT last_expansion_id FROM sync_progress WHERE process_name = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "sync_progress");

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("last_expansion_id");
            }

            return 0L;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Actualiza el checkpoint de la tabla sync_progress
    public void updateLastExpansionId(Long id) {

        String sql = "UPDATE sync_progress SET last_expansion_id = ? WHERE process_name = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.setString(2, "sync_progress");

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Obtiene los nombres de las expansiones de la tabla card_trader_expansion
    public List<String> selectExpansionNamesList(){

        String SELECT_EXPANSION = "SELECT name FROM card_trader_expansion";

        List<String> expansionList = new ArrayList<>();

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_EXPANSION)){

            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                String expansionName = rs.getString("name");
                expansionList.add(expansionName);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        for(String e : expansionList){
            System.out.println(e);
        }
        return expansionList;
    }
}
