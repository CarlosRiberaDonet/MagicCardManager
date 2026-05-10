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
            @RequestParam(required = false) String setName,
            @RequestParam(required = false) String rarity,
            @RequestParam(required = false) String lang,
            @RequestParam(required = false) String typeLine,
            @RequestParam int page,
            @RequestParam int size
    ) {
        return cardService.searchCards(name, setName, rarity, lang, typeLine, page, size);
    }

    // Buscar carta por id
    @GetMapping("id")
    public ScryfallCardDTO searchCardById(@RequestParam Long cardId){

        return cardService.getCardById(cardId);
    }
}
