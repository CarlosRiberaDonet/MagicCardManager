package com.magic.investor_api.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.investor_api.scryfall.dao.ScryfallCardDAO;
import com.magic.investor_api.dao.CardPriceDAO;
import com.magic.investor_api.mapper.CardPriceMapper;
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
public class CardmarketService {

    @Autowired
    private final CardPriceRepository cardPriceRepository;
    @Autowired
    private ScryfallCardDAO cardDAO;
    @Autowired
    private CardPriceDAO cardPriceDAO;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final String basePath = System.getProperty("user.dir");

    // Importar precios de JSON carmarket a card_price
    public void importGuidePricesToBD() throws IOException {

        CardPriceMapper cardPriceMapper = new CardPriceMapper();
        // Limpiar precios anteriores antes de insertar los nuevos
        cardPriceDAO.truncateCardPrice();

        String GUIDE_PRICES_JSON_PATH = basePath + "/src/main/resources/guide-prices.json";
        List<CardPrice> batch = new ArrayList<>();

        // Factoría de Jackson para parseo en streaming (no carga todo el JSON en memoria)
        JsonFactory factory = new JsonFactory();

        // Abro el parser sobre el fichero
        try (JsonParser parser = factory.createParser(new File(GUIDE_PRICES_JSON_PATH))) {

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

                    List<CardPrice> newPrices = cardPriceMapper.mapNodeToCardPrice(guide);
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
                }
            }
        }
        if (!batch.isEmpty()) {
            cardPriceRepository.saveAll(batch);
        }
    }
}