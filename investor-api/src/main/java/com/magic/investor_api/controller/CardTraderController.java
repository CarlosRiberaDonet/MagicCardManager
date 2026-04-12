package com.magic.investor_api.controller;

import com.magic.investor_api.API.CardTraderAPI;
import com.magic.investor_api.model.Expansion;
import com.magic.investor_api.service.CardVariantService;
import com.magic.investor_api.service.ExpansionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trader")
public class CardTraderController {

    private final CardTraderAPI cardTraderApi;
    private final ExpansionService expansionService;
    private final CardVariantService cardVariantService;

    public CardTraderController(CardTraderAPI cardTraderApi, ExpansionService expansionService, CardVariantService cardVariantService) {
        this.cardTraderApi = cardTraderApi;
        this.expansionService = expansionService;
        this.cardVariantService = cardVariantService;
    }

    // Método para descargar ediciones desde CardTrader
    @GetMapping("/edition")
    public ResponseEntity<List<Expansion>> downloadEdition() {

        List<Expansion> expansions = expansionService.importExpansion();

        return ResponseEntity.ok(expansions);
    }

    // Obtener todas las cartas de cada edicion
    @GetMapping("/download/cards")
    public ResponseEntity<String> getBlueprints() {
        cardVariantService.getCardVariantList();

        return ResponseEntity.ok();
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
