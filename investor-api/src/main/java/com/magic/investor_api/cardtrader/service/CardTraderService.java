package com.magic.investor_api.cardtrader.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.magic.investor_api.cardmarketPrice.model.CardmarketPrice;
import com.magic.investor_api.cardmarketPrice.service.CardmarketPriceService;
import com.magic.investor_api.cardtrader.dao.CardtraderDAO;
import com.magic.investor_api.cardtrader.model.CardtraderCard;
import com.magic.investor_api.api.CardTraderAPI;
import com.magic.investor_api.cardtrader.repository.CardtraderRepository;
import com.magic.investor_api.cardtraderPrice.dto.CardtraderPriceDTO;
import com.magic.investor_api.cardtraderPrice.service.CardtraderPriceService;
import com.magic.investor_api.expansion.dao.ExpansionDAO;
import com.magic.investor_api.scryfall.dto.ScryfallCardDTO;
import com.magic.investor_api.scryfall.model.ScryfallCard;
import com.magic.investor_api.scryfall.service.ScryfallService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardTraderService {

    private final CardTraderAPI cardTraderAPI;
    private final CardtraderRepository cardtraderRepository;
    private final ExpansionDAO expansionDAO;
    private final CardtraderDAO cardtraderDAO;
    private final ScryfallService scryfallService;
    private final CardtraderPriceService cardtraderPriceService;

    // Obtiene lista de expansiones de la API cardtrader
    public void downloadCardtraderExpansion(){
        // Obtengo lista de expansiones de cardtrader y la inserto en la BD
        expansionDAO.insertCardtraderExpansion(cardTraderAPI.getExpansions());
    }

    // Obtiene todas las cartas de las expansiones
    public void cardsByExpansion() {
        List<CardtraderCard> batch = new ArrayList<>();
        // Lista de id de expansiones de card_trader_expansion
        List<Long> expansionList = expansionDAO.getExpansionListId();

        for(Long e : expansionList){
            try{
                // Obtiene todas las cartas de cada expansión
                JsonNode root = cardTraderAPI.getCardtraderCards(e);

                if (root == null || root.isEmpty() || root.isMissingNode() || !root.isArray())  {
                    continue;
                }
                // Iteración de cartas dentro de la expansión
                for (JsonNode node : root) {
                    // Mapeo los datos obtenidos del JSON a objeto CardtraderCard
                    batch.add(mapNodeToCardtraderCard(node)); // Agrego la carta al batch
                }
                // Vuelco el batch a la tabla cardtrader_card
                if (!batch.isEmpty()) {
                    cardtraderRepository.saveAll(batch);
                    batch.clear();
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    // Mapea JSON de cartas a CardtraderCard
    private CardtraderCard mapNodeToCardtraderCard(JsonNode node){
        CardtraderCard card = new CardtraderCard();

        card.setScryfallId(node.path("scryfall_id").asText());
        JsonNode carmarketId = node.path("card_market_ids");
        Long cardmarketId = (carmarketId.isArray() && carmarketId.size() > 0)
                ? carmarketId.get(0).asLong()
                : null;
        card.setCardmarketId(cardmarketId);
        card.setCardtraderId(node.path("id").asLong());
        card.setName(node.path("name").asText());
        card.setRarity(node.path("fixed_properties").path("mtg_rarity").asText());
        card.setExpansionId(node.path("expansion_id").asLong());
        card.setCollectorNumber(node.path("fixed_properties").path("collector_number").asText());

        // El nombre y código de la edición lo añadiré desde un JOIN en la BD
        // Ya que no vienen en la respuesta de la API
        return card;
    }

    // Inserta set_code y set_name en cardtrader_card
    public void mapCardtraderSets(){
        cardtraderDAO.mappingCardtraderSets();
    }

    // Obtener cardtrader_id a través del scryfall_id
    public Long getCardtraderIdByScryfallId(String scryfallId){
        return cardtraderDAO.selectCardTraderId(scryfallId);
    }

    // Obtener precios de cardtrader
    public CardtraderPriceDTO getCardtraderPrices(Long cardId, String scryfallId, String lang, String condition, boolean isFoil){

        ScryfallCardDTO card = new ScryfallCardDTO();
        card.setId(cardId);
        card.setScryfallId(scryfallId);
        card.setLang(lang);
        card.setCondition(condition);
        card.setFoil(isFoil);

        return cardtraderPriceService.getCardtraderPrice(card);
    }
}
