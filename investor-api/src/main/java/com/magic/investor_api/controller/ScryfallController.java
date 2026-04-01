package com.magic.investor_api.controller;


import com.magic.investor_api.downloader.ScryfallDownloader;
import com.magic.investor_api.service.ScryfallImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/scryfall")
@RequiredArgsConstructor
public class ScryfallController {

    private final ScryfallDownloader scryfallDownloader;
    private final ScryfallImportService scryfallImportService;

    @PostMapping("/import-all")
    public ResponseEntity<String> startImport() {
        try {
            // El controlador delega TODA la responsabilidad al orquestador
            scryfallDownloader.startFullImport();

            return ResponseEntity.ok("Proceso iniciado. Revisa la consola de IntelliJ para ver el progreso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al conectar con Scryfall: " + e.getMessage());
        }
    }

    @PostMapping("/sync")
    public ResponseEntity<String> importJsonToDB(){
        try {
            System.out.println("Iniciando procesado de JSON a BD...");

            String SCRYFALL_JSON_PATH = "D:/Proyectos/MagicCardManager/cards.json";
            scryfallImportService.importToDatabase(SCRYFALL_JSON_PATH);

            System.out.println("¡Proceso total finalizado con éxito!");

        } catch (Exception e) {
            System.err.println("Error en el proceso: " + e.getMessage());
            System.out.println("Erro en ScryfallOtchestator startFullImport:");
            e.printStackTrace();
        }
        return ResponseEntity.ok("Base de datos actualizada.");
    }
}
