package com.magic.investor_api.cardtraderListing.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.magic.investor_api.cardtrader.ports.CardTraderAPI;
import com.magic.investor_api.cardtrader.service.CardTraderService;
import com.magic.investor_api.cardtraderListing.dao.CardtraderListingDAO;
import com.magic.investor_api.cardtraderPrice.dto.CardtraderPriceDTO;
import com.magic.investor_api.cardtraderListing.repository.CardtraderListingRepository;
import com.magic.investor_api.cardtraderListing.model.CardtraderListing;
import com.magic.investor_api.cardtraderPrice.service.CardtraderPriceService;
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
    private final CardtraderListingDAO cardtraderListingDAO;
    private final CardtraderPriceService cardtraderPriceService;

    // Compruebo si este cardtraderId ya existe en cardtrader_listing
    public CardtraderListing checkCardtraderId(Long cartraderId){
        return cardtraderListingDAO.checkCardtraderIdOnCardtraderListing(cartraderId);
    }

    //  Obtener y mapear JsonNode del mercado de cartas cardtrader
    public boolean updateCardPrice(Long cardtraderId){

        // Obtengo lista de cartas a través del cardtraderId
        JsonNode node = cardTraderAPI.fetchCardProducts(cardtraderId);

        // Leo la respuesta JSON y convierto a objeto de tipo CardtraderListing e inserto en cardtrader_listing
        readCardtraderJsonNode(cardtraderId, node);

        // Insertar lista filtrada en cardtrader_price_cache
        cardtraderPriceService.convertCardtraderListingToPriceCache();

        return true;
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
                    listing.setCardMappingId(cardId);
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
