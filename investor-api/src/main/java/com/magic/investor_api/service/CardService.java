package com.magic.investor_api.service;

import com.magic.investor_api.dao.ScryfallCardDAO;
import com.magic.investor_api.dto.ScryfallCardDTO;
import com.magic.investor_api.dto.CardPageDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {

    private final ScryfallCardDAO scryfallCardDAO;
    private CardPageDTO cardPageDTO;

    public CardService(ScryfallCardDAO cardDAO){
        this.scryfallCardDAO = cardDAO;
    }

    // Actualizar cardmarket_id de la tabla card
    public void updateCardMarketId(){
        scryfallCardDAO.updateCardmarketId();
    }

    // Obtiene lista de cartas mediante su nombre
    public CardPageDTO getCardByName(String name, int page, int size) {

        // Total de cartas que coinciden con la búsqueda
        int totalCards = scryfallCardDAO.countCardsByName(name);
        // Busco la carta mediante su nombre
        List<ScryfallCardDTO> cardListDTO = scryfallCardDAO.selectCardByName(name, page, size);
        cardPageDTO = new CardPageDTO(totalCards, page, cardListDTO);
        return cardPageDTO;
    }

    // Obtiene carta con datos completos mediante su id
    public ScryfallCardDTO getCardById(Long cardId){

        return scryfallCardDAO.getScryfallCardById(cardId);
    }
}
