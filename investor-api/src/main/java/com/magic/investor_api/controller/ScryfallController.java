package com.magic.investor_api.controller;


import com.magic.investor_api.dao.ExpansionDAO;
import com.magic.investor_api.model.CardPrice;
import com.magic.investor_api.model.ScryfallSet;
import com.magic.investor_api.service.ScryfallService;
import com.magic.investor_api.service.SetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/scryfall")
@RequiredArgsConstructor
public class ScryfallController {

    private final ScryfallService scryfallImportService;
    private final SetService setService;

    // Descargar ediciones desde scryfall
    @PostMapping("/editions")
    public ResponseEntity<String> editions(){
        try{
            System.out.println("Proceso de descarga iniciado...");
            scryfallImportService.importScryfallEditionsToDB();
            return ResponseEntity.ok("Descarga completada.");
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al conectar con Scryfall: " + e.getMessage());
        }
    }

    // Descargar cartas de scryfall e importarlas a la BD
    @PostMapping("/cards")
    public ResponseEntity<String> cards() {
        try {
            System.out.println("Proceso de descarga iniciado...");
            scryfallImportService.importScryfallCardsToBD();
            return ResponseEntity.ok("Descarga completada.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al conectar con Scryfall: " + e.getMessage());
        }
    }

    // Actualizar precios de la tabla scryfall_card
    @PostMapping("/update-prices")
    public void updatePrices(){
        scryfallImportService.updateScryfallPrices();
    }

    // Obtiene y devuelve lista de expansiones de scryfall_set
    @GetMapping("/sets")
    public List<ScryfallSet> getSetsList(){
        return setService.getSets();
    }
}
