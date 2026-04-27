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
    public CardPageDTO searchCardByName(
            @RequestParam String name,
            @RequestParam int page,
            @RequestParam int size) {
        return cardService.getCardByName(name, page, size);
    }

    // Buscar carta por UUID scryfall
    @GetMapping("id")
    public ScryfallCardDTO searchCardById(@RequestParam Long cardId){
        return cardService.getCardById(cardId);
    }

    // Mapear cardmarketId desde cartrader_card a scryfall_card
    @PutMapping("update")
    public void updateCardmarketId() {

        cardService.updateCardMarketId();
    }
}
