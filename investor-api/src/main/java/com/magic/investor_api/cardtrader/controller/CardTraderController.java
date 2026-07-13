package com.magic.investor_api.cardtrader.controller;

import com.magic.investor_api.cardtrader.service.CardTraderService;
import com.magic.investor_api.cardtraderPrice.dto.CardtraderPriceDTO;
import com.magic.investor_api.scryfall.dto.ScryfallCardDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/cardtrader")
@RequiredArgsConstructor
public class CardTraderController {

    private final CardTraderService cardTraderService;

    // Descargar ediciones desde CardTrader
    @GetMapping("/edition")
    public void downloadCardtradesExpanion() {
        cardTraderService.downloadCardtraderExpansion();
    }

    // Obtener todas las cartas de cada expansión
    @GetMapping("/cards")
    public void getBlueprints() {
        cardTraderService.cardsByExpansion();
    }

    // Obtener carta con precios de cardtrader
    @PostMapping("/lastPrices")
    public ResponseEntity<?> selectCardtraderCard(HttpServletRequest httpRequest,
                                                @RequestParam Long cardId,
                                                @RequestParam String scryfallId,
                                                @RequestParam String lang,
                                                @RequestParam String condition,
                                                @RequestParam boolean isFoil
    ){

        // Debo obtener los precios de cardtrader para cardDetail 138
        System.out.println("Obtener precios de: " + cardId + " " + scryfallId +  " " + lang + " " + " " + condition + " " + isFoil);
        CardtraderPriceDTO priceDTO = new CardtraderPriceDTO();
        priceDTO = cardTraderService.getCardtraderPrices(cardId, scryfallId, lang, condition, isFoil);

        if (priceDTO == null) {
            return ResponseEntity.noContent().build(); // 204
        }

        System.out.println(priceDTO);
        return ResponseEntity.ok(priceDTO);
    }
}
