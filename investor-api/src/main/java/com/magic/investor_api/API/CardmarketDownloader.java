package com.magic.investor_api.API;

import org.springframework.stereotype.Component;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Component
public class CardmarketDownloader {

    private static final String URL_S3 = "https://downloads.s3.cardmarket.com/productCatalog/priceGuide/price_guide_1.json";
    private static final String PATH_LOCAL = "C:/Proyectos/MagicCardManager/guide-prices.json";

    public void downloadGuidePrice() {
        try {
            System.out.println("Iniciando descarga desde Cardmarket S3...");

            URL url = new URL(URL_S3);
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
