package com.magic.investor_api.controller;

import com.magic.investor_api.dto.CardDTO;
import com.magic.investor_api.dto.CardPageDTO;
import com.magic.investor_api.service.CardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cards")

public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService){
        this.cardService = cardService;
    }

    @GetMapping("/search")
    public CardPageDTO searchCardByName(@RequestParam String name, @RequestParam int page, @RequestParam int size){

        return cardService.getCardByName(name, page, size);
    }
}
