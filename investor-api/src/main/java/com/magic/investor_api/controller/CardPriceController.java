package com.magic.investor_api.controller;

import com.magic.investor_api.API.CardmarketDownloader;
import com.magic.investor_api.service.CardmarketImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
public class CardPriceController {

    private final CardmarketDownloader cardmarketDownloader;
    private final CardmarketImportService cardmarketService;
    private final String basePath = System.getProperty("user.dir");

    // Descargar el price guide de cardmarket
    @PostMapping("/import")
    public ResponseEntity<String> startImportGuidePrices(){
        try {

            cardmarketDownloader.downloadGuidePrice();

            return ResponseEntity.ok("Proceso iniciado. Revisa la consola de IntelliJ para ver el progreso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al conectar con cardmarket: " + e.getMessage());
        }
    }

    // Actualizar precios de card_price
    @PostMapping("/update")
    public ResponseEntity<String> updatePrices(){
        try {
            System.out.println("Actualizando lista de precios...");

            String GUIDE_PRICES_JSON_PATH = basePath + "/src/main/resources/guide-prices.json";
            cardmarketService.importToDatabase(GUIDE_PRICES_JSON_PATH);

            System.out.println("¡Proceso total finalizado con éxito!");

        } catch (Exception e) {
            System.err.println("Error en el proceso: " + e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok("Base de datos actualizada.");
    }
}
