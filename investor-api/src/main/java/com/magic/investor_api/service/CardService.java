package com.magic.investor_api.service;

import com.magic.investor_api.dao.CardDAO;
import com.magic.investor_api.dto.CardDTO;
import com.magic.investor_api.dto.CardPageDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {


    private final CardDAO cardDAO;
    //private final CardPageDTO cardPageDTO;

    public CardService(CardDAO cardDAO){
        this.cardDAO = cardDAO;
    }

    // Obtiene lista de cartas mediante su nombre
    public CardPageDTO getCardByName(String name, int page, int size){

        int totalCards = cardDAO.countCardsByName(name);
        List<CardDTO> cardListDTO = cardDAO.selectCardByName(name, page, size);
        CardPageDTO cardPageDTO = new CardPageDTO(totalCards, page, size, cardListDTO);
        return cardPageDTO;
    }

    // Actualizar cardmarket_id de la tabla card
    public void updateCardMarketId(){
        cardDAO.updateCardmarketId();
    }
}
