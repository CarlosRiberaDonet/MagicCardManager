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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardmarketImportService {

    @Autowired
    private final CardPriceRepository cardPriceRepository;
    @Autowired
    private CardDAO cardDAO;
    private ObjectMapper objectMapper = new ObjectMapper();

    public void importToDatabase(String filePath) throws IOException {

        List<CardPrice> batch = new ArrayList<>();
        String fechaCreacion = "";

        // Factoría de Jackson para parseo en streaming (no carga todo el JSON en memoria)
        JsonFactory factory = new JsonFactory();

        // Abro el parser sobre el fichero (try-with-resources para cierre automático)
        try (JsonParser parser = factory.createParser(new File(filePath))) {

            // Primer token del JSON: debe ser un objeto raíz {...}
            if (parser.nextToken() != JsonToken.START_OBJECT) {
                throw new IOException("Formato JSON inválido, se esperaba un objeto al inicio");
            }

            // Itero sobre cada campo del objeto raíz
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String nombreCampo = parser.getCurrentName();

                // Avanzo al valor del campo
                parser.nextToken();

                // Nombre del campo actual (ej: "createdAt", "priceGuides")
                if ("createdAt".equals(nombreCampo)) {
                    fechaCreacion = parser.getText();

                } else if ("priceGuides".equals(nombreCampo)) {

                    // Convierto todo el array en memoria
                    JsonNode priceGuidesArray = objectMapper.readTree(parser);

                    // Recorro cada elemento dentro del array
                    for (JsonNode guide : priceGuidesArray) {

                        // Filtro los idCategory = 1
                        if (guide.path("idCategory").asInt() == 1) {

                            // Extraigo el identificador que conecta con la BD
                            Long idProduct = guide.path("idProduct").asLong();

                            // Buscas la carta en BD usando ese id
                            Card card = cardDAO.getCardByCardmarketId(idProduct);

                            // Si no existe en BD
                            if(card == null) {
                                System.out.println("No se encontró cardmarketId: " + idProduct);
                                continue;
                            }
                            CardPrice newCardPrice = mapNodeToCardPrice(guide, card);
                            batch.add(newCardPrice);
                        }
                        // Guardamos cada 1000 elementos
                        if (batch.size() >= 1000) {
                            cardPriceRepository.saveAll(batch);
                            batch.clear();
                            System.out.println(" Cartas volcadas a la BD:");
                        }
                        else {
                            // Si el campo no te interesa, lo ignoras completamente
                            parser.skipChildren();
                        }
                    }
                }
                if (!batch.isEmpty()) {
                    cardPriceRepository.saveAll(batch);
                }
            }

            // Guardamos los últimos elementos que queden en batch
            if (!batch.isEmpty()) {
                cardPriceRepository.saveAll(batch);
                System.out.println("📦 Cartas en BD (resto final)");
            }
        }
    }

    private CardPrice mapNodeToCardPrice(JsonNode node, Card card){

        // Extraigo el idProduct del json de cardmarket
        Long idProduct = node.path("idProduct").asLong();


        System.out.println("idProduct del node: " + idProduct);
        System.out.println("Carta obtenida: " + card);

        if(card == null){
            System.out.println("No se encontró cardmarketId");
        } else{
            CardPrice cardPrice = new CardPrice();

            cardPrice.setCard(card);
            cardPrice.setCardmarketId(card.getCardmarketId());
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
        return null;
    }
}