package com.magic.investor_api.dao;

import com.magic.investor_api.dto.ScryfallCardDTO;
import com.magic.investor_api.model.CardPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class ScryfallCardDAO {

    @Autowired
    private DataSource dataSource;

    // Obtener carta mediante su nombre
    public List<ScryfallCardDTO> selectCardByName(String name, String rarity, String lang, String typeLine, int page, int size, int offset) {

        StringBuilder query = new StringBuilder(
                "SELECT DISTINCT sc.id, sc.name, sc.printed_name, sc.lang, " +
                        "sc.image_url, sc.rarity, sc.set_name, " +
                        "sc.collector_number, sc.cardmarket_url, sc.price " +
                        "FROM scryfall_card sc " +
                        "WHERE (sc.name LIKE ? OR sc.printed_name LIKE ?)"
        );

        if (rarity != null)   query.append(" AND sc.rarity = ?");
        if (lang != null)     query.append(" AND sc.lang = ?");
        if (typeLine != null) query.append(" AND sc.type_line LIKE ?");
        query.append(" LIMIT ? OFFSET ?");

        List<ScryfallCardDTO> cardListDTO = new ArrayList<>();

        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query.toString())) {


            int idx = 1;
            stmt.setString(idx++, name + "%");
            stmt.setString(idx++, name + "%");
            if (rarity != null) stmt.setString(idx++, rarity);
            if (lang != null) stmt.setString(idx++, lang);
            if (typeLine != null) stmt.setString(idx++, "%" + typeLine + "%");
            stmt.setInt(idx++, size);
            stmt.setInt(idx++, offset);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ScryfallCardDTO cardDTO = new ScryfallCardDTO();
                cardDTO.setId(rs.getLong("id"));
                cardDTO.setName(rs.getString("name"));
                cardDTO.setPrintedName(rs.getString("printed_name"));
                cardDTO.setLang(rs.getString("lang"));
                cardDTO.setImageUrl(rs.getString("image_url"));
                cardDTO.setRarity(rs.getString("rarity"));
                cardDTO.setSetName(rs.getString("set_name"));
                cardDTO.setCollectorNumber(rs.getString("collector_number"));
                cardDTO.setCardmarketURL(rs.getString("cardmarket_url"));
                cardDTO.setPrice(rs.getBigDecimal("price"));

                cardListDTO.add(cardDTO);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return cardListDTO;
    }
    // Actualizar cardmarket id en tabla card mediante scryfallUUID de la tabla card_variant
    public boolean updateCardmarketId(){
        String UPDATE_CARDMARKET_ID = "UPDATE card c " +
                "JOIN card_variant cv ON cv.scryfall_id = c.scryfall_id " +
                "SET c.cardmarket_id = cv.cardmarket_id " +
                "WHERE (c.cardmarket_id IS NULL OR c.cardmarket_id = 0) " +
                "AND cv.cardmarket_id IS NOT NULL AND cv.cardmarket_id > 0";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_CARDMARKET_ID)){

            int filasAfectadas = stmt.executeUpdate();

            if(filasAfectadas > 0){
                System.out.println("Cartas actualizadas en tabla card_variant: " + filasAfectadas);
                return true;
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    // mapear precios card_price a scryfall_card
    public void updateScryfallPrices(){
        String query = "UPDATE scryfall_card sc " +
                "JOIN card_price cp ON cp.cardmarket_id = sc.cardmarket_id " +
                "SET sc.price = cp.avg, " +
                "sc.price_foil = cp.avg_foil";

        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)){

            System.out.println("Actualizando precios...");
            int filasAfectadas = stmt.executeUpdate();
            System.out.println("Precios actualizados: " + filasAfectadas);

        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    // Contador de cartas por nombre
    public int countCardsByName(String name, String rarity, String lang, String typeLine){

        StringBuilder query = new StringBuilder(
                "SELECT COUNT(*) FROM scryfall_card " +
                        "WHERE (name LIKE ? OR printed_name LIKE ?)"
        );

        if (rarity != null) query.append(" AND rarity = ?");
        if (lang != null) query.append(" AND lang = ?");
        if (typeLine != null) query.append(" AND type_line LIKE ?");

        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            int idx = 1;
            stmt.setString(idx++, name + "%");
            stmt.setString(idx++, name + "%");
            if (rarity != null)   stmt.setString(idx++, rarity);
            if (lang != null)     stmt.setString(idx++, lang);
            if (typeLine != null) stmt.setString(idx++, "%" + typeLine + "%");

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }


        return -1;
    }
    // Obtener detalles de carta mediante id de scryfall_card
    public ScryfallCardDTO getScryfallCardById(Long cardId){

        String query = "SELECT sc.scryfall_id, sc.cardmarket_id, sc.cardtrader_id, sc.name, sc.printed_name, " +
                "sc.lang, sc.image_url, sc.rarity, sc.set_name, sc.collector_number, sc.cardmarket_url, sc.type_line, " +
                "sc.border_color, sc.frame, sc.is_reprint, sc.released_at, " +
                "cp.low, cp.trend, cp.avg1, cp.avg7, cp.avg30, cp.low_foil, cp.trend_foil, cp.avg1_foil, " +
                "cp.avg7_foil, cp.avg30_foil, cp.updated_at " +
                "FROM scryfall_card sc " +
                "LEFT JOIN  card_price cp ON cp.cardmarket_id = sc.cardmarket_id " +
                "WHERE sc.id = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setLong(1, cardId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                ScryfallCardDTO card = new ScryfallCardDTO();
                card.setId(cardId);
                card.setScryfallId(rs.getString("scryfall_id"));
                card.setCardmarketId(rs.getLong("cardmarket_id"));
                card.setCardtraderId(rs.getLong("cardtrader_id"));
                card.setName(rs.getString("name"));
                card.setPrintedName(rs.getString("printed_name"));
                card.setLang(rs.getString("lang"));
                card.setImageUrl(rs.getString("image_url"));
                card.setRarity(rs.getString("rarity"));
                card.setSetName(rs.getString("set_name"));
                card.setCollectorNumber(rs.getString("collector_number"));
                card.setCardmarketURL(rs.getString("cardmarket_url"));
                card.setTypeLine(rs.getString("type_line"));
                card.setBorderColor(rs.getString("border_color"));
                card.setFrame(rs.getString("frame"));
                card.setReprint(rs.getBoolean("is_reprint"));
                card.setReleasedAt(rs.getDate("released_at") != null ? rs.getDate("released_at").toLocalDate() : null);

                CardPrice cardPrice = new CardPrice();
                cardPrice.setLow(rs.getBigDecimal("low"));
                cardPrice.setTrend(rs.getBigDecimal("trend"));
                cardPrice.setAvg1(rs.getBigDecimal("avg1"));
                cardPrice.setAvg7(rs.getBigDecimal("avg7"));
                cardPrice.setAvg30(rs.getBigDecimal("avg30"));
                cardPrice.setLowFoil(rs.getBigDecimal("low_foil"));
                cardPrice.setTrendFoil(rs.getBigDecimal("trend_foil"));
                cardPrice.setAvg1Foil(rs.getBigDecimal("avg1_foil"));
                cardPrice.setAvg7Foil(rs.getBigDecimal("avg7_foil"));
                cardPrice.setAvg30Foil(rs.getBigDecimal("avg30_foil"));
                if (rs.getTimestamp("updated_at") != null)
                    cardPrice.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

                card.setCardPrice(cardPrice);

                return card;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}