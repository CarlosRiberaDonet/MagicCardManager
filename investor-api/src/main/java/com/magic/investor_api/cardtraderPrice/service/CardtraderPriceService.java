package com.magic.investor_api.cardtraderPrice.service;

import com.magic.investor_api.cardtraderListing.model.CardtraderListing;
import com.magic.investor_api.cardtraderListing.service.CardtraderListingService;
import com.magic.investor_api.cardtraderPrice.dto.CardtraderPriceDTO;
import com.magic.investor_api.cardtraderListing.dao.CardtraderListingDAO;
import com.magic.investor_api.cardtraderPrice.dao.CardtraderPriceDAO;
import com.magic.investor_api.cardtraderPrice.model.CardtraderPrice;
import com.magic.investor_api.cardtraderPrice.repository.CardtraderPriceRepository;
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
    private final CardtraderListingService cardTraderListingService;

    // Insertar lista de cartas de cardtrader_listing en cardtrader_price
    public void convertCardtraderListingToPriceCache(){

        // Lista de cartas filtradas de cardtrader_listing
        List<CardtraderPrice> cardPriceList = cardtraderListingDAO.getCardtraderListingValues();

        // Inserto la lista en cardtrader_price_cache
        repository.saveAll(cardPriceList);
    }

    // Obtener lista de precios mediante cardtraderId
    public void getCardtraderPrices(CardtraderListing request){
        //  Obtener y mapear JsonNode del mercado de cartas cardtrader
        cardTraderListingService.updateCardPrice(request);

        // Insertar lista de cartas de cardtrader_listing en cardtrader_price
        convertCardtraderListingToPriceCache();
    }

    // Consulta de precios de la carta en cardtrader_price
    public CardtraderPriceDTO getCardtraderPrice(CardtraderListing dto){

        // Utilizo ENUM para equiparar el valor del campo condition recibido con el de la tabla de la BD
        String cardCondition =  Utils.CardCondition.valueOf(dto.getCondition()).getCardTraderValue();
        dto.setCondition(cardCondition);
        return cardtraderPriceDAO.selectFromCardtraderPriceCache(dto);
    }
}
