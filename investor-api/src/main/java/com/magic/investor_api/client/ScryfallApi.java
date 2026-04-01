package com.magic.investor_api.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Component
public class ScryfallApi {

    private static final String SCRYFALL_METADATA_URL = "https://api.scryfall.com/bulk-data/default-cards";
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Almacén de "tickets de cancelación". La clave es un ID que tú elijas (ej: "scryfall" o "cardmarket")
    private final Map<String, AtomicBoolean> activeDownloads = new ConcurrentHashMap<>();

    /**
     * MÉTODO UNIVERSAL: Descarga cualquier URL a un destino local.
     */
    //@Async
    public void downloadFile(String downloadId, String urlSource, String destinationPath, Consumer<Double> onProgressUpdate) {
        // Creamos el flag de cancelación y lo registramos
        AtomicBoolean cancelRequested = new AtomicBoolean(false);
        activeDownloads.put(downloadId, cancelRequested);

        try {
            URL url = new URL(urlSource);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (InputStream inputStream = conn.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(destinationPath)) {

                byte[] buffer = new byte[16384]; // 16KB para mayor velocidad en archivos grandes
                int bytesRead;
                long totalBytesRead = 0;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    // Si el mapa dice que este ID debe cancelarse, paramos
                    if (cancelRequested.get()) {
                        System.out.println("Descarga [" + downloadId + "] cancelada por el usuario.");
                        break;
                    }

                    outputStream.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;

                    if (onProgressUpdate != null) {
                        onProgressUpdate.accept(totalBytesRead / (1024.0 * 1024.0));
                    }
                }
            } finally {
                conn.disconnect();
                activeDownloads.remove(downloadId); // Limpiamos el ticket al terminar
            }
        } catch (Exception e) {
            activeDownloads.remove(downloadId);
            System.err.println("Error en descarga [" + downloadId + "]: " + e.getMessage());
        }
    }

    /**
     * LÓGICA DE NEGOCIO: Obtiene la URL de Scryfall y lanza la descarga universal.
     */
    public void downloadScryfallDefaultCards(String localPath, Consumer<Double> progress) throws Exception {
        String realUrl = fetchScryfallDownloadUrl();
        // Llamamos al método universal con el ID "scryfall"
        downloadFile("scryfall", realUrl, localPath, progress);
    }

    /**
     * LÓGICA DE NEGOCIO: Para Cardmarket (Ejemplo de cómo reutilizar el código).
     */
    public void downloadCardmarketPrices(String localPath, Consumer<Double> progress) {
        String url = "https://api.cardmarket.com/v2/prices.json"; // URL ficticia
        downloadFile("cardmarket", url, localPath, progress);
    }

    /**
     * MÉTODO DE CONTROL: Permite cancelar desde el Controlador usando el ID.
     */
    public void stopDownload(String downloadId) {
        AtomicBoolean cancel = activeDownloads.get(downloadId);
        if (cancel != null) {
            cancel.set(true);
        }
    }

    private String fetchScryfallDownloadUrl() throws IOException {
        URL url = new URL(SCRYFALL_METADATA_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Accept", "application/json");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            JsonNode rootNode = objectMapper.readTree(reader);
            return rootNode.get("download_uri").asText();
        } finally {
            conn.disconnect();
        }
    }
}