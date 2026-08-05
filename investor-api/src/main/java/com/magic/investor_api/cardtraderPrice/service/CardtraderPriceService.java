package com.magic.investor_api.cardtraderPrice.service;

import com.magic.investor_api.cardtrader.dao.CardtraderDAO;
import com.magic.investor_api.cardtraderListing.model.CardtraderListing;
import com.magic.investor_api.cardtraderListing.service.CardtraderListingService;
import com.magic.investor_api.cardtraderPrice.dto.CardtraderPriceDTO;
import com.magic.investor_api.cardtraderListing.dao.CardtraderListingDAO;
import com.magic.investor_api.cardtraderPrice.dao.CardtraderPriceDAO;
import com.magic.investor_api.cardtraderPrice.model.CardtraderPrice;
import com.magic.investor_api.cardtraderPrice.repository.CardtraderPriceRepository;
import com.magic.investor_api.scryfall.dto.ScryfallCardDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardtraderPriceService {

    private final CardtraderListingDAO cardtraderListingDAO;
    private final CardtraderPriceDAO cardtraderPriceDAO;
    private final CardtraderPriceRepository repository;
    private final CardtraderDAO cardtraderDAO;
    private final CardtraderListingService cardTraderListingService;

    // Insertar lista de cartas de cardtrader_listing en cardtrader_price
    public void convertCardtraderListingToCardtraderPrice(Long cardId){

        // Lista de cartas filtradas de cardtrader_listing
        List<CardtraderPrice> cardPriceList = cardtraderListingDAO.getCardtraderListingValues(cardId);

        // Inserto la lista en cardtrader_price
        cardtraderPriceDAO.insertCardtraderPrice(cardPriceList);
    }

    // Actualizar precios de cardtrader_listing y cardtrader_price mediante cardtraderId
    public boolean updateCardtraderPrices(ScryfallCardDTO card){

        System.out.println("SCRYFALL ID: " + card.getScryfallId());
        // Obtengo el cardTraderId mediante scryfallId
        long cardTraderId = cardtraderDAO.selectCardTraderId(card.getScryfallId());
        System.out.println("CARDTRADER ID: " + cardTraderId);

        if(cardTraderId > 0){
            // Creo objeto cardtraderListing con los valores obtenidos
            CardtraderListing listing = new CardtraderListing();
            listing.setCardId(card.getId());
            listing.setScryfallId(card.getScryfallId());
            listing.setCardtraderId(cardTraderId);
            listing.setCondition(card.getCondition());
            listing.setLang(card.getLang());
            listing.setFoil(card.isFoil());

            //  Obtener y mapear JsonNode del mercado de cartas cardtrader en cardtrader_listing
            cardTraderListingService.updateCardPrice(listing);

            // Insertar lista de cartas de cardtrader_listing en cardtrader_price
            convertCardtraderListingToCardtraderPrice(card.getId());

        }
        return false;
    }

    // Consulta de precios de la carta en cardtrader_price
    public CardtraderPriceDTO getCardtraderPrice(ScryfallCardDTO card){

        // Asigno el cardTraderId mediante scryfallId
        card.setCardTraderId(cardtraderDAO.selectCardTraderId(card.getScryfallId()));

        // Obtengo precios de la tabla cardtrader_price
        return cardtraderPriceDAO.selectPriceFromCardtraderPrice(card);
    }
}
