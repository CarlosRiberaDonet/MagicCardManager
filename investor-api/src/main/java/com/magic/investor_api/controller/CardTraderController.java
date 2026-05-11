package com.magic.investor_api.controller;

import com.magic.investor_api.API.CardTraderAPI;
import com.magic.investor_api.model.Expansion;
import com.magic.investor_api.service.ExpansionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trader")
public class CardTraderController {

    private final CardTraderAPI cardTraderApi;
    private final ExpansionService expansionService;

    public CardTraderController(CardTraderAPI cardTraderApi, ExpansionService expansionService) {
        this.cardTraderApi = cardTraderApi;
        this.expansionService = expansionService;
    }

    // Método para descargar ediciones desde CardTrader
    /*@GetMapping("/edition")
    public void downloadEdition() {
        expansionService.importExpansion();
    }

    // Obtener todas las cartas de cada expansión
    @GetMapping("/cards")
    public void getBlueprints() {
        expansionService.cardsByExpansion();
    }

    @GetMapping("/card")
    public String getProducts(
            @RequestParam String blueprintId,
            @RequestParam(defaultValue = "1") int page
    ) {
        return cardTraderApi.fetchCardProducts(blueprintId, page);
    }*/
}
