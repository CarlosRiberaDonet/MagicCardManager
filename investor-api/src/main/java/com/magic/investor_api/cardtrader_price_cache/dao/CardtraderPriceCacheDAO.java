package com.magic.investor_api.cardtrader_price_cache.dao;

import com.magic.investor_api.cardtrader_price_cache.model.CardtraderPriceCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CardtraderPriceCacheDAO {

    @Autowired
    private DataSource dataSource;

    // Insertar cartas en Cardtrader_price_cache
    public void insertCardtraderPriceCache(){

    }
}
