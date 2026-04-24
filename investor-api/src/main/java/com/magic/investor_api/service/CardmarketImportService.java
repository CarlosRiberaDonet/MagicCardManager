package com.magic.investor_api.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.investor_api.dao.CardDAO;
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
    private CardDAO cardDAO;
    @Autowired
    private CardPriceDAO cardPriceDAO;
    private ObjectMapper objectMapper = new ObjectMapper();

    public void importToDatabase(String filePath) throws IOException {

        // Limpiar precios anteriores antes de insertar los nuevos
        cardPriceDAO.truncateCardPrice();

        // Llenar Mapa <CardmarketID, ScryfallUUID>
        Map<Long, Long> cardMap = cardDAO.getAllCardsIds();

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
                String nombreCampo = parser.getCurrentName();

                // Avanzo al valor del campo
                parser.nextToken();

                if ("priceGuides".equals(nombreCampo)) {

                    // Convierto todo el array en memoria
                    JsonNode priceGuidesArray = objectMapper.readTree(parser);

                    // Recorro cada elemento dentro del array
                    for (JsonNode guide : priceGuidesArray) {

                        // Extraigo el identificador que conecta con la BD
                        Long idProduct = guide.path("idProduct").asLong();

                        // Busca el cardmarketId en el diccionario
                        Long cardId = cardMap.get(idProduct);

                        // Si no existe en BD
                        if(cardId == null) {
                            System.out.println("No se encontró cardmarketId: " + idProduct);
                            continue;
                        }

                        List<CardPrice> newPrices = mapNodeToCardPrice(guide, cardId);
                        batch.addAll(newPrices);
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
                    // Guardamos los últimos elementos que queden en batch
                    if (!batch.isEmpty()) {
                        cardPriceRepository.saveAll(batch);
                        System.out.println("📦 Cartas en BD (resto final)");
                    }
                }
            }
        }
        if (!batch.isEmpty()) {
            cardPriceRepository.saveAll(batch);
        }
    }

    private List<CardPrice> mapNodeToCardPrice(JsonNode node, Long cardVariantId) {
        List<CardPrice> prices = new ArrayList<>();

        CardtraderCard cardProxy = new CardtraderCard();
        cardProxy.setId(cardVariantId);

        // Precio normal
        CardPrice normal = new CardPrice();
        normal.setCardVariant(cardProxy);
        normal.setSource(CardPrice.Source.CARDMARKET);
        normal.setFoil(false);
        normal.setAvg(node.path("avg").decimalValue());
        normal.setLow(node.path("low").decimalValue());
        normal.setTrend(node.path("trend").decimalValue());
        normal.setAvg1(node.path("avg1").decimalValue());
        normal.setAvg7(node.path("avg7").decimalValue());
        normal.setAvg30(node.path("avg30").decimalValue());
        normal.setUpdatedAt(LocalDateTime.now());
        prices.add(normal);

        // Precio foil (solo si existe)
        if (!node.path("avg-foil").isNull()) {
            CardPrice foil = new CardPrice();
            foil.setCardVariant(cardProxy);
            foil.setSource(CardPrice.Source.CARDMARKET);
            foil.setFoil(true);
            foil.setAvg(node.path("avg-foil").decimalValue());
            foil.setLow(node.path("low-foil").decimalValue());
            foil.setTrend(node.path("trend-foil").decimalValue());
            foil.setAvg1(node.path("avg1-foil").decimalValue());
            foil.setAvg7(node.path("avg7-foil").decimalValue());
            foil.setAvg30(node.path("avg30-foil").decimalValue());
            foil.setUpdatedAt(LocalDateTime.now());
            prices.add(foil);
        }

        return prices;
    }
}