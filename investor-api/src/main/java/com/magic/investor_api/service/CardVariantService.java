package com.magic.investor_api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.investor_api.API.CardTraderAPI;
import com.magic.investor_api.dao.CardDAO;
import com.magic.investor_api.dao.CardVariantDAO;
import com.magic.investor_api.dao.ExpansionDAO;
import com.magic.investor_api.dto.CardVariantDTO;
import com.magic.investor_api.model.ScryfallCard;
import com.magic.investor_api.model.CardtraderCard;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class CardVariantService {

    private final ExpansionDAO expansionDAO;
    private final CardTraderAPI cardTraderAPI;
    private final CardVariantDAO cardVariantDAO;
    private final CardDAO cardDAO;

    public CardVariantService(ExpansionDAO expansionDAO, CardTraderAPI cardTraderAPI, CardVariantDAO cardVariantDAO, CardDAO cardDAO) {
        this.expansionDAO = expansionDAO;
        this.cardTraderAPI = cardTraderAPI;
        this.cardVariantDAO = cardVariantDAO;
        this.cardDAO = cardDAO;
    }

    // Depuración para optener la última expansión procesada.
    public int getLastExpansionProcessed(){

        // Obtengo los id de la lista de expansiones
        List<Long> expansionList = expansionDAO.getExpansionList();

        return expansionList.size();

    }

    // Obtener cartas por expansión
    public List<Long> getCardVariantList() {

        // Borro los datos de la tabla card_variant
        //cardVariantDAO.truncateCardVariant();

        int count = 0;
        Map<Long, Long> cardmarketMap = cardDAO.getCardmarketId();
        Map<String, Long> scryfallMap = cardDAO.getScryfallId();

        ObjectMapper objectMapper = new ObjectMapper();

        // Obtengo los id de la lista de expansiones
        List<Long> expansionList = expansionDAO.getExpansionList();

        // ORDEN CRÍTICO (para checkpoint fiable)
        Collections.sort(expansionList);
        // Obtengo la última expansión procesada
        Long lastProcessed = expansionDAO.getLastExpansionId();
        System.out.println("Última expansión procesada: " + lastProcessed);

        for (Long id : expansionList) {

            if (id <= lastProcessed) continue;

            try {
                String jsonCards = cardTraderAPI.fetchBlueprints(id);

                List<CardVariantDTO> cardVariantList = objectMapper.readValue(
                        jsonCards,
                        new TypeReference<List<CardVariantDTO>>() {}
                );

                List<CardtraderCard> cardVariantsList = new ArrayList<>();

                // Itero sobre la lista obtenida de cartas
                for (CardVariantDTO dto : cardVariantList) {

                    Long cardId = null;
                    Long cardmarketId = null;
                    String scryfallId = null;

                    // Si el campo cardmaketId no es nulo ni está vacío
                    if (dto.getCardMarketIds() != null && !dto.getCardMarketIds().isEmpty()) {
                        cardmarketId = dto.getCardMarketIds().get(0); // Le asigno el cardmarketId obtenido
                    }

                    // Si el campo scryfallId no es nulo ni está vacío
                    if (dto.getScryfallId() != null && !dto.getScryfallId().trim().isEmpty()) {
                        scryfallId = dto.getScryfallId(); // Le asigno el scryfallId obtenido
                    }

                    // Si scryfallId no es nulo busco en el mapa si existe
                    if (scryfallId != null) {
                        cardId = scryfallMap.get(scryfallId);
                    }
                    // Si cardmarketId no es nulo busco en el mapa si existe
                    if (cardId == null && cardmarketId != null) {
                        cardId = cardmarketMap.get(cardmarketId);
                    }

                    CardtraderCard cardVariant = new CardtraderCard();

                    ScryfallCard cardRef = new ScryfallCard();
                    cardRef.setId(cardId);

                    cardVariant.setCard(cardRef);
                    cardVariant.setCardtraderId(dto.getCardTraderId());
                    cardVariant.setCardmarketId(cardmarketId);
                    cardVariant.setScryfallId(scryfallId);
                    cardVariant.setExpansionId(dto.getExpansionId());
                    cardVariant.setVersion(dto.getVersion());
                    cardVariant.setCollectorNumber(dto.getFixedProperties().getCollectorNumber());

                    cardVariantsList.add(cardVariant);
                }

                // Inserto la lista en la tabla card_variant
                if (!cardVariantsList.isEmpty()) {
                    cardVariantDAO.insertCardVariant(cardVariantsList);
                }

                // CHECKPOINT SOLO SI TODO OK
                expansionDAO.updateLastExpansionId(id);
                count++;
                System.out.println("Expansiones descargadas: " + count);

            } catch (HttpClientErrorException.NotFound e) {
                System.out.println("Expansión " + id + " no disponible, saltando...");
                expansionDAO.updateLastExpansionId(id);

            } catch (Exception e) {
                System.out.println("Error en expansión " + id + ": " + e.getMessage());
                throw new RuntimeException(e);
            }
            System.out.println("Procesando expansión: " + id);
        }

        return null;
    }

    // Permite visualizar la estructura de la respuesta de la API para las cartas de una expansión
    public String testSingleExpansionRaw() {

        Long expansionId = 3261L;

        String jsonCards = cardTraderAPI.fetchBlueprints(expansionId);

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode root = objectMapper.readTree(jsonCards);

            // Devuelve JSON formateado para Postman
            return objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(root);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}