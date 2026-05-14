package com.magic.investor_api.controller;

import com.magic.investor_api.API.CardmarketDownloader;
import com.magic.investor_api.service.CardmarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
public class CardPriceController {

    private final CardmarketDownloader cardmarketDownloader;
    private final CardmarketService cardmarketService;

    // Descargar el price guide de cardmarket
    @PostMapping("/download")
    public ResponseEntity<String> startDownloadGuidePrices(){
        try {

            cardmarketDownloader.downloadGuidePrice();

            return ResponseEntity.ok("Proceso de descarga iniciado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al conectar con cardmarket: " + e.getMessage());
        }
    }

    // Actualizar precios de card_price
    @PostMapping("/import")
    public ResponseEntity<String> updatePrices(){
        try {
            System.out.println("Actualizando lista de precios...");

            cardmarketService.importGuidePricesToBD();

            System.out.println("¡Proceso total finalizado con éxito!");

        } catch (Exception e) {
            System.err.println("Error en el proceso: " + e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok("Base de datos actualizada.");
    }
}
