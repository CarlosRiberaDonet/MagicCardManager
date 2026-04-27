package com.magic.investor_api.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.investor_api.dao.ScryfallCardDAO;
import com.magic.investor_api.dao.CardPriceDAO;
import com.magic.investor_api.model.CardPrice;
import com.magic.investor_api.model.CardtraderCard;
import com.magic.investor_api.repository.CardPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CardmarketImportService {

    @Autowired
    private final CardPriceRepository cardPriceRepository;
    @Autowired
    private ScryfallCardDAO cardDAO;
    @Autowired
    private CardPriceDAO cardPriceDAO;
    private ObjectMapper objectMapper = new ObjectMapper();

    public void importToDatabase(String filePath) throws IOException {

        // Limpiar precios anteriores antes de insertar los nuevos
        cardPriceDAO.truncateCardPrice();

        List<CardPrice> batch = new ArrayList<>();

        // Factoría de Jackson para parseo en streaming (no carga todo el JSON en memoria)
        JsonFactory factory = new JsonFactory();

        // Abro el parser sobre el fichero
        try (JsonParser parser = factory.createParser(new File(filePath))) {

            // Primer token del JSON: debe ser un objeto raíz {...}
            if (parser.nextToken() != JsonToken.START_OBJECT) {
                throw new IOException("Formato JSON inválido, se esperaba un objeto al inicio");
            }

            // Itero sobre cada campo del objeto raíz
            while (parser.nextToken() != JsonToken.END_OBJECT) {

                // Avanzo al valor del campo
                parser.nextToken();

                // Convierto todo el array en memoria
                JsonNode priceGuidesArray = objectMapper.readTree(parser);

                // Recorro cada elemento dentro del array
                for (JsonNode guide : priceGuidesArray) {

                    List<CardPrice> newPrices = mapNodeToCardPrice(guide);
                    batch.addAll(newPrices);

                    // Guardo cada 1000 elementos
                    if (batch.size() >= 1000) {
                        cardPriceRepository.saveAll(batch);
                        batch.clear();
                        System.out.println(" Cartas volcadas a la BD:");
                    }
                    /*else {
                        // Si el campo no te interesa, lo ignoras completamente
                        parser.skipChildren();
                    }*/
                }
                // Guardamos los últimos elementos que queden en batch
                if (!batch.isEmpty()) {
                    cardPriceRepository.saveAll(batch);
                    System.out.println("📦 Cartas en BD (resto final)");
                }
            }
        }
        if (!batch.isEmpty()) {
            cardPriceRepository.saveAll(batch);
        }
    }

    private List<CardPrice> mapNodeToCardPrice(JsonNode node) {

        List<CardPrice> prices = new ArrayList<>();

        CardPrice newPrice = new CardPrice();
        newPrice.setCardmarketId(node.path("idProduct").asLong());
        newPrice.setAvg(node.path("avg").decimalValue());
        newPrice.setLow(node.path("low").decimalValue());
        newPrice.setTrend(node.path("trend").decimalValue());
        newPrice.setAvg1(node.path("avg1").decimalValue());
        newPrice.setAvg7(node.path("avg7").decimalValue());
        newPrice.setAvg30(node.path("avg30").decimalValue());
        newPrice.setAvgFoil(node.path("avg_foil").decimalValue());
        newPrice.setLowFoil(node.path("low_foil").decimalValue());
        newPrice.setTrendFoil(node.path("trend_foil").decimalValue());
        newPrice.setAvg1Foil(node.path("avg1_foil").decimalValue());
        newPrice.setAvg7Foil(node.path("avg7_foil").decimalValue());
        newPrice.setAvg30Foil(node.path("avg30_foil").decimalValue());

        newPrice.setUpdatedAt(LocalDateTime.now());

        prices.add(newPrice);

        return prices;
    }
}