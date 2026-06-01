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

    // Mapear scryfall_card desde cardtrader_card
    public void mapScryfallCard(){
        cardMappingDAO.updateCardmarketIdOnScryfallCard();
    }

    // Mapear cardtrader_card desde scryfall_card
    public void mapCardtraderCard(){
        cardMappingDAO.updateCardmarketIdOnCardtraderCard();
    }

    // Inserto scryfall_id de scryfall_card en card_mapping
    public void insertScryfallId(){
        cardMappingDAO.initializeCardMapping();
    }

    // Relaciono scryfall_card con cardtrader_card mediante cardmarket_id en card_mapping
    public void mapCardmarketIdFromScryfallToCardMapping(){
        cardMappingDAO.mappingCardmarketIdToCardMappingFromScryfallCard();
    }

    // Relaciono cardtrader_card con scryfall_card mediante cardmarket_id en card_mapping
    public void mapCardmarketIdFromCardtraderCardToCardMapping(){
        cardMappingDAO.mappingCardmarketIdToCardMappingFromCardtraderCard();
    }

    public void lastJoin(){
        cardMappingDAO.lastJoin1();
        cardMappingDAO.lastJoin2();
    }
}
