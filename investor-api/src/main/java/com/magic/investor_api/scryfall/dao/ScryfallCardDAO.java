package com.magic.investor_api.scryfall.dao;

import com.magic.investor_api.cardmarketPrice.model.CardmarketPrice;
import com.magic.investor_api.scryfall.dto.ScryfallCardDTO;
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

    // Busca cartas aplicando filtros opcionales. Todos los parámetros son opcionales excepto size y offset.
    public List<ScryfallCardDTO> selectFiltersCard(
            String name,
            String setCode,
            String rarity,
            String lang,
            String typeLine,
            String orderBy,
            int size,
            int offset
    ) {

        List<ScryfallCardDTO> cardListDTO = new ArrayList<>();

        StringBuilder query = new StringBuilder(
                "SELECT DISTINCT sc.id, sc.scryfall_id, sc.cardmarket_id, sc.name, sc.printed_name, " +
                        "sc.lang, sc.image_url, sc.rarity, sc.set_name, " +
                        "sc.collector_number, s.icon_svg_uri, " +
                        "cp.avg, cp.low, cp.trend " +
                        "FROM scryfall_card sc " +
                        "JOIN scryfall_set s ON sc.set_code = s.set_code " +
                        "LEFT JOIN cardmarket_price cp ON cp.cardmarket_id = sc.cardmarket_id " +
                        "WHERE 1=1"
        );

        if (name != null && !name.isEmpty())
            query.append(" AND (sc.name LIKE ? OR sc.printed_name LIKE ?)");

        if (setCode != null)
            query.append(" AND sc.set_code = ?");

        if (rarity != null)
            query.append(" AND sc.rarity = ?");

        if (lang != null)
            query.append(" AND sc.lang = ?");

        if (typeLine != null)
            query.append(" AND sc.type_line LIKE ?");

        switch (orderBy == null ? "" : orderBy) {
            case "name_desc":
                query.append(" ORDER BY sc.name DESC");
                break;
            case "price_asc":
                query.append(" ORDER BY COALESCE(cp.trend, cp.avg, cp.low) ASC");
                break;
            case "price_desc":
                query.append(" ORDER BY COALESCE(cp.trend, cp.avg, cp.low) DESC");
                break;
            default:
                query.append(" ORDER BY sc.name ASC");
        }

        query.append(" LIMIT ? OFFSET ?");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            int idx = 1;

            if (name != null && !name.isEmpty()) {
                stmt.setString(idx++, name + "%");
                stmt.setString(idx++, name + "%");
            }

            if (setCode != null) stmt.setString(idx++, setCode);
            if (rarity != null) stmt.setString(idx++, rarity);
            if (lang != null) stmt.setString(idx++, lang);
            if (typeLine != null) stmt.setString(idx++, "%" + typeLine + "%");

            stmt.setInt(idx++, size);
            stmt.setInt(idx, offset);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CardmarketPrice price = new CardmarketPrice();
                price.setAvg(rs.getBigDecimal("avg"));
                price.setLow(rs.getBigDecimal("low"));
                price.setTrend(rs.getBigDecimal("trend"));

                ScryfallCardDTO dto = new ScryfallCardDTO();
                dto.setId(rs.getLong("id"));
                dto.setScryfallId(rs.getString("scryfall_id"));
                dto.setCardmarketId(rs.getLong("cardmarket_id"));
                dto.setName(rs.getString("name"));
                dto.setPrintedName(rs.getString("printed_name"));
                dto.setLang(rs.getString("lang"));
                dto.setImageUrl(rs.getString("image_url"));
                dto.setRarity(rs.getString("rarity"));
                dto.setSetName(rs.getString("set_name"));
                dto.setIconSvgUri(rs.getString("icon_svg_uri"));
                dto.setCollectorNumber(rs.getString("collector_number"));
                dto.setCardPrice(price);

                cardListDTO.add(dto);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return cardListDTO;
    }

    // Cuenta el total de cartas que coinciden con los filtros opcionales
    public int countCardsByFilter(
            String name,
            String setCode,
            String rarity,
            String lang,
            String typeLine
    ) {

        StringBuilder query = new StringBuilder(
                "SELECT COUNT(*) FROM scryfall_card WHERE 1=1 "
        );

        // Filtros de texto
        if (name != null && !name.isEmpty()) {
            query.append(" AND (name LIKE ? OR printed_name LIKE ?) ");
        }

        if (setCode != null && !setCode.isEmpty()) {
            query.append(" AND set_code = ? ");
        }

        if (rarity != null && !rarity.isEmpty()) {
            query.append(" AND rarity = ? ");
        }

        if (lang != null && !lang.isEmpty()) {
            query.append(" AND lang = ? ");
        }

        if (typeLine != null && !typeLine.isEmpty()) {
            query.append(" AND type_line LIKE ? ");
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            int idx = 1;

            if (name != null && !name.isEmpty()) {
                stmt.setString(idx++, name + "%");
                stmt.setString(idx++, name + "%");
            }

            if (setCode != null && !setCode.isEmpty()) {
                stmt.setString(idx++, setCode);
            }

            if (rarity != null && !rarity.isEmpty()) {
                stmt.setString(idx++, rarity);
            }

            if (lang != null && !lang.isEmpty()) {
                stmt.setString(idx++, lang);
            }

            if (typeLine != null && !typeLine.isEmpty()) {
                stmt.setString(idx++, "%" + typeLine + "%");
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    // Obtener cardmarketId de la tabla scryfall_card
    public Long selectCardmarketIdBycardId(Long cardId){
        String sql = "SELECT cardmarket_id FROM scryfall_card WHERE id = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setLong(1, cardId);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                // Retornar el valor obtenido
               return rs.getLong("cardmarket_id");
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return -1L;
    }

    // Obtener detalles de carta mediante id de scryfall_id
    public ScryfallCardDTO getScryfallCardById(String scryfallId){

        String query = "SELECT sc.id, sc.scryfall_id, sc.cardmarket_id, sc.name, sc.printed_name, " +
                "sc.lang, sc.image_url, sc.rarity, sc.set_code, sc.set_name, sc.collector_number, sc.cardmarket_url, " +
                "sc.price, sc.price_foil, sc.type_line, " +
                "sc.border_color, sc.frame, sc.is_reprint, sc.released_at, " +
                "ss.set_code, ss.icon_svg_uri " +
                "FROM scryfall_card sc " +
                "JOIN scryfall_set ss ON sc.set_code = ss.set_code " +
                "WHERE id = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setString(1, scryfallId);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                ScryfallCardDTO card = new ScryfallCardDTO();
                card.setId(rs.getLong("id"));
                card.setScryfallId(rs.getString("sc.scryfall_id"));
                card.setCardmarketId(rs.getLong("sc.cardmarket_id"));
                card.setName(rs.getString("sc.name"));
                card.setPrintedName(rs.getString("sc.printed_name"));
                card.setLang(rs.getString("sc.lang"));
                card.setImageUrl(rs.getString("sc.image_url"));
                card.setRarity(rs.getString("sc.rarity"));
                card.setSetCode(rs.getString("set_code"));
                card.setSetName(rs.getString("sc.set_name"));
                card.setCollectorNumber(rs.getString("sc.collector_number"));
                card.setCardmarketURL(rs.getString("sc.cardmarket_url"));
                card.setTypeLine(rs.getString("sc.type_line"));
                card.setBorderColor(rs.getString("sc.border_color"));
                card.setFrame(rs.getString("sc.frame"));
                card.setReprint(rs.getBoolean("sc.is_reprint"));
                card.setSetCode(rs.getString("ss.set_code"));
                card.setIconSvgUri(rs.getString("ss.icon_svg_uri"));

                card.setReleasedAt(rs.getDate("released_at") != null ? rs.getDate("released_at").toLocalDate() : null);

                return card;
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return null;
    }

    // Obtener detalles de carta
    public ScryfallCardDTO getCardById(Long cardId){

        String query = "SELECT sc.id, sc.scryfall_id, sc.cardmarket_id, sc.name, sc.printed_name, " +
                "sc.lang, sc.image_url, sc.rarity, sc.set_code, sc.set_name, sc.collector_number, " +
                "sc.type_line, sc.cardmarket_url, sc.border_color, sc.frame, sc.is_reprint, " +
                "sc.released_at, ss.set_code, ss.icon_svg_uri " +
                "FROM scryfall_card sc " +
                "JOIN scryfall_set ss ON sc.set_code = ss.set_code " +
                "WHERE sc.id = ?";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setLong(1, cardId);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                ScryfallCardDTO card = new ScryfallCardDTO();
                card.setId(rs.getLong("id"));
                card.setScryfallId(rs.getString("sc.scryfall_id"));
                card.setCardmarketId(rs.getLong("sc.cardmarket_id"));
                card.setName(rs.getString("sc.name"));
                card.setPrintedName(rs.getString("sc.printed_name"));
                card.setLang(rs.getString("sc.lang"));
                card.setImageUrl(rs.getString("sc.image_url"));
                card.setRarity(rs.getString("sc.rarity"));
                card.setSetCode(rs.getString("set_code"));
                card.setSetName(rs.getString("sc.set_name"));
                card.setCollectorNumber(rs.getString("sc.collector_number"));
                card.setCardmarketURL(rs.getString("sc.cardmarket_url"));
                card.setTypeLine(rs.getString("sc.type_line"));
                card.setBorderColor(rs.getString("sc.border_color"));
                card.setFrame(rs.getString("sc.frame"));
                card.setReprint(rs.getBoolean("sc.is_reprint"));
                card.setSetCode(rs.getString("ss.set_code"));
                card.setIconSvgUri(rs.getString("ss.icon_svg_uri"));

                card.setReleasedAt(rs.getDate("released_at") != null ? rs.getDate("released_at").toLocalDate() : null);

                return card;
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return null;
    }

    // Obtiene URL de cardmarket
    public String getUrlCard(Long cardId){

        String query = "SELECT url FROM scryfall_card WHERE id = ? ";
        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setLong(1, cardId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getString("url");
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return "";
    }

    // Borra los datos de la tabla scryfall_card
    public void truncateScryfallCard(){
        String query = "TRUNCATE TABLE scryfall_card";

        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    // Obtiene todos los scryfall_id
    public List<String> getScryfallIdList(){
        List<String> scryfallIdList = new ArrayList<>();
        String query = "SELECT ct.cardtrader_id FROM cardtrader_card ct " +
                "WHERE EXISTS (SELECT 1 ";
        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                String id = rs.getString("scryfall_id");
                scryfallIdList.add(id);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return scryfallIdList;
    }

    // Actualiza el campo price
    public void updateScryfallPrice(){
        String query = "UPDATE scryfall_card sc " +
                "JOIN cardmarket_price cp ON sc.cardmarket_id = cp.cardmarket_id " +
                "SET sc.price = cp.low " +
                "WHERE cp.low IS NOT NULL";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)){

           int filasAfectadas = stmt.executeUpdate();
           if(filasAfectadas > 0){
               System.out.println("Total precios actualizados" + filasAfectadas);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}