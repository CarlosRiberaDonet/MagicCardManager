package com.magic.investor_api.cardtrader.controller;

import com.magic.investor_api.cardtrader.service.CardTraderService;
import lombok.RequiredArgsConstructor;
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

    // Obtener precios actualizados
    /*@GetMapping("/cardId/{cardId}/scryfallId{scryfallId}")
    public CardPrice updateCardtraderPrice(@PathVariable Long cardId, @PathVariable String scryfallId){// Obtengo el cardtraderId

        return cardTraderService.mapNodeToCardtraderListing(cardId, scryfallId); // Obtengo lista de cartas mediante cardtrader_id
    }*/
}
