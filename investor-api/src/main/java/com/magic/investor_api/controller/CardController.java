package com.magic.investor_api.controller;

import com.magic.investor_api.dto.CardDTO;
import com.magic.investor_api.dto.CardPageDTO;
import com.magic.investor_api.service.CardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")

public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService){
        this.cardService = cardService;
    }

    // Buscar carta por nombre
    @GetMapping("/search")
    public CardPageDTO searchCardByName(@RequestParam String name, @RequestParam int page, @RequestParam int size){

        return cardService.getCardByName(name, page, size);
    }

    // Buscar carta por UUID scryfall
    @GetMapping("id")
    public CardDTO searchCardById(@RequestParam String s){

        return null;
        // return cardService.getCardById(id);
    }
    @PutMapping("update")
    public void updateCardmarketId() {
        cardService.updateCardMarketId();
    }
}
