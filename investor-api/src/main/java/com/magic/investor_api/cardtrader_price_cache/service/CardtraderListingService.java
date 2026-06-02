package com.magic.investor_api.cardtrader_price_cache.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.magic.investor_api.cardtrader.ports.CardTraderAPI;
import com.magic.investor_api.cardtrader.service.CardTraderService;
import com.magic.investor_api.cardtrader_price_cache.CardtraderPriceCacheDTO;
import com.magic.investor_api.cardtrader_price_cache.repository.CardtraderListingRepository;
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
    private final CardtraderPriceCacheService cardtraderPriceCacheService;

    //  Obtener y mapear JsonNode del mercado de cartas cardtrader
    public CardtraderPriceCacheDTO updateCardPrices(String scryfallId, String lang, String condition, boolean isFoil){

        // Obtengo el cardtrader_id que corresponde a ese scryfall_id
        Long cardtraderId = cardTraderService.getCardtraderId(scryfallId);
        if(cardtraderId == null || cardtraderId <= 0){
            System.out.println("No existe cardtraderId asociado a scryfallId: " + scryfallId);
            return null;
        }

        // Creo objeto DTO con los resultados de la consulta
        CardtraderPriceCacheDTO dto = cardtraderPriceCacheService.getCardtraderPriceCacheDTO(cardtraderId, lang, condition, isFoil);

        // Compruebo si la carta ya existe en cardtrader_price_cache
        if(dto == null){
            // Obtengo lista de cartas a través del cardtraderId
            JsonNode node = cardTraderAPI.fetchCardProducts(cardtraderId);

            // Leo la respuesta JSON y convierto a objeto de tipo CardtraderListing e inserto en cardtrader_listing
            readCardtraderJsonNode(cardtraderId, node);

            // Insertar lista filtrada en cardtrader_price_cache
            cardtraderPriceCacheService.convertCardtraderListingToPriceCache();

            // Creo objeto DTO con los resultados de la consulta
            dto = cardtraderPriceCacheService.getCardtraderPriceCacheDTO(cardtraderId, lang, condition, isFoil);
        }

        // Consulta precios de la carta elegida por el usuario
        return dto;
    }

    // Leer JSON de cartas y mapearlas a Lista<CardTraderListing>
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
