package com.magic.investor_api.controller;


import com.magic.investor_api.API.ScryfallDownloader;
import com.magic.investor_api.service.ScryfallImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;


@RestController
@RequestMapping("/scryfall")
@RequiredArgsConstructor
public class ScryfallController {

    private final ScryfallDownloader scryfallDownloader;
    private final ScryfallImportService scryfallImportService;


    // Descargar JSON de scryfall
    @PostMapping("/import")
    public ResponseEntity<String> startImport() {
        try {
            System.out.println("Proceso de descarga iniciado...");
            scryfallDownloader.startFullImport();

            return ResponseEntity.ok("Descarga completada.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al conectar con Scryfall: " + e.getMessage());
        }
    }

    // Volcar JSON a BD
    @PostMapping("/sync")
    public ResponseEntity<String> importJsonToDB(){
        try {
            System.out.println("Iniciando procesado de JSON a BD...");

            InputStream input = getClass()
                    .getClassLoader()
                    .getResourceAsStream("cards.json");
            scryfallImportService.importToDatabase(input);

            System.out.println("¡Proceso total finalizado con éxito!");

        } catch (Exception e) {
            System.err.println("Error en el proceso: " + e.getMessage());
            System.out.println("Erro en ScryfallController importJsonToDB:");
            e.printStackTrace();
        }
        return ResponseEntity.ok("Base de datos actualizada.");
    }
}
