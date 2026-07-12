package com.magic.investor_api.cardmarketPrice.controller;

import com.magic.investor_api.api.CardmarketDownloader;
import com.magic.investor_api.cardmarketPrice.service.CardmarketPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
public class CardmarketPriceController {

    private final CardmarketDownloader cardmarketDownloader;
    private final CardmarketPriceService cardmarketPriceService;

    // Descargar el price guide de cardmarket
    @PostMapping("/update")
    public ResponseEntity<String> startDownloadGuidePrices() throws IOException {

        System.out.println("Descargando precios desde cardmarket");
        cardmarketDownloader.downloadGuidePrice();
        System.out.println("Actualizando lista de precios...");
        cardmarketPriceService.importGuidePricesToBD();

        return ResponseEntity.ok("Base de datos actualizada.");
    }

    // Obtener precios de la tabla cardmaket_price
}
