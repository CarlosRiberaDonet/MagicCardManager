package com.magic.investor_api.cardmarketPrice.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.investor_api.cardmarketPrice.dao.CardmarketPriceDAO;
import com.magic.investor_api.cardmarketPrice.model.CardmarketPrice;
import com.magic.investor_api.cardmarketPrice.repository.CardmarketPriceRepository;
import com.magic.investor_api.scryfall.dao.ScryfallCardDAO;
import com.magic.investor_api.scryfall.service.ScryfallService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardmarketPriceService {

    @Autowired
    private final CardmarketPriceRepository cardmarketPriceRepository;
    @Autowired
    private CardmarketPriceDAO cardmarketPriceDAO;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final String basePath = System.getProperty("user.dir");
    private final ScryfallCardDAO scryfallCardDAO;

    public void importGuidePricesToBD() throws IOException {

        // Limpiar tabla antes de cargar nuevos datos
        cardmarketPriceDAO.truncateCardPrice();

        Path jsonPath = Paths.get(basePath, "guide-prices.json");

        List<CardmarketPrice> batch = new ArrayList<>();

        JsonFactory factory = new JsonFactory();

        try (JsonParser parser = factory.createParser(jsonPath.toFile())) {

            if (parser.nextToken() != JsonToken.START_OBJECT) {
                throw new IOException("JSON inválido: se esperaba un objeto raíz");
            }

            while (parser.nextToken() != JsonToken.END_OBJECT) {

                String fieldName = parser.currentName();
                parser.nextToken();

                if (!"priceGuides".equals(fieldName)) {
                    parser.skipChildren();
                    continue;
                }

                if (parser.currentToken() != JsonToken.START_ARRAY) {
                    throw new IOException("priceGuides no es un array válido");
                }

                while (parser.nextToken() != JsonToken.END_ARRAY) {

                    JsonNode guide = objectMapper.readTree(parser);

                    CardmarketPrice price = mapNodeToCardmarketPrice(guide);
                    batch.add(price);

                    if (batch.size() >= 1000) {
                        cardmarketPriceRepository.saveAll(batch);
                        batch.clear();
                    }
                }
            }

            if (!batch.isEmpty()) {
                cardmarketPriceRepository.saveAll(batch);
            }

        } finally {
            try {
                Files.deleteIfExists(jsonPath);
            } catch (IOException e) {
                System.err.println("No se pudo eliminar guide-prices.json: " + e.getMessage());
            }
        }
    }

    public CardmarketPrice mapNodeToCardmarketPrice(JsonNode node){
        CardmarketPrice price = new CardmarketPrice();

        JsonNode idProductNode = node.get("idProduct");

        if (idProductNode == null) {
            return null;
        }
        price.setCardmarketId(node.get("idProduct").asLong());
        price.setAvg(getDecimal(node, "avg"));
        price.setLow(getDecimal(node,"low"));
        price.setTrend(getDecimal(node, "trend"));
        price.setAvg1(getDecimal(node, "avg1"));
        price.setAvg7(getDecimal(node,"avg7"));
        price.setAvg30(getDecimal(node, "avg30"));
        price.setAvgFoil(getDecimal(node, "avg-foil"));
        price.setLowFoil(getDecimal(node,"low-foil"));
        price.setTrendFoil(getDecimal(node,"trend-foil"));
        price.setAvg1Foil(getDecimal(node,"avg1-foil"));
        price.setAvg7Foil(getDecimal(node, "avg7-foil"));
        price.setAvg30Foil(getDecimal(node,"avg30-foil"));
        price.setUpdatedAt(LocalDateTime.now());
        return price;
    }

    // Comprobador de campos decimales
    private BigDecimal getDecimal(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return (value == null || value.isNull())
                ? null
                : value.decimalValue();
    }

    // Obtener cardmarketId
    public Long getCarmarketId(Long cardId){
        return scryfallCardDAO.selectCardmarketIdBycardId(cardId);
    }

    // Obtengo precios de cardmarket_price
    public CardmarketPrice getCardmarketPriceByCardmarketId(Long cardmarketId) {

        return cardmarketPriceDAO.selectCardmarketPrice(cardmarketId);
    }
}