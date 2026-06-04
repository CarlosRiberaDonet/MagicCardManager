package com.magic.investor_api.cardtraderPrice.service;

import com.magic.investor_api.cardtraderPrice.dto.CardtraderPriceDTO;
import com.magic.investor_api.cardtraderListing.dao.CardtraderListingDAO;
import com.magic.investor_api.cardtraderPrice.dao.CardtraderPriceDAO;
import com.magic.investor_api.cardtraderPrice.model.CardtraderPrice;
import com.magic.investor_api.cardtraderPrice.repository.CardtraderPriceRepository;
import com.magic.investor_api.cardmarketPrice.model.CardmarketPrice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardtraderPriceService {

    private final CardtraderListingDAO cardtraderListingDAO;
    private final CardtraderPriceDAO cardtraderPriceDAO;
    private final CardtraderPriceRepository repository;

    // Insertar lista en cardtrader_price_cache
    public void convertCardtraderListingToPriceCache(){

        // Lista de cartas filtradas de cardtrader_listing
        List<CardtraderPrice> cardPriceList = cardtraderListingDAO.getCardtraderListingValues();

        // Inserto la lista en cardtrader_price_cache
        repository.saveAll(cardPriceList);
    }

    // Compruebo si la carta tiene precios en cardtrader_price_cache
    public CardmarketPrice getCardtraderPrice(Long cardmarketId){

        return null;
    }

    // Consulta en cardtrader_price_cache
    public CardtraderPriceDTO getCardtraderPriceCacheDTO(Long cardId, String lang){

        return cardtraderPriceDAO.selectFromCardtraderPriceCache(cardId, lang);
    }
}
