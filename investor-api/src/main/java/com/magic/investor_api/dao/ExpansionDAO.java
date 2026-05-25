package com.magic.investor_api.dao;

import com.magic.investor_api.model.CardtraderSet;
import com.magic.investor_api.model.ScryfallSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ExpansionDAO {

    @Autowired
    private DataSource dataSource;

    // Inserta las expansiones en la tabla scryfall_set
    public void insertScryfallSet(List<ScryfallSet> scryfallExpansionList) {

        String INSERT_EXPANSION = "INSERT INTO scryfall_set(set_code, name, icon_svg_uri," +
                " released_at) " +
                "VALUES (?, ?, ?, ?)";
        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_EXPANSION)){
            for(ScryfallSet e : scryfallExpansionList){
                stmt.setString(1, e.getSetCode());
                stmt.setString(2, e.getName());
                stmt.setString(3, e.getIconSvgUri());
                stmt.setDate(4, Date.valueOf(e.getReleasedAt()));

                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    // Inserta las expansiones en la tabla cardtrader_set
    public void insertCardtraderExpansion(List<CardtraderSet> cardtraderExpansions){

        String query = "INSERT INTO cardtrader_set (id, code, name) " +
                "VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (CardtraderSet e : cardtraderExpansions) {

                stmt.setLong(1, e.getId());
                stmt.setString(2, e.getCode());
                stmt.setString(3, e.getName());

                stmt.addBatch();
            }

            stmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Obtiene los nombres de las expansiones de la tabla scryfall_set
    public List<ScryfallSet> selectScryfallExpansionList(){

        String SELECT_EXPANSION = "SELECT code, name FROM scryfall_set ORDER BY name ASC";

        List<ScryfallSet> scryfallExpansionList = new ArrayList<>();

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_EXPANSION)){

            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                ScryfallSet e = new ScryfallSet();
                e.setSetCode(rs.getString("code"));
                e.setName(rs.getString("name"));
                scryfallExpansionList.add(e);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return scryfallExpansionList;
    }

    // Obtiene los id de las expansiones de la tabla cardtrader_set
    public List<Long> getExpansionListId(){

        String SELECT_EXPANSION = "SELECT id FROM cardtrader_set ORDER BY id ASC";

        List<Long> expansionListId = new ArrayList<>();

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_EXPANSION)){

            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Long expansionId = rs.getLong("id");
                expansionListId.add(expansionId);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return expansionListId;
    }

    // Obtener progreso de la tabla sync_progress
    // La utilizo como checkpoint para saber la última expansión de la que estoy descargando
    // las cartas de cardtrader
    public Long getLastExpansionId() {

        String sql = "SELECT last_expansion_id FROM sync_progress";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

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

        String sql = "UPDATE sync_progress SET last_expansion_id = ? WHERE code_expansion = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.setString(2, "card_variant_sync");
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
