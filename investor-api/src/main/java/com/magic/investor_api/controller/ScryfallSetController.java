package com.magic.investor_api.controller;


import com.magic.investor_api.service.ScryfallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/scryfall")
@RequiredArgsConstructor
public class ScryfallSetController {

    private final ScryfallService scryfallImportService;

    // Descargar ediciones desde scryfall e importarlas a la BD
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

    @PostMapping("update-prices")
    public void updatePrices(){

        scryfallImportService.updateScryfallPrices();
    }
}
