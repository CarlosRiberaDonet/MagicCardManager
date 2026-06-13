package com.magic.investor_api.cardtraderPrice.controller;

import com.magic.investor_api.auth.JwtService;
import com.magic.investor_api.cardtrader.service.CardTraderService;
import com.magic.investor_api.cardtraderPrice.dto.CardtraderPriceDTO;
import com.magic.investor_api.cardtraderListing.service.CardtraderListingService;
import com.magic.investor_api.scryfall.service.ScryfallService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("pricecache")
@RequiredArgsConstructor
public class CardtraderPriceController {

    private final CardTraderService cardTraderService;
    private final ScryfallService scryfallService;
    @Autowired
    JwtService jwtService;

    // Obtener carta de cartrader mediante scryfallId
   /* @GetMapping("/{scryfallId}")
    public CardtraderPriceDTO getProducts(@PathVariable String scryfallId,
                                          @PathVariable String lang) {

        System.out.println("ACTUALIZANDO PRECIO CARDTRADER");

        cardTraderService.getCardByscryfallId();

    }*/
}
