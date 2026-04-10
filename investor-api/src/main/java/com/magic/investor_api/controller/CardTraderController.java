package com.magic.investor_api.controller;

import com.magic.investor_api.dao.CardTraderAPI;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trader")
public class CardTraderController {

    private final CardTraderAPI cardTraderApi;

    public CardTraderController(CardTraderAPI cardTraderApi) {
        this.cardTraderApi = cardTraderApi;
    }

    // Prueba directa: devuelve el JSON tal cual viene de CardTrader
    @GetMapping("/card")
    public String getProducts(
            @RequestParam String blueprintId,
            @RequestParam(defaultValue = "1") int page
    ) {
        return cardTraderApi.fetchCardProducts(blueprintId);
    }
}
