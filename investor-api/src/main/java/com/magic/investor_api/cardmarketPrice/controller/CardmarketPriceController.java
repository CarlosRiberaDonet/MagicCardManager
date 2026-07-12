package com.magic.investor_api.cardmarketPrice.controller;

import com.magic.investor_api.api.CardmarketDownloader;
import com.magic.investor_api.cardmarketPrice.model.CardmarketPrice;
import com.magic.investor_api.cardmarketPrice.service.CardmarketPriceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/cardmarket")
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
    @GetMapping("/{cardId}")
    public ResponseEntity<?> getCardmarketPrice(@PathVariable Long cardId) {

        Long cardmarketId = cardmarketPriceService.getCarmarketId(cardId);
        if (cardmarketId == null) {
            return ResponseEntity.notFound().build();
        }

        CardmarketPrice price = cardmarketPriceService.getCardmarketPriceByCardmarketId(cardmarketId);

        if (price == null) {
            return ResponseEntity.noContent().build(); // 204
        }

        return ResponseEntity.ok(price);
    }
}