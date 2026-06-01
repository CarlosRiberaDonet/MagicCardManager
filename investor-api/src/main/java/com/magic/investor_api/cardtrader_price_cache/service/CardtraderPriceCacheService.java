package com.magic.investor_api.cardtrader_price_cache.service;

import com.magic.investor_api.cardtrader_price_cache.dao.CardtraderListingDAO;
import com.magic.investor_api.cardtrader_price_cache.model.CardtraderPriceCache;
import com.magic.investor_api.cardtrader_price_cache.repository.CardtraderPriceCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardtraderPriceCacheService {

    private final CardtraderListingDAO cardtraderListingDAO;
    private final CardtraderPriceCacheRepository repository;

    // Insertar lista en cardtrader_price_cache
    public void converCardtraderListingToPriceCache(){


        // Lista de cartas filtradas de cardtrader_listing
        List<CardtraderPriceCache> cardPriceList = cardtraderListingDAO.getCardtraderListingValues();

        // Inserto la lista en cardtrader_price_cache
        repository.saveAll(cardPriceList);
    }
}
