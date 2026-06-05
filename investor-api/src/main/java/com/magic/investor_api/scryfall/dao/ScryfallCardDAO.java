package com.magic.investor_api.scryfall.dao;

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

    // Busca cartas aplicando filtros opcionales: nombre, set, rareza, idioma, tipo y precio.
    // Todos los parámetros son opcionales excepto size y offset.
    public List<ScryfallCardDTO> selectFiltersCard(String name, String setCode, String rarity,
                                                   String lang, String typeLine, String orderBy,
                                                   Double minPrice, Double maxPrice,
                                                   int size, int offset, boolean hideNA) {

        List<ScryfallCardDTO> cardListDTO = new ArrayList<>();
        StringBuilder query = new StringBuilder(
                "SELECT DISTINCT sc.id, sc.scryfall_id, sc.cardmarket_id, sc.name, sc.printed_name, sc.lang, " +
                        "sc.image_url, sc.rarity, sc.set_name, " +
                        "sc.collector_number, sc.cardmarket_url, sc.price, s.icon_svg_uri " +
                        "FROM scryfall_card sc " +
                        "JOIN scryfall_set s ON sc.set_code = s.set_code " +
                        "WHERE 1=1"
        );

        // Filtros
        if (name != null && !name.isEmpty()) query.append(" AND (sc.name LIKE ? OR sc.printed_name LIKE ?)");
        if (setCode  != null) query.append(" AND sc.set_code = ?");
        if (rarity   != null) query.append(" AND sc.rarity = ?");
        if (lang     != null) query.append(" AND sc.lang = ?");
        if (typeLine != null) query.append(" AND sc.type_line LIKE ?");

        // Filtros de precio
        if (minPrice != null) query.append(" AND (price >= ? OR price = 0 OR price IS NULL)");
        if (maxPrice != null) query.append(" AND (price <= ? OR price = 0 OR price IS NULL)");
        if (hideNA) query.append(" AND price IS NOT NULL AND price > 0"); // ← antes del ORDER BY

        // Ordenación
        if ("price_asc".equals(orderBy))  query.append(" ORDER BY sc.price ASC");
        if ("price_desc".equals(orderBy)) query.append(" ORDER BY sc.price DESC");
        if ("name_asc".equals(orderBy))   query.append(" ORDER BY sc.name ASC");
        if ("name_desc".equals(orderBy))  query.append(" ORDER BY sc.name DESC");

        // Paginación
        query.append(" LIMIT ? OFFSET ?");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            // ASIGNACIÓN DE PARÁMETROS
            int idx = 1;

            // Parámetros de texto
            if (name != null && !name.isEmpty()) {
                stmt.setString(idx++, name + "%");  // sc.name LIKE ?
                stmt.setString(idx++, name + "%");  // sc.printed_name LIKE ?
            }
            if (setCode  != null) stmt.setString(idx++, setCode);
            if (rarity   != null) stmt.setString(idx++, rarity);
            if (lang     != null) stmt.setString(idx++, lang);
            if (typeLine != null) stmt.setString(idx++, "%" + typeLine + "%");

            // Parámetros de precio
            if (minPrice != null) stmt.setDouble(idx++, minPrice);
            if (maxPrice != null) stmt.setDouble(idx++, maxPrice);

            // Paginación
            stmt.setInt(idx++, size);   // LIMIT
            stmt.setInt(idx,   offset); // OFFSET

            ResultSet rs = stmt.executeQuery();

            // MAPEO EL RESULTADO A DTOs
            while (rs.next()) {
                ScryfallCardDTO cardDTO = new ScryfallCardDTO();
                cardDTO.setId(rs.getLong("id"));
                cardDTO.setScryfallId(rs.getString("scryfall_id"));
                cardDTO.setName(rs.getString("name"));
                cardDTO.setPrintedName(rs.getString("printed_name"));
                cardDTO.setLang(rs.getString("lang"));
                cardDTO.setImageUrl(rs.getString("image_url"));
                cardDTO.setRarity(rs.getString("rarity"));
                cardDTO.setSetName(rs.getString("set_name"));
                cardDTO.setIconSvgUri(rs.getString("icon_svg_uri"));
                cardDTO.setCollectorNumber(rs.getString("collector_number"));
                cardDTO.setCardmarketURL(rs.getString("cardmarket_url"));
                cardDTO.setPrice(rs.getBigDecimal("price"));
                cardListDTO.add(cardDTO);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return cardListDTO;
    }


    // Cuenta el total de cartas que coinciden con los filtros opcionales.
    // Se usa para calcular el número de páginas en la paginación.
    // NOTA: orderBy no se incluye aquí porque ORDER BY no afecta al COUNT
    public int countCardsByFilter(String name, String setCode, String rarity, String lang,
                                  String typeLine, Double minPrice, Double maxPrice, boolean hideNA) {

        // Construimos la query dinámicamente según los filtros recibidos
        StringBuilder query = new StringBuilder(
                "SELECT COUNT(*) FROM scryfall_card WHERE 1=1"
        );

        // Filtros de texto — solo si no son nulos ni vacíos
        if (name != null && !name.isEmpty()) query.append(" AND (name LIKE ? OR printed_name LIKE ?)");
        if (setCode  != null) query.append(" AND set_code = ?");
        if (rarity   != null) query.append(" AND rarity = ?");
        if (lang     != null) query.append(" AND lang = ?");
        if (typeLine != null) query.append(" AND type_line LIKE ?");

        // Filtros de precio
        if (minPrice != null) query.append(" AND (price >= ? OR price = 0 OR price IS NULL)");
        if (maxPrice != null) query.append(" AND (price <= ? OR price = 0 OR price IS NULL)");
        if (hideNA) query.append("AND price IS NOT NULL AND price > 0 ");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            // Asignamos los parámetros en el mismo orden que aparecen los ? en la query
            int idx = 1;

            // Parámetros de texto
            if (name != null && !name.isEmpty()) {
                stmt.setString(idx++, name + "%");  // name LIKE ?
                stmt.setString(idx++, name + "%");  // printed_name LIKE ?
            }
            if (setCode  != null) stmt.setString(idx++, setCode);
            if (rarity   != null) stmt.setString(idx++, rarity);
            if (lang     != null) stmt.setString(idx++, lang);
            if (typeLine != null) stmt.setString(idx++, "%" + typeLine + "%");

            // Parámetros de precio
            if (minPrice != null) stmt.setDouble(idx++, minPrice);
            if (maxPrice != null) stmt.setDouble(idx++, maxPrice);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return 0;
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
                "WHERE scryfall_id = ?";

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
                card.setPrice(rs.getBigDecimal("sc.price"));
                card.setPriceFoil(rs.getBigDecimal("sc.price_foil"));
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

    // Obtiene carta mediante cardId
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

}