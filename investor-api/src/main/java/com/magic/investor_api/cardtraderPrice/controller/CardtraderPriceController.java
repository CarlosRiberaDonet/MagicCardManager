package com.magic.investor_api.cardtraderPrice.controller;

import com.magic.investor_api.auth.JwtService;
import com.magic.investor_api.cardtrader.service.CardTraderService;
import com.magic.investor_api.cardtraderListing.model.CardtraderListing;
import com.magic.investor_api.cardtraderPrice.dto.CardtraderPriceDTO;
import com.magic.investor_api.cardtraderPrice.service.CardtraderPriceService;
import com.magic.investor_api.scryfall.service.ScryfallService;
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
                                          @RequestBody CardtraderListing request) {
        // Obtener cardtraderId
        System.out.println("Obteniendo cardtraderId de scryfalllId: " + request.getScryfallId());
        Long cardTraderId = cardTraderService.getCardtraderIdByScryfallId(request.getScryfallId());
        if(cardTraderId == -1){
            System.out.println("No se encontraron precios para la carta.");
            return null;
        }
        // Asigno cardtraderId a la carta
        request.setCardtraderId(cardTraderId);
        // Obtengo lista de precios de la carta
        cardtraderPriceService.getCardtraderPrices(request);
       System.out.println(request);
        return cardtraderPriceService.getCardtraderPrice(request);
    }
}
