package com.magic.investor_api.cardtraderPrice.controller;

import com.magic.investor_api.auth.JwtService;
import com.magic.investor_api.cardtrader.service.CardTraderService;
import com.magic.investor_api.cardtraderPrice.dto.CardtraderPriceDTO;
import com.magic.investor_api.cardtraderPrice.service.CardtraderPriceService;
import com.magic.investor_api.scryfall.dto.ScryfallCardDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<?> getProducts(
            @RequestParam Long cardId,
            @RequestParam String scryfallId,
            @RequestParam String lang,
            @RequestParam String condition,
            @RequestParam boolean isFoil) {

        ScryfallCardDTO dto = new ScryfallCardDTO();
        dto.setId(cardId);
        dto.setScryfallId(scryfallId);
        dto.setLang(lang);
        dto.setCondition(condition);
        dto.setFoil(isFoil);
        System.out.println("OBTENIENDO PRECIO: " + dto);
        if (cardtraderPriceService.updateCardtraderPrices(dto)) {

            CardtraderPriceDTO price = cardtraderPriceService.getCardtraderPrice(dto);
            System.out.println(price);

            return ResponseEntity.ok(price);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "message", "No se encontraron precios"
                ));
    }
}
