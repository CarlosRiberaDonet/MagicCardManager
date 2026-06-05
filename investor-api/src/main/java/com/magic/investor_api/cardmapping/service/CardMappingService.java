package com.magic.investor_api.cardmapping.service;

import com.magic.investor_api.cardmapping.dao.CardMappingDAO;
import com.magic.investor_api.cardtrader.dao.CardtraderDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardMappingService {

    private final CardMappingDAO cardMappingDAO;
    private final CardtraderDAO cardtraderDAO;

    // Inserto cardtrader_id desde cardtrader_card
    public void insertCardtraderIdOnCardMapping(){
        cardMappingDAO.insertCardmarketIdAndCardtraderIdOnCardMapping();
    }

    // Mapeo scryfall_id de cardtrader_card en card_mapping
    public void mapScryfallIdOnCardMapping(){
        cardMappingDAO.updateScryfallIdOnCardMapping();
    }

    // Mapeo cardmarket_id de cardtrader_card en card_mapping
    public void mapCardmarketIdOnCardMapping(){
        cardMappingDAO.updateCardmarketIdOnCardMapping();
    }

    // Obtener cardmarketId a través de scryfallId en card_mapping
    public Long[] getIds(String scryfallId){
        return cardMappingDAO.getCardmarketAndCardtraderId(scryfallId);
    }
}
