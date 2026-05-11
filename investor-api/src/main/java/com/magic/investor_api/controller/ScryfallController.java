package com.magic.investor_api.controller;


import com.magic.investor_api.API.ScryfallAPI;
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
public class ScryfallController {

    private final ScryfallService scryfallImportService;


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
    // Descargar JSON de scryfall
    @PostMapping("/cards")
    public ResponseEntity<String> cards() {
        try {
            System.out.println("Proceso de descarga iniciado...");
            scryfallImportService.importScryfallEditionsToDB();
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

    // Volcar JSON a BD
    /*@PostMapping("/sync")
    public ResponseEntity<String> importJsonToDB(){
        try {
            System.out.println("Iniciando procesado de JSON a BD...");

            scryfallImportService.importScryfallCardsToBD();

            System.out.println("¡Proceso total finalizado con éxito!");

        } catch (Exception e) {
            System.err.println("Error en el proceso: " + e.getMessage());
            System.out.println("Erro en ScryfallController importJsonToDB:");
            e.printStackTrace();
        }
        return ResponseEntity.ok("Base de datos actualizada.");
    }*/


}
