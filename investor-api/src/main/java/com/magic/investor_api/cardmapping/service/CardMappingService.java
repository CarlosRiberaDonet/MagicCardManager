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

    // Inserto ids desde cardtrader_card en card_mapping
    public void insertCardtraderIdOnCardMapping(){
        cardMappingDAO.insertCardmarketIdAndCardtraderIdOnCardMapping();
    }

    // Obtener cardmarketId a través de scryfallId en card_mapping
    public Long getCardtraderId(String scryfallId){

        // Retorna cardTraderId
        return cardMappingDAO.getCardtraderIdFromCardtraderCard(scryfallId);
    }
}
