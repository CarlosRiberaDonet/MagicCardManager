package com.magic.investor_api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.investor_api.API.CardTraderAPI;
import com.magic.investor_api.dao.CardDAO;
import com.magic.investor_api.dao.CardVariantDAO;
import com.magic.investor_api.dao.ExpansionDAO;
import com.magic.investor_api.dto.CardVariantDTO;
import com.magic.investor_api.model.Card;
import com.magic.investor_api.model.CardVariant;
import org.springframework.stereotype.Service;

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

    public List<Long> getCardVariantList() {

        Map<Long, Long> cardmarketMap = cardDAO.getCardmarketId();
        Map<String, Long> scryfallMap = cardDAO.getScryfallId();

        ObjectMapper objectMapper = new ObjectMapper();

        List<Long> expansionList = expansionDAO.getExpansionList();

        // ORDEN CRÍTICO (para checkpoint fiable)
        Collections.sort(expansionList);

        Long lastProcessed = expansionDAO.getLastExpansionId();

        for (Long id : expansionList) {

            if (id <= lastProcessed) {
                continue;
            }

            try {

                String jsonCards = cardTraderAPI.fetchBlueprints(id);

                List<CardVariantDTO> cardVariantList = objectMapper.readValue(
                        jsonCards,
                        new TypeReference<List<CardVariantDTO>>() {}
                );

                List<CardVariant> cardVariants = new ArrayList<>();

                for (CardVariantDTO dto : cardVariantList) {

                    Long cardId = null;
                    Long cardmarketId = null;
                    String scryfallId = null;

                    if (dto.getCardMarketIds() != null && !dto.getCardMarketIds().isEmpty()) {
                        cardmarketId = dto.getCardMarketIds().get(0);
                    }

                    if (dto.getScryfallId() != null && !dto.getScryfallId().trim().isEmpty()) {
                        scryfallId = dto.getScryfallId();
                    }

                    if (cardmarketId != null) {
                        cardId = cardmarketMap.get(cardmarketId);
                    }

                    if (cardId == null && scryfallId != null) {
                        cardId = scryfallMap.get(scryfallId);
                    }

                    if (cardId == null) {
                        continue;
                    }

                    CardVariant cardVariant = new CardVariant();

                    Card cardRef = new Card();
                    cardRef.setId(cardId);

                    cardVariant.setCard(cardRef);
                    cardVariant.setCardtraderId(dto.getCardTraderId());
                    cardVariant.setCardmarketId(cardmarketId);
                    cardVariant.setScryfallId(scryfallId);
                    cardVariant.setExpansionId(dto.getExpansionId());
                    cardVariant.setVersion(dto.getVersion());
                    cardVariant.setCollectorNumber(dto.getFixedProperties().getCollectorNumber());

                    cardVariants.add(cardVariant);
                }

                if (!cardVariants.isEmpty()) {
                    cardVariantDAO.insertCardVariant(cardVariants);
                }

                // CHECKPOINT SOLO SI TODO OK
                expansionDAO.updateLastExpansionId(id);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    public void testSingleExpansion() {

        Long expansionId = 69L;

        Map<Long, Long> cardmarketMap = cardDAO.getCardmarketId();
        Map<String, Long> scryfallMap = cardDAO.getScryfallId();

        ObjectMapper objectMapper = new ObjectMapper();

        String jsonCards = cardTraderAPI.fetchBlueprints(expansionId);

        try {

            JsonNode root = objectMapper.readTree(jsonCards);

            // CardTrader puede devolver array directo o wrapper
            JsonNode arrayNode = root.isArray() ? root : root.get("data");

            List<CardVariantDTO> cardVariantList = objectMapper.readValue(
                    arrayNode.toString(),
                    new TypeReference<List<CardVariantDTO>>() {}
            );

            List<CardVariant> cardVariants = new ArrayList<>();

            for (CardVariantDTO dto : cardVariantList) {

                Long cardId = null;

                Long cardmarketId = null;
                String scryfallId = null;

                if (dto.getCardMarketIds() != null && !dto.getCardMarketIds().isEmpty()) {
                    cardmarketId = dto.getCardMarketIds().get(0);
                }

                if (dto.getScryfallId() != null && !dto.getScryfallId().trim().isEmpty()) {
                    scryfallId = dto.getScryfallId();
                }

                if (cardmarketId != null) {
                    cardId = cardmarketMap.get(cardmarketId);
                }

                if (cardId == null && scryfallId != null) {
                    cardId = scryfallMap.get(scryfallId);
                }

                if (cardId == null) {
                    continue;
                }

                CardVariant cardVariant = new CardVariant();

                Card cardRef = new Card();
                cardRef.setId(cardId);

                cardVariant.setCard(cardRef);
                cardVariant.setCardtraderId(dto.getCardTraderId());
                cardVariant.setCardmarketId(cardmarketId);
                cardVariant.setScryfallId(scryfallId);
                cardVariant.setExpansionId(dto.getExpansionId());
                cardVariant.setVersion(dto.getVersion());

                cardVariants.add(cardVariant);
            }

            if (!cardVariants.isEmpty()) {
                cardVariantDAO.insertCardVariant(cardVariants);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Permite visualizar la estructura de la respuesta de la API para las cartas de una expansión
    public String testSingleExpansionRaw() {

        Long expansionId = 1L;

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