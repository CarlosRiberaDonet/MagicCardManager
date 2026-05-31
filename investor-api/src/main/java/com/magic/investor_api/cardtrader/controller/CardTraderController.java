package com.magic.investor_api.cardtrader.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.magic.investor_api.cardtrader.ports.CardTraderAPI;
import com.magic.investor_api.cardtrader.service.CardTraderService;
import com.magic.investor_api.model.CardPrice;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cardtrader")
@RequiredArgsConstructor
public class CardTraderController {

    private final CardTraderAPI cardTraderApi;
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

    // Obtener precios actualizados
    @GetMapping("scryfallId")
    public CardPrice updateCardtraderPrice(@RequestParam Long cardId, @RequestParam String scryfallId){// Obtengo el cardtraderId

        return cardTraderService.mapNodeToCardtraderListing(cardId, scryfallId); // Obtengo lista de cartas mediante cardtrader_id
    }

    // Obtener carta mediante bluteprint
    /*@GetMapping("/card")
    public String getProducts(
            @RequestParam Long blueprintId,
            @RequestParam(defaultValue = "1") int page
    ) {
        return cardTraderApi.fetchCardProducts(blueprintId);
    }*/
}
