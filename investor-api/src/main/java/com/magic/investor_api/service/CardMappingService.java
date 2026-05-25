package com.magic.investor_api.service;

import com.magic.investor_api.dao.CardMappingDAO;
import com.magic.investor_api.dao.CardtraderDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardMappingService {

    private final CardMappingDAO cardMappingDAO;
    private final CardtraderDAO cardtraderDAO;


    // Inserto scryfall_id de scryfall_card en card_mapping
    public void insertScryfallId(){
        cardMappingDAO.insertScryfallId();
    }

    // Mapeo cardmarket_id desde scryfall_card
    public void mapCards(){
        cardMappingDAO.mappingCardmarketId();
    }

    // Mapeo cardmarket_id desde cardtrader
    public void mapCardmarketCards(){
        cardMappingDAO.mappingCardtraderId();
    }

    // Inserta set_code y set_name en cardtrader_card
    public void mapCardtraderSets(){
        cardtraderDAO.mappingCardtraderSets();
    }

    // Mapear cardtrader_card desde scryfall_card
    public void mapCardtraderCard(){
        cardMappingDAO.updateCardmarketIdOnCardtraderCard();
    }

    public void mapScryfallCard(){
        cardMappingDAO.updateCardmarketIdOnScryfallCard();
    }

    public void lastJoin(){
        cardMappingDAO.lastJoin1();
        cardMappingDAO.lastJoin2();
    }
}
