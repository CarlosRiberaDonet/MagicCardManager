package com.magic.investor_api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.investor_api.API.CardTraderAPI;
import com.magic.investor_api.dao.CardVariantDAO;
import com.magic.investor_api.dao.ExpansionDAO;
import com.magic.investor_api.dto.CardVariantDTO;
import com.magic.investor_api.model.CardVariant;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CardVariantService {

    private final ExpansionDAO expansionDAO;
    private final CardTraderAPI cardTraderAPI;
    private final CardVariantDAO cardVariantDAO;

    public CardVariantService(ExpansionDAO expansionDAO, CardTraderAPI cardTraderAPI, CardVariantDAO cardVariantDAO) {
        this.expansionDAO = expansionDAO;
        this.cardTraderAPI = cardTraderAPI;
        this.cardVariantDAO = cardVariantDAO;
    }

    // Obtener las ediciones de la tabla card_trader_expansion mediante su id
    public List<Long> getCardVariantList() {

        List<CardVariantDTO> cardVariantDTOList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        // Obtengo los id de cada edición
        List<Long> expansionList = expansionDAO.getExpansionList();

        // Itero sobre la lista
        for (Long id : expansionList) {

            String jsonCards = cardTraderAPI.fetchBlueprints(id); // Obtengo JSON con las cartas de la expansion

            try {
                List<CardVariantDTO> cardVariantList = objectMapper.readValue(
                        jsonCards,
                        new TypeReference<List<CardVariantDTO>>() {}
                );

                // Itero sobre la lista obtenida
                for (CardVariantDTO dto : cardVariantList) {


                    // Debo buscar la carta en la tabla card de la BD mediante cardmarket_id o scryfall_id o crear un diccionario de set + collector_number
                    cardVariantDAO.insertCardVariant(dto);

                    CardVariantDTO cardVariantDTO = new CardVariantDTO();

                    cardVariantDTO.setId(dto.getId()); // BluePrint
                    cardVariantDTO.setCardMarketIds(dto.getCardMarketIds()); // cardmarketId
                    cardVariantDTO.setScryfallId(dto.getScryfallId()); // scryfall UUID
                    cardVariantDTO.setName(dto.getName());
                    cardVariantDTO.setExpansionId(dto.getExpansionId());



                }

                break;

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }
}
