package com.magic.investor_api.controller;

import com.magic.investor_api.dto.ScryfallCardDTO;
import com.magic.investor_api.dto.CardPageDTO;
import com.magic.investor_api.service.CardService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards")

public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService){
        this.cardService = cardService;
    }

    // Buscar carta por nombre
    @GetMapping("/search")
    public CardPageDTO searchCards(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String setCode,  // Edición
            @RequestParam(required = false) String rarity,   // Rareza
            @RequestParam(required = false) String lang,     // Idioma
            @RequestParam(required = false) String typeLine, // Tipo de carta
            @RequestParam(required = false) Double minPrice, // Precio mínimo
            @RequestParam(required = false) Double maxPrice, // Precio máximo
            @RequestParam(required = false) String orderBy,   // "price_asc" o "price_desc"
            @RequestParam(required = false) boolean hideNA, // false -> muestra cartas con precio N/A, true oculta
            @RequestParam int page,
            @RequestParam int size
    ) {
        return cardService.searchCards(name, setCode, rarity, lang, typeLine, orderBy,
                minPrice, maxPrice, page, size, hideNA);
    }

    // Buscar carta por id
    @GetMapping("id")
    public ScryfallCardDTO searchCardById(@RequestParam Long cardId){

        return cardService.getCardById(cardId);
    }
}
