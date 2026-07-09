package com.magic.investor_api.cardtraderPrice.service;

import com.magic.investor_api.cardtrader.service.CardTraderService;
import com.magic.investor_api.cardtraderListing.model.CardtraderListing;
import com.magic.investor_api.cardtraderListing.service.CardtraderListingService;
import com.magic.investor_api.cardtraderPrice.dto.CardtraderPriceDTO;
import com.magic.investor_api.cardtraderListing.dao.CardtraderListingDAO;
import com.magic.investor_api.cardtraderPrice.dao.CardtraderPriceDAO;
import com.magic.investor_api.cardtraderPrice.model.CardtraderPrice;
import com.magic.investor_api.cardtraderPrice.repository.CardtraderPriceRepository;
import com.magic.investor_api.scryfall.dto.ScryfallCardDTO;
import com.magic.investor_api.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardtraderPriceService {

    private final CardtraderListingDAO cardtraderListingDAO;
    private final CardtraderPriceDAO cardtraderPriceDAO;
    private final CardtraderPriceRepository repository;
    private final CardTraderService cardTraderService;
    private final CardtraderListingService cardTraderListingService;

    // Insertar lista de cartas de cardtrader_listing en cardtrader_price
    public void convertCardtraderListingToPriceCache(){

        // Lista de cartas filtradas de cardtrader_listing
        List<CardtraderPrice> cardPriceList = cardtraderListingDAO.getCardtraderListingValues();

        // Inserto la lista en cardtrader_price_cache
        repository.saveAll(cardPriceList);
    }

    // Actualizar precios de cardtrader_listing y cardtrader_price mediante cardtraderId
    public void updateCardtraderPrices(ScryfallCardDTO card){

        // Obtengo el cardTraderId mediante scryfallId
        long cardTraderId = cardTraderService.getCardtraderIdByScryfallId(card.getScryfallId());
        if(cardTraderId > 0){
            // Creo objeto cardtraderListing con los valores obtenidos
            CardtraderListing listing = new CardtraderListing();
            listing.setCardId(card.getId());
            listing.setScryfallId(card.getScryfallId());
            listing.setCardtraderId(cardTraderId);
            listing.setCondition(card.getCondition());
            listing.setLang(card.getLang());
            listing.setFoil(card.isFoil());

            //  Obtener y mapear JsonNode del mercado de cartas cardtrader
            if(cardTraderListingService.updateCardPrice(listing)){ // Si se ha obtenido la lista de precios correctamente
                // Insertar lista de cartas de cardtrader_listing en cardtrader_price
                convertCardtraderListingToPriceCache();
            }
        }
    }

    // Consulta de precios de la carta en cardtrader_price
    public CardtraderPriceDTO getCardtraderPrice(ScryfallCardDTO card){

        // Obtengo el cardTraderId mediante scryfallId
        long cardTraderId = cardTraderService.getCardtraderIdByScryfallId(card.getScryfallId());
        // Si existe cardtraderId
        if(cardTraderId > 0) {
            // Creo objeto CardTraderListing
            CardtraderListing listing = new CardtraderListing();
            listing.setCardtraderId(cardTraderId);
            // Utilizo ENUM para equiparar el valor del campo condition recibido con el de la tabla de la BD
            listing.setCondition(Utils.CardCondition.valueOf(card.getCondition()).getCardTraderValue());
            listing.setLang(card.getLang());
            listing.setFoil(card.isFoil());

            // Trato de obtener precios de cardtrader_price
            CardtraderPriceDTO cardtraderPrice = cardtraderPriceDAO.selectPriceFromCardtraderPrice(listing);
            // Si cardtrader_price tiene precios para la carta
            if (cardtraderPrice != null) {
                card.setCardPrice(cardtraderPrice); // Le asigno los precios obtenidos
                return cardtraderPrice;
            }
        }
        return null;
    }
}
