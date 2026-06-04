package com.magic.investor_api.cardtraderPrice.controller;

import com.magic.investor_api.auth.JwtService;
import com.magic.investor_api.cardtraderPrice.dto.CardtraderPriceDTO;
import com.magic.investor_api.cardtraderListing.service.CardtraderListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("pricecache")
@RequiredArgsConstructor
public class CardtraderPriceController {

    private final CardtraderListingService cardtraderListingService;
    @Autowired
    JwtService jwtService;

    // Obtener carta de cartrader mediante scryfallId
    @GetMapping("/{scryfallId}")
    public CardtraderPriceDTO getProducts(@PathVariable String scryfallId,
                                          @RequestParam String lang) {

        System.out.println("ACTUALIZANDO PRECIO CARDTRADER");
        return cardtraderListingService.updateCardPrices(scryfallId, lang);

    }
}
