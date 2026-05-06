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

    // Obtiene lista de cartas mediante su nombre
    public CardPageDTO searchCards(String name, String rarity, String lang, String typeLine, int page, int size) {

        // Calcula la fila de inicio según la página — ej: página 2 con 20 resultados empieza en la fila 20
        int offset = (page - 1) * size;
        // Total de cartas que coinciden con la búsqueda
        int totalCards = scryfallCardDAO.countCardsByName(name, rarity, lang, typeLine);
        // Busco la carta mediante su nombre
        List<ScryfallCardDTO> cardListDTO = scryfallCardDAO.selectCardByName(name, rarity, lang, typeLine, page, size, offset);
        cardPageDTO = new CardPageDTO(totalCards, page, cardListDTO);
        return cardPageDTO;
    }

    // Obtiene carta con datos completos mediante su id
    public ScryfallCardDTO getCardById(Long cardId){
        return scryfallCardDAO.getScryfallCardById(cardId);
    }
}
