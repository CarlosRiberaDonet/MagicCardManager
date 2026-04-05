package com.magic.investor_api.service;

import com.magic.investor_api.dao.CardDAO;
import com.magic.investor_api.dto.CardDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {


    private final CardDAO cardDAO;

    public CardService(CardDAO cardDAO){
        this.cardDAO = cardDAO;
    }

    // Obtiene lista de cartas mediante su nombre
    public List<CardDTO> getCardByName(String name, int page, int size){

        List<CardDTO> cardListDTO = cardDAO.selectCardByName(name, page, size);
        int totalCards = cardDAO.countCardsByName(name);
        return cardListDTO;
    }
}
