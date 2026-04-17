package com.magic.investor_api.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.investor_api.dao.CardDAO;
import com.magic.investor_api.model.Card;
import com.magic.investor_api.model.CardPrice;
import com.magic.investor_api.model.CardVariant;
import com.magic.investor_api.repository.CardPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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
    private ObjectMapper objectMapper = new ObjectMapper();

    public void importToDatabase(String filePath) throws IOException {

        // Llenar Mapa <CardmarketID, ScryfallUUID>
        Map<Long, Long> cardMap = cardDAO.getAllCardsIds();

        List<CardPrice> batch = new ArrayList<>();


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

                if ("priceGuides".equals(nombreCampo)) {

                    // Convierto todo el array en memoria
                    JsonNode priceGuidesArray = objectMapper.readTree(parser);

                    // Recorro cada elemento dentro del array
                    for (JsonNode guide : priceGuidesArray) {

                        // Filtro los idCategory = 1 (cartas sueltas)
                        if (guide.path("idCategory").asInt() == 1) {

                            // Extraigo el identificador que conecta con la BD
                            Long idProduct = guide.path("idProduct").asLong();

                            // Busca el cardmarketId en el diccionario
                            Long cardId = cardMap.get(idProduct);

                            // Si no existe en BD
                            if(cardId == null) {
                                System.out.println("No se encontró cardmarketId: " + idProduct);
                                continue;
                            }

                            CardPrice newCardPrice = mapNodeToCardPrice(guide, cardId);
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

    private CardPrice mapNodeToCardPrice(JsonNode node, Long cardId) {
        // 1. Creo la entidad de precio
        CardPrice cardPrice = new CardPrice();

        // 2. Creo un objeto Card "fantasma" (Proxy)
        // Solo le seteamos el ID para que Hibernate sepa a qué carta enlazar el precio
        CardVariant cardProxy = new CardVariant();
        cardProxy.setId(cardId);

        // 3. Setteo la relación y los datos
        cardPrice.setCardVariant(cardProxy);
        //cardPrice.setCardmarketId(node.path("idProduct").asLong());

        // Usamos decimalValue() para asegurar precisión financiera
        cardPrice.setAvg(node.path("avg").decimalValue());
        cardPrice.setLow(node.path("low").decimalValue());

        return cardPrice;
    }
}