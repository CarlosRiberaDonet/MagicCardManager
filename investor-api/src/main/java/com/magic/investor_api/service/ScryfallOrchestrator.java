package com.magic.investor_api.service;

import com.magic.investor_api.client.ScryfallApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScryfallOrchestrator {

    private final ScryfallApi scryfallApi;
    private final ScryfallImportService importService;

    public void startFullImport(String path) {
        String localPath = "D:/Proyectos/MagicCardManager/catalog_all.json";

        System.out.println("1. Iniciando descarga de 3GB...");

        // Ejecutamos la descarga
        // Nota: Si mantienes @Async, esto debería devolver un Future o usar un Callback
        try {
            scryfallApi.downloadScryfallDefaultCards(localPath, progress -> {
                if (progress % 10 == 0) { // Solo logueamos cada 10MB para no saturar
                    System.out.println("Descargado: " + progress + " MB");
                }
            });

            // Aquí hay un detalle: Si downloadFile es @Async,
            // el código seguirá a la línea 2 antes de que termine la descarga.
            // Para desarrollo, lo más sencillo es que la descarga sea SÍNCRONA.

            System.out.println("2. Descarga completada. Iniciando procesado (Masticando)...");
            importService.importToDatabase(localPath);

            System.out.println("3. ¡Proceso total finalizado con éxito!");

        } catch (Exception e) {
            System.err.println("Error en el proceso: " + e.getMessage());
        }
    }
}