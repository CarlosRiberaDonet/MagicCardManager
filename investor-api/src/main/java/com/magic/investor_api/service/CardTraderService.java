package com.magic.investor_api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.magic.investor_api.API.CardTraderAPI;
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


    // Obtiene lista de expansiones de la API cardtrader
    public void downloadCardtraderExpansion(){
        // Obtengo lista de expansiones de cardtrader y la inserto en la BD
        expansionDAO.insertCardtraderExpansion(cardTraderAPI.getExpansions());
    }

    // Obtiene todas las cartas de las expansiones
    public void cardsByExpansion() {
        List<CardtraderCard> batch = new ArrayList<>();
        // CheckPoint de la última expansión procesada
        Long lastExpansionProcessed = expansionDAO.getLastExpansionId();
        // Lista con ids de card_trader_expansion
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
                    batch.add(mapNodeToCardtraderCard(node)); // Agrego la carta al batch
                }
                // Vuelco el batch a la tabla cardtrader_card
                if (!batch.isEmpty()) {
                    cardtraderRepository.saveAll(batch);
                    System.out.println("Expansión: " + e + " Añadida. Cartas: " + batch.size());
                    batch.clear();
                    expansionDAO.updateLastExpansionId(e);
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    // Mapea JSON a CardtraderCard
    private CardtraderCard mapNodeToCardtraderCard(JsonNode node){
        System.out.println("Mapeando carta: " + node.path("name").asText());
        CardtraderCard card = new CardtraderCard();
        // IDENTIFICADORES PRINCIPALES
        card.setCardtraderId(node.path("id").asLong());
        JsonNode cm = node.path("card_market_ids");
        Long cardmarketId = (cm.isArray() && cm.size() > 0)
                ? cm.get(0).asLong()
                : null;
        card.setCardmarketId(cardmarketId);
        card.setScryfallId(node.path("scryfall_id").asText());

        card.setExpansionId(node.path("expansion_id").asLong());
        card.setName(node.path("name").asText());
        card.setImageUrl(node.path("image_url").asText());
        card.setRarity(node.path("fixed_properties").path("mtg_rarity").asText());
        card.setCollectorNumber(node.path("fixed_properties").path("collector_number").asText());
        card.setVersion(node.path("version").asText());

        return card;
    }
}
