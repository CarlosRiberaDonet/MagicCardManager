package com.magic.investor_api.downloader;

import com.magic.investor_api.client.ScryfallApi;
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

    private final ScryfallApi scryfallApi;
    private final ScryfallImportService importService;
    private static final String URL_SCRYFALL = "https://data.scryfall.io/all-cards/all-cards-20260401092717.json";
    private static final String PATH_LOCAL = "D:/Proyectos/MagicCardManager/cards.json";

    public void startFullImport() {

        System.out.println("Iniciando descarga desde Scryfall...");

        try {
            System.out.println("Iniciando descarga desde Cardmarket S3...");

            URL url = new URL(URL_SCRYFALL);
            try (InputStream in = url.openStream();
                 FileOutputStream out = new FileOutputStream(PATH_LOCAL)) {

                // Copia el contenido del stream de internet al archivo en D:/
                in.transferTo(out);
            }

            System.out.println("Archivo guardado con éxito en: " + PATH_LOCAL);

        } catch (IOException e) {
            System.err.println("Error al descargar el JSON de precios: " + e.getMessage());
        }
    }
}
