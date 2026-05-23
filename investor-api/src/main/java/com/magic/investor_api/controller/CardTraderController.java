package com.magic.investor_api.controller;

import com.magic.investor_api.API.CardTraderAPI;
import com.magic.investor_api.service.CardTraderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    // Obtener carta mediante bluteprint
    @GetMapping("/card")
    public String getProducts(
            @RequestParam String blueprintId,
            @RequestParam(defaultValue = "1") int page
    ) {
        return cardTraderApi.fetchCardProducts(blueprintId, page);
    }
}
