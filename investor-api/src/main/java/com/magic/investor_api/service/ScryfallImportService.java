package com.magic.investor_api.service;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.investor_api.model.Card;
import com.magic.investor_api.repository.CardRepository;
import com.magic.investor_api.repository.CardPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScryfallImportService {

    private final CardRepository cardRepository;
    private final CardPriceRepository cardPriceRepository;
    private ObjectMapper objectMapper = new ObjectMapper(); // Spring ya lo tiene configurado

    private static final Map<String, String> LANGUAGE_MAP = Map.ofEntries(
            Map.entry("en", "language=1"),
            Map.entry("fr", "language=2"),
            Map.entry("de", "language=3"),
            Map.entry("es", "language=4"),
            Map.entry("it", "language=5"),
            Map.entry("zhs", "language=6"),
            Map.entry("jp", "language=7"),
            Map.entry("pt", "language=8"),
            Map.entry("ru", "language=9"),
            Map.entry("ko", "language=10"),
            Map.entry("zht", "language=11")
    );

    public void importToDatabase(String filePath) throws IOException {
        JsonFactory factory = new JsonFactory();
        try (JsonParser parser = factory.createParser(new File(filePath))) {

            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new IOException("Formato JSON inválido");
            }

            List<Card> batch = new ArrayList<>();
            int totalProcessed = 0;

            while (parser.nextToken() == JsonToken.START_OBJECT) {
                JsonNode node = objectMapper.readTree(parser);

                // Usamos tu lógica de mapeo que ya está limpia
                Card card = mapNodeToCard(node);
                batch.add(card);
                totalProcessed++;

                // Cada 1000 cartas, volcamos y vaciamos la memoria RAM
                if (totalProcessed % 1000 == 0) {
                    cardRepository.saveAll(batch);
                    batch.clear();
                    System.out.println("📦 Cartas en BD: " + totalProcessed);
                }
            }

            // No olvides las últimas que queden en el tintero
            if (!batch.isEmpty()) {
                cardRepository.saveAll(batch);
            }
        }
    }

    private Card mapNodeToCard(JsonNode node) {
        Card card = new Card();

        // Campos de identidad
        card.setId(node.path("id").asText(""));
        card.setOracleId(node.path("oracle_id").asText(""));
        card.setCardmarketId(node.path("cardmarket_id").asLong(0L));
        card.setTcgplayerId(node.path("tcgplayer_id").asText("")); // Corregido el nombre del campo
        card.setName(node.path("name").asText("Unknown"));
        card.setLang(node.path("lang").asText("en"));

        // Métodos externos (Asegúrate de que usen .path interiormente)
        card.setImageUrl(extractImageUrl(node));
        card.setCardmarketURL(buildCardmarketUrl(node));

        card.setRarity(node.path("rarity").asText("common"));

        // Fecha con validación robusta
        String releasedAtStr = node.path("released_at").asText("");
        if (!releasedAtStr.isEmpty()) {
            try {
                card.setReleasedAt(LocalDate.parse(releasedAtStr));
            } catch (DateTimeParseException e) {
                card.setReleasedAt(null);
            }
        }

        // Campos de edición
        card.setSetCode(node.path("set").asText(""));
        card.setSetName(node.path("set_name").asText(""));
        card.setCollectorNumber(node.path("collector_number").asText(""));
        card.setTypeLine(node.path("type_line").asText(""));
        card.setBorderColor(node.path("border_color").asText(""));

        // CORRECCIÓN CRÍTICA: Scryfall usa "foil" y "reprint", no "is_foil"
        card.setFoil(node.path("foil").asBoolean(false));
        card.setReprint(node.path("reprint").asBoolean(false));

        return card;
    }

    // Método para extraer las imagen de la URL
    public String extractImageUrl(JsonNode node) {
        // Buscar la URL de la imagen en la raíz del JSON
        JsonNode imageUris = node.path("image_uris");

        // Si no está en la raíz, buscar en el array de card_faces del JSON
        if (imageUris.isMissingNode() && node.has("card_faces")) {
            // Accedemos de forma segura a la primera cara y sus uris
            imageUris = node.path("card_faces").path(0).path("image_uris");
        }

        // Devolvemos la versión 'normal', o null si el archivo está corrupto/vacío
        // return imageUris.path("normal").asText(null);
        return imageUris.path("normal").isMissingNode() ? null : imageUris.path("normal").asText();
    }

    public String buildCardmarketUrl(JsonNode node) {
        // 1. Acceso ultra-seguro al nodo de la URL
        JsonNode cmNode = node.path("purchase_uris").path("cardmarket");

        // Si no existe, devolvemos null real para la BD (no el String "null")
        if (cmNode.isMissingNode() || cmNode.isNull()) {
            return null;
        }

        String url = cmNode.asText();

        // 2. Extraemos metadatos con fallback para evitar errores en los replace
        String name = node.path("name").asText("");
        String lang = node.path("lang").asText("");
        String edition = node.path("set_name").asText("");

        // 3. Reconstrucción de la URL de búsqueda a URL de Single
        // Usamos .toLowerCase() porque Cardmarket es sensible a esto en sus slugs
        if (url.contains("Search?searchString=")) {
            // Limpiamos caracteres que rompen URLs (comas, puntos, apóstrofes)
            String formattedEdition = edition.replace(" ", "-").replaceAll("[^a-zA-Z0-9-]", "");
            String formattedName = name.replace(" ", "-").replaceAll("[^a-zA-Z0-9-]", "");

            url = url.replace("Search?searchString=", "/Singles/" + formattedEdition + "/" + formattedName);
        }

        // 4. Filtro de lenguaje y limpieza de tracking
        // Validamos que LANGUAGE_MAP esté inicializado para que no pete aquí
        if (LANGUAGE_MAP != null && !lang.isEmpty()) {
            String languageParam = LANGUAGE_MAP.get(lang);
            if (languageParam != null) {
                // Reemplazamos el tracking de Scryfall por tu parámetro de idioma
                url = url.replace("utm_campaign=card_prices&utm_medium=text&utm_source=scryfall", languageParam);
            }
        }

        return url;
    }

    /*public void importCardsFromJson(String filePath) throws IOException {
        JsonFactory factory = new JsonFactory();
        try (JsonParser parser = factory.createParser(new File(filePath))) {

            // Verificamos que el JSON empiece con un array [
            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new IOException("El archivo no es un array JSON válido");
            }

            List<Card> cardBatch = new ArrayList<>();
            int count = 0;

            // Leemos el "río" de datos elemento por elemento
            while (parser.nextToken() == JsonToken.START_OBJECT) {
                // Leemos el nodo actual (una carta)
                JsonNode node = objectMapper.readTree(parser);

                // Mapeamos el JSON a nuestro objeto Card
                Card card = mapNodeToCard(node);
                cardBatch.add(card);

                count++;

                // Guardamos en bloques de 1000 para no saturar MySQL
                if (count % 1000 == 0) {
                    cardRepository.saveAll(cardBatch);
                    cardBatch.clear();
                    System.out.println("Procesadas: " + count + " cartas...");
                }
            }

            // Guardamos el resto
            if (!cardBatch.isEmpty()) {
                cardRepository.saveAll(cardBatch);
            }

            System.out.println("¡Importación finalizada! Total: " + count);
        }
    }*/
}
