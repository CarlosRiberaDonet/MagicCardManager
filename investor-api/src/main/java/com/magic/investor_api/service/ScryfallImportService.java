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

    public void importCardsFromJson(String filePath) throws IOException {
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
    }

    private Card mapNodeToCard(JsonNode node) {
        Card card = new Card();
        card.setId(node.path("id").asText());
        card.setOracleId(node.path("oracle_id").asText());
        card.setCardmarketId(node.path("cardmarket_id").asLong(0L));
        card.setTcgplayerId(node.path("oracle_id").asText());
        card.setName(node.path("name").asText());
        card.setLang(node.path("lang").asText());
        String imageUrl = extractImageUrl(node); // Extraigo la URL de la imagen
        card.setImageUrl(buildCardmarketUrl(node));// Creo la URL de cardmarket
        card.setImageUrl(imageUrl);
        card.setRarity(node.path("rarity").asText());
        // 1. Extraemos el texto del nodo
        String releasedAtStr = node.path("released_at").asText();

        // 2. Validamos y parseamos
        if (releasedAtStr != null && !releasedAtStr.isEmpty()) {
            try {
                // LocalDate.parse() entiende "2024-03-23" de forma nativa
                card.setReleasedAt(LocalDate.parse(releasedAtStr));
            } catch (DateTimeParseException e) {
                // Si por algún motivo la fecha viene mal, evitamos que pete el import
                card.setReleasedAt(null);
                System.err.println("Fecha inválida para la carta: " + releasedAtStr);
            }
        }
        card.setSetCode(node.path("set").asText());
        card.setSetName(node.path("set_name").asText());
        card.setCollectorNumber(node.path("collector_number").asText());
        card.setTypeLine(node.path("type_line").asText());
        card.setManaCost(node.path("mana_cost").asText());
        // node.path("cmc") devuelve un MissingNode si no existe, evitando NullPointerException
        JsonNode cmcNode = node.path("cmc");

        if (!cmcNode.isMissingNode() && !cmcNode.isNull()) {
            // decimalValue() convierte el número del JSON directamente a BigDecimal
            card.setCmc(cmcNode.decimalValue());
        } else {
            // Es buena práctica inicializar a ZERO si no existe, o dejarlo null si tu BD lo permite
            card.setCmc(BigDecimal.ZERO);
        }

        card.setBorderColor(node.path("border_color").asText());
        card.setFoil(node.path("is_foil").asBoolean());
        card.setReprint(node.path("is_reprint").asBoolean());

        // Más campos aquí...
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
        return imageUris.path("normal").asText(null);
    }

    // Método para construir URL de cardmarket
    public String buildCardmarketUrl(JsonNode node) {
        // 1. Extraemos los datos necesarios del JSON
        String url = node.path("purchase_uris").path("cardmarket").asText(null);
        if (url == null) return null;

        String name = node.path("name").asText("");
        String lang = node.path("lang").asText("");
        String edition = node.path("set_name").asText("");

        // 2. Aplicamos tu lógica de reconstrucción para búsquedas genéricas
        if (url.contains("Search?searchString=")) {
            String formattedEdition = edition.replace(" ", "-");
            String formattedName = name.replace(" ", "-");
            url = url.replace("Search?searchString=", "/Singles/" + formattedEdition + "/" + formattedName);
        }

        // 3. Aplicamos tu filtro de lenguaje (Sustituyendo el tracking de Scryfall)
        String languageParam = LANGUAGE_MAP.get(lang);
        if (languageParam != null) {
            // Tu técnica para limpiar la URL de parámetros de marketing y poner el idioma
            url = url.replace("utm_campaign=card_prices&utm_medium=text&utm_source=scryfall", languageParam);
        }

        return url;
    }

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
}
