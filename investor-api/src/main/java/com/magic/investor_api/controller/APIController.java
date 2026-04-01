package com.magic.investor_api.controller;


import com.magic.investor_api.client.ScryfallApi;
import com.magic.investor_api.service.ScryfallOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/scryfall")
@RequiredArgsConstructor
public class APIController {

    private final ScryfallApi scryfallApi;
    private final ScryfallOrchestrator orchestrator;

    @PostMapping("/import-all")
    public ResponseEntity<String> startImport(@RequestParam String path) {
        try {
            // El controlador delega TODA la responsabilidad al orquestador
            orchestrator.startFullImport(path);

            return ResponseEntity.ok("Proceso iniciado. Revisa la consola de IntelliJ para ver el progreso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al conectar con Scryfall: " + e.getMessage());
        }
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopImport() {
        scryfallApi.stopDownload("scryfall");
        return ResponseEntity.ok("Se ha enviado la señal de parada a la descarga de Scryfall.");
    }
}
