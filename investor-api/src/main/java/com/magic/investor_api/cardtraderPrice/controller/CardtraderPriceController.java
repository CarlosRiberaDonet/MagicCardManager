package com.magic.investor_api.cardtraderPrice.controller;

import com.magic.investor_api.auth.JwtService;
import com.magic.investor_api.cardtrader.service.CardTraderService;
import com.magic.investor_api.cardtraderListing.model.CardtraderListing;
import com.magic.investor_api.cardtraderPrice.dto.CardtraderPriceDTO;
import com.magic.investor_api.cardtraderPrice.service.CardtraderPriceService;
import com.magic.investor_api.scryfall.dto.ScryfallCardDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("pricecache")
@RequiredArgsConstructor
public class CardtraderPriceController {

    private final CardTraderService cardTraderService;
    private final CardtraderPriceService cardtraderPriceService;
    @Autowired
    JwtService jwtService;

    // Actualizar lista de precios
   @PostMapping("/getPrices")
    public CardtraderPriceDTO getProducts(HttpServletRequest httpRequest,
                                          @RequestBody ScryfallCardDTO request) {
        // Obtener cardtraderId
        System.out.println("Obteniendo cardtraderId de scryfalllId: " + request.getScryfallId());
        // Actualizo lista de precios de la carta
        cardtraderPriceService.updateCardtraderPrices(request);
        System.out.println(request);

        // Obtengo precio de la carta en cardtrader_price
        return cardtraderPriceService.getCardtraderPrice(request);
    }
}
