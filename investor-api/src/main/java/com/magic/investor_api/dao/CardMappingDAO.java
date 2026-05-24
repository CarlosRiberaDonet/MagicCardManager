package com.magic.investor_api.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class CardMappingDAO {

    @Autowired
    private DataSource dataSource;

    // Mapear campos scryfall_id, cardtrader_id y cardmarket_id en card_mapping
    public void mappingCards(){
        String query = "";
    }
}
