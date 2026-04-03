package com.magic.investor_api.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.investor_api.model.CardPrice;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

@Service
public class JsonReaderService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void readPriceGuides(String filePath, Consumer<JsonNode> procesador) throws IOException {
        // Leemos todo el JSON
        JsonNode root = objectMapper.readTree(new File(filePath));

        // Accedemos al array priceGuides
        JsonNode priceGuides = root.path("priceGuides");
        if (!priceGuides.isArray()) {
            throw new IOException("El JSON no contiene un array priceGuides");
        }

        // Recorremos cada objeto dentro del array
        for (JsonNode node : priceGuides) {
            procesador.accept(node);
        }
    }
}
