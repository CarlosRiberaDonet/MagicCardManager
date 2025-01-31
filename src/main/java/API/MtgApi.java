/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package API;

import GUI.DownloadProgressDialog;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 *
 * @author sovi8
 */
  public class MtgApi {
    private static final String MTG_JSON_URL = "https://api.scryfall.com/bulk-data/all-cards";

    public void downloadJson(String filePath, AtomicBoolean cancelRequested, DownloadProgressDialog progressDialog) throws Exception {
        // Paso 1: Obtener la URL de descarga real del archivo JSON
        URL url = new URL(MTG_JSON_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        int responseCode = conn.getResponseCode();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Fallo en la conexión: HTTP error code: " + responseCode);
        }

        // Leer la respuesta y extraer la URL de descarga
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        conn.disconnect();

        JSONObject jsonResponse = new JSONObject(response.toString());
        String downloadUrl = jsonResponse.getString("download_uri");

        // Paso 2: Descargar el archivo JSON real desde la URL de descarga obtenida
        URL downloadURL = new URL(downloadUrl);
        conn = (HttpURLConnection) downloadURL.openConnection();
        conn.setRequestMethod("GET");

        InputStream inputStream = conn.getInputStream();
        FileOutputStream outputStream = new FileOutputStream(filePath);

        byte[] buffer = new byte[4096];
        int bytesRead;
        long totalBytesRead = 0;

        // Mientras se lee el archivo
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            if (cancelRequested.get()) {
                outputStream.close();
                inputStream.close();
                conn.disconnect();
                System.out.println("Descarga cancelada.");
                return; // Termina la descarga si se solicita cancelación
            }

            totalBytesRead += bytesRead;
            outputStream.write(buffer, 0, bytesRead);

            // Actualizar el progreso en el diálogo
            double megabytesDescargados = totalBytesRead / (1024.0 * 1024.0);
            progressDialog.actualizarProgreso(megabytesDescargados);
        }

        outputStream.close();
        inputStream.close();
        conn.disconnect();

        // Notificar la descarga completa
        progressDialog.notificarDescargaCompleta(totalBytesRead / (1024.0 * 1024.0));
        System.out.println("Descarga completa. Archivo guardado en: " + filePath);
    }
}