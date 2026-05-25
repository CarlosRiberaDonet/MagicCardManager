package com.magic.investor_api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.magic.investor_api.API.CardTraderAPI;
import com.magic.investor_api.dao.CardMappingDAO;
import com.magic.investor_api.dao.CardtraderDAO;
import com.magic.investor_api.dao.ExpansionDAO;
import com.magic.investor_api.model.CardtraderCard;
import com.magic.investor_api.repository.CardtraderRepository;
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
    private final CardMappingDAO cardMappingDAO;


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

                if (root == null || root.isMissingNode() || !root.isArray()) {
                    continue;
                }
                // Iteración de cartas dentro de la expansión
                for (JsonNode node : root) {
                    // Mapeo los datos obtenidos del JSON a objeto CardtraderCard
                    System.out.println(node);
                    batch.add(mapNodeToCardtraderCard(node)); // Agrego la carta al batch
                }
                // Vuelco el batch a la tabla cardtrader_card
                if (!batch.isEmpty()) {
                    cardtraderRepository.saveAll(batch);
                    batch.clear();
                    expansionDAO.updateLastExpansionId(e);
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    // Mapea JSON de cartas a CardtraderCard
    private CardtraderCard mapNodeToCardtraderCard(JsonNode node){
        CardtraderCard card = new CardtraderCard();
        // IDENTIFICADORES PRINCIPALES
        card.setScryfallId(node.path("scryfall_id").asText());
        JsonNode cm = node.path("card_market_ids");
        Long cardmarketId = (cm.isArray() && cm.size() > 0)
                ? cm.get(0).asLong()
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
}
