package com.magic.investor_api.API;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class ScryfallAPI {
    
    private static final String SCRYFALL_EDITIONS = "https://api.scryfall.com/sets";
    private static final String SCRYFALL_ALL_CARDS = "https://data.scryfall.io/all-cards/all-cards-20260401092717.json";
    private final String basePath = System.getProperty("user.dir");
    String EDITIONS_PATH = basePath + "/src/main/resources/editions.json";
    String CARDS_PATH = basePath + "/src/main/resources/cards.json";

    // Descarga ediciones desde scryfall y guarda en formato JSON
    public void getEditions(){
        try{
           URL url = new URL(SCRYFALL_EDITIONS);
           try(InputStream in = url.openStream()){
               FileOutputStream out = new FileOutputStream(EDITIONS_PATH);
               System.out.println("Iniciando descarga de ediciones desde scryfall...");
               // Copia el contenido del stream de scryfall al directorio EDITIONS
               in.transferTo(out);
           }
            System.out.println("Archivo guardado con éxito en: " + EDITIONS_PATH);
        }catch (IOException e) {
            System.err.println("Error al descargar el JSON de ediciones: " + e.getMessage());
        }
    }

    // Descarga las cartas desde scryfall y guarda en formato JSON
    public void downloadCards() {
        try {
            URL url = new URL(SCRYFALL_ALL_CARDS);
            try (InputStream in = url.openStream();
                FileOutputStream out = new FileOutputStream(CARDS_PATH)) {
                System.out.println("Iniciando descarga de cartas desde scryfall...");
                // Copia el contenido del stream de scryfall al directorio CARDS
                in.transferTo(out);
            }

            System.out.println("Archivo guardado con éxito en: " + CARDS_PATH);

        } catch (IOException e) {
            System.err.println("Error al descargar el JSON de cartas: " + e.getMessage());
        }
    }
}
