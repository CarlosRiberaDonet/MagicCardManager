package com.magic.investor_api.cardmarketPrice.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.investor_api.scryfall.dao.ScryfallCardDAO;
import com.magic.investor_api.cardmarketPrice.dao.CardmarketPriceDAO;
import com.magic.investor_api.cardmarketPrice.model.CardmarketPrice;
import com.magic.investor_api.cardmarketPrice.repository.CardmarketPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardmarketPriceService {

    @Autowired
    private final CardmarketPriceRepository cardmarketPriceRepository;
    @Autowired
    private ScryfallCardDAO cardDAO;
    @Autowired
    private CardmarketPriceDAO cardmarketPriceDAO;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final String basePath = System.getProperty("user.dir");

    // Importar precios de JSON carmarket a card_price
    public void importGuidePricesToBD() throws IOException {

        // Limpiar precios anteriores antes de insertar los nuevos
        cardmarketPriceDAO.truncateCardPrice();

        String GUIDE_PRICES_JSON_PATH = basePath + "/src/main/resources/guide-prices.json";
        List<CardmarketPrice> batch = new ArrayList<>();

        // Factoría de Jackson para parseo en streaming (no carga todo el JSON en memoria)
        JsonFactory factory = new JsonFactory();

        // Abro el parser sobre el fichero
        try (JsonParser parser = factory.createParser(new File(GUIDE_PRICES_JSON_PATH))) {



            // Itero sobre cada campo del objeto raíz
            while (parser.nextToken() != JsonToken.END_OBJECT) {

                String fieldName = parser.getCurrentName();
                parser.nextToken();

                if(!"priceGuides".equals(fieldName)){
                    parser.skipChildren();
                    continue;
                }

                // Convierto todo el array en memoria
                JsonNode priceGuidesArray = objectMapper.readTree(parser);

                // Recorro cada elemento dentro del array
                for (JsonNode guide : priceGuidesArray) {

                    CardmarketPrice price = mapNodeToCardmarketPrice(guide);
                    batch.add(price);

                    // Guardo cada 1000 elementos
                    if (batch.size() >= 1000) {
                        cardmarketPriceRepository.saveAll(batch);
                        batch.clear();
                        System.out.println(" Cartas volcadas a la BD:");
                    }
                }
                // Guardamos los últimos elementos que queden en batch
                if (!batch.isEmpty()) {
                    cardmarketPriceRepository.saveAll(batch);
                }
            }
        }
        if (!batch.isEmpty()) {
            cardmarketPriceRepository.saveAll(batch);
        }
    }

    public CardmarketPrice mapNodeToCardmarketPrice(JsonNode node){
        CardmarketPrice price = new CardmarketPrice();

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

        return price;
    }

    // Comprobador de campos decimales
    private BigDecimal getDecimal(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return (value == null || value.isNull())
                ? null
                : value.decimalValue();
    }

    // Obtengo precios de cardmarket_price
    public CardmarketPrice getCardmarketPrice(Long cardmarketId){
        return cardmarketPriceDAO.checkCardPrice(cardmarketId);
    }
}