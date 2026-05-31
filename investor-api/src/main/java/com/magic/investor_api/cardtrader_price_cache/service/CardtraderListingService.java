package com.magic.investor_api.cardtrader_price_cache.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.magic.investor_api.cardtrader.ports.CardTraderAPI;
import com.magic.investor_api.cardtrader.service.CardTraderService;
import com.magic.investor_api.cardtrader_price_cache.CardtraderListingRepository;
import com.magic.investor_api.cardtrader_price_cache.model.CardtraderListing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CardtraderListingService {

    private final CardtraderListingRepository repository;
    private final CardTraderService cardTraderService;
    private final CardTraderAPI cardTraderAPI;

    //  Obtener y mapear JsonNode del mercado de cartas cardtrader
    public String mapNodeToCardtraderListing(Long cardId, String scryfallId){

        Long cardtraderId = cardTraderService.getCardtraderId(scryfallId);

        JsonNode node = cardTraderAPI.fetchCardProducts(cardtraderId);
        readCardtraderJsonNode(cardId, node);

        return null;
    }

    // Leer JSON de cartas y mapearlas a objeto CardTraderListing
    public void readCardtraderJsonNode(Long cardId, JsonNode node) {

        List<CardtraderListing> batch = new ArrayList<>();

        try {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();

                JsonNode arrayNode = entry.getValue();

                if (!arrayNode.isArray()) {
                    continue;
                }

                for (JsonNode item : arrayNode) {
                    CardtraderListing listing = new CardtraderListing();
                    listing.setCardId(cardId);
                    listing.setCardtraderId(item.path("blueprint_id").asLong());
                    listing.setPrice(BigDecimal.valueOf(item.path("price_cents").asLong()).movePointLeft(2));
                    listing.setCondition(item.path("properties_hash").path("condition").asText());
                    listing.setLang(item.path("properties_hash").path("mtg_language").asText(null));
                    listing.setFoil(item.path("properties_hash").path("mtg_foil").asBoolean());
                    listing.setUrl("https://www.cardtrader.com/cards/" + (item.path("blueprint_id").asText()));
                    listing.setFetchedAt(LocalDateTime.now());

                    batch.add(listing);
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        repository.saveAll(batch);
    }
}
