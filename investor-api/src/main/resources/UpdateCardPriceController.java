package com.magic.investor_api.controller;


import com.magic.investor_api.dao.CardPriceDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("scraper")
public class UpdateCardPriceController {

    private final CardPriceDAO cardPriceDAO;
    private final CardmarketScrapperService cardmarketScrapperService;

    public UpdateCardPriceController(CardPriceDAO cardPriceDAO,
                                     CardmarketScrapperService cardmarketScrapperService){
        this.cardPriceDAO = cardPriceDAO;
        this.cardmarketScrapperService = cardmarketScrapperService;
    }

    // Actualizar precio desde cardmarket
    @PostMapping("cardmarket")
    public ResponseEntity<Void> updatePrice(@RequestParam Long cardId){
        cardmarketScrapperService.getCardmarketPrice(cardId);
        return ResponseEntity.accepted().build();
    }
}
