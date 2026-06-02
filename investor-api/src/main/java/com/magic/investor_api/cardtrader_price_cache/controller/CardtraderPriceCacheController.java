package com.magic.investor_api.cardtrader_price_cache.controller;

import com.magic.investor_api.auth.JwtService;
import com.magic.investor_api.cardtrader_price_cache.CardtraderPriceCacheDTO;
import com.magic.investor_api.cardtrader_price_cache.service.CardtraderListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("pricecache")
@RequiredArgsConstructor
public class CardtraderPriceCacheController {

    private final CardtraderListingService cardtraderListingService;
    @Autowired
    JwtService jwtService;

    // Obtener carta mediante bluteprint
    @GetMapping("/{scryfallId}")
    public CardtraderPriceCacheDTO getProducts(@PathVariable String scryfallId,
                                               @RequestParam String lang,
                                               @RequestParam  String condition,
                                               @RequestParam  boolean isFoil) {

        System.out.println("ACTUALIZANDO PRECIO CARDTRADER");
        CardtraderPriceCacheDTO dto = cardtraderListingService.updateCardPrices(scryfallId, lang, condition, isFoil);
        if(dto == null){
            System.out.println("LA CARTA NO TIENE CARDTRADERID");
            return null;
        }
        System.out.println(dto.toString());
        return dto;
    }
}
