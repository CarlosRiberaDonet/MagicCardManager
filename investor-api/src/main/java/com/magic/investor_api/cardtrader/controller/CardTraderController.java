package com.magic.investor_api.cardtrader.controller;

import com.magic.investor_api.cardtrader.service.CardTraderService;
import com.magic.investor_api.cardtraderPrice.model.CardtraderPrice;
import com.magic.investor_api.scryfall.dto.ScryfallCardDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        ScryfallCardDTO dto = new ScryfallCardDTO();
        dto.setId(cardId);
        dto.setScryfallId(scryfallId);
        dto.setLang(lang);
        dto.setCondition(condition);
        dto.setFoil(isFoil);
        dto = cardTraderService.getCardtraderPrices(dto);

        return ResponseEntity.ok(dto);
    }
}
