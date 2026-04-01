package com.magic.investor_api.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.magic.investor_api.model.Card;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CardmarketService {

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
