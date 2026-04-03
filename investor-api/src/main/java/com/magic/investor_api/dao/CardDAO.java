package com.magic.investor_api.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class CardDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Long> getCardMapIds() {
        String sql = "SELECT id, cardmarket_id FROM card WHERE cardmarket_id IS NOT NULL";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new Object[]{ rs.getString("id"), rs.getLong("cardmarket_id") };
        }).stream().collect(Collectors.toMap(
                data -> (String) data[0], // UUID Scryfall como clave
                data -> (Long) data[1],   // cardmarket_id como valor
                (existing, replacement) -> existing // Si hay duplicados, conservar el primero
        ));
    }
}