package com.magic.investor_api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.magic.investor_api.API.CardTraderAPI;
import com.magic.investor_api.dao.ExpansionDAO;
import com.magic.investor_api.model.CardtraderExpansion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardTraderService {

    private final CardTraderAPI cardTraderAPI;
    private final ExpansionDAO expansionDAO;

    // Obtiene todas las cartas de las expansiones
    public void cardsByExpansion() {

        // Lista de expansion_id
        List<Long> expansionList = expansionDAO.getExpansionListId();

        for (Long expansionId : expansionList) {

            // 1. Llamada a CardTrader: blueprint export por expansión
            JsonNode root = cardTraderAPI.getCardtraderCards(expansionId);

            if (root == null || root.isMissingNode()) {
                continue;
            }

            // 2. Nodo principal con las cartas
            JsonNode data = root.path("data");

            if (!data.isArray()) {
                continue;
            }

            // 3. Iteración de cartas dentro de la expansión
            for (JsonNode card : data) {

                // IDENTIFICADORES PRINCIPALES
                long cardtraderId = card.path("id").asLong();
                long expansion = card.path("expansion_id").asLong();

                String name = card.path("name").asText(null);
                String imageUrl = card.path("image_url").asText(null);
                String version = card.path("version").asText(null);

                // PROPIEDADES MTG (anidadas)
                JsonNode fixed = card.path("fixed_properties");
                String rarity = fixed.path("mtg_rarity").asText(null);
                String collectorNumber = fixed.path("collector_number").asText(null);

                // IDS EXTERNOS
                Long cardmarketId = null;
                JsonNode cmArray = card.path("card_market_ids");

                if (cmArray.isArray() && cmArray.size() > 0) {
                    cardmarketId = cmArray.get(0).asLong();
                }

                String scryfallId = card.path("scryfall_id").asText(null);

                // =========================
                // DEBUG (fase actual: solo lectura)
                // =========================

                System.out.println(
                        "CT_ID: " + cardtraderId +
                                " | NAME: " + name +
                                " | RARITY: " + rarity +
                                " | COL#: " + collectorNumber +
                                " | EXP: " + expansion
                );

                // =========================
                // FUTURO (NO IMPLEMENTADO AÚN)
                // =========================
                // Aquí irá:
                // - mapeo a entity CardtraderCard
                // - batch insert en DAO
                // - control de duplicados
            }
        }
    }
}
