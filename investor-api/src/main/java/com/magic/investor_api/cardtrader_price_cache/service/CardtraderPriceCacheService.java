package com.magic.investor_api.cardtrader_price_cache.service;

import com.magic.investor_api.cardtrader_price_cache.CardtraderPriceCacheDTO;
import com.magic.investor_api.cardtrader_price_cache.dao.CardtraderListingDAO;
import com.magic.investor_api.cardtrader_price_cache.dao.CardtraderPriceCacheDAO;
import com.magic.investor_api.cardtrader_price_cache.model.CardtraderPriceCache;
import com.magic.investor_api.cardtrader_price_cache.repository.CardtraderPriceCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardtraderPriceCacheService {

    private final CardtraderListingDAO cardtraderListingDAO;
    private final CardtraderPriceCacheDAO cardtraderPriceCacheDAO;
    private final CardtraderPriceCacheRepository repository;

    // Insertar lista en cardtrader_price_cache
    public void convertCardtraderListingToPriceCache(){

        // Lista de cartas filtradas de cardtrader_listing
        List<CardtraderPriceCache> cardPriceList = cardtraderListingDAO.getCardtraderListingValues();

        // Inserto la lista en cardtrader_price_cache
        repository.saveAll(cardPriceList);
    }

    // Consulta en cardtrader_price_cache
    public CardtraderPriceCacheDTO getCardtraderPriceCacheDTO(Long cardId, String lang, String condition, boolean isFoil){

        return cardtraderPriceCacheDAO.selectFromCardtraderPriceCache(cardId, lang, condition, isFoil);
    }
}
