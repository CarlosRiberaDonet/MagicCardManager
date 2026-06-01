package com.magic.investor_api.cardtrader_price_cache.controller;

import com.magic.investor_api.cardtrader_price_cache.service.CardtraderListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("pricecache")
@RequiredArgsConstructor
public class CardtraderPriceCacheController {

    private final CardtraderListingService cardtraderListingService;

    // Obtener carta mediante bluteprint
    @GetMapping("/scryfallId")
    public String getProducts(@RequestParam String scryfallId) {

        return cardtraderListingService.mapNodeToCardtraderListing(283L, scryfallId);
    }
}
