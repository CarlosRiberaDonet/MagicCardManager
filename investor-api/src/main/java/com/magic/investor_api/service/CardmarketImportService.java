package com.magic.investor_api.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.investor_api.dao.CardDAO;
import com.magic.investor_api.model.Card;
import com.magic.investor_api.model.CardPrice;
import com.magic.investor_api.repository.CardPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CardmarketImportService {

    private final CardPriceRepository cardPriceRepository;
    private JsonReaderService jsonReader;
    private CardDAO cardDAO;
    private ObjectMapper objectMapper = new ObjectMapper();
    private Map<String, Long> cardMapIds = new HashMap<>();

    public void importToDatabase(String filePath) throws IOException {

        JsonFactory factory = new JsonFactory();
        List<CardPrice> batch = new ArrayList<>();
        int totalProcessed = 0;

        try (JsonParser parser = factory.createParser(new File(filePath))) {

            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new IOException("Formato JSON inválido");
            }

            while (parser.nextToken() == JsonToken.START_OBJECT) {
                JsonNode node = objectMapper.readTree(parser);

                // Usamos tu lógica de mapeo que ya está limpia
                CardPrice newCardPrice = mapNodeToCardPrice(node);
                batch.add(newCardPrice);
                totalProcessed++;

                // Cada 1000 cartas, vuelco a la BD
                if (totalProcessed > 999) {
                    cardPriceRepository.saveAll(batch);
                    batch.clear();
                    System.out.println("📦 Cartas en BD: " + totalProcessed);
                }
            }

            if (!batch.isEmpty()) {
                cardPriceRepository.saveAll(batch);
            }
        }
    }

    private CardPrice mapNodeToCardPrice(JsonNode node){

        // Extraigo el productId del json de cardmarket
        Long productId = node.path("productId").asLong();
        String uuidScryfall = "";
        Long valor;

        // Busco el UUID de scryfall
        for(Map.Entry<String, Long> entry : cardMapIds.entrySet()){
            valor = entry.getValue();
            if(productId.equals(valor)){
                uuidScryfall = entry.getKey();
                break;
            }
            if(valor == 0){
                System.out.println("No se encontró cardmarketId para: " + entry.getKey());
            }
        }

        Card card = new Card();
        card.setId(uuidScryfall);
        card.setCardmarketId(productId);

        CardPrice cardPrice = new CardPrice();

        cardPrice.setCard(card);
        cardPrice.setAvg(node.path("avg").decimalValue());
        cardPrice.setLow(node.path("low").decimalValue());
        cardPrice.setTrend(node.path("trend").decimalValue());
        cardPrice.setAvg1(node.path("avg1").decimalValue());
        cardPrice.setAvg7(node.path("avg7").decimalValue());
        cardPrice.setAvg30(node.path("avg30").decimalValue());

        cardPrice.setAvgFoil(node.path("avg-foil").decimalValue());
        cardPrice.setLowFoil(node.path("low-foil").decimalValue());
        cardPrice.setTrendFoil(node.path("trend-foil").decimalValue());
        cardPrice.setAvg1Foil(node.path("avg1-foil").decimalValue());
        cardPrice.setAvg7Foil(node.path("avg7-foil").decimalValue());
        cardPrice.setAvg30Foil(node.path("avg30-foil").decimalValue());

        return cardPrice;
    }
}