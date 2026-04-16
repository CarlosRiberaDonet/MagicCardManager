package com.magic.investor_api.API;

import com.magic.investor_api.service.ScryfallImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class ScryfallDownloader {

    private final ScryfallImportService importService;
    private static final String URL_SCRYFALL = "https://data.scryfall.io/all-cards/all-cards-20260401092717.json";
    private final String basePath = System.getProperty("user.dir");
    String CARDS_PRICES_JSON_PATH = basePath + "/src/main/resources/guide-prices.json";

    public void startFullImport() {

        try {

            URL url = new URL(URL_SCRYFALL);
            try (InputStream in = url.openStream();
                 FileOutputStream out = new FileOutputStream(CARDS_PRICES_JSON_PATH)) {

                // Copia el contenido del stream de internet al archivo en D:/
                in.transferTo(out);
            }

            System.out.println("Archivo guardado con éxito en: " + CARDS_PRICES_JSON_PATH);

        } catch (IOException e) {
            System.err.println("Error al descargar el JSON de precios: " + e.getMessage());
        }
    }
}
