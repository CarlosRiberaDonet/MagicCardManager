package com.magic.investor_api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.investor_api.API.CardTraderAPI;
import com.magic.investor_api.dao.CardVariantDAO;
import com.magic.investor_api.dao.ExpansionDAO;
import com.magic.investor_api.dto.CardVariantDTO;
import com.magic.investor_api.model.Card;
import org.springframework.stereotype.Service;

import java.util.List;


// Tengo que insertar valores en la tabla card_variant obteniendo la lsita de cartas mediante su edicion y creando un objeto CARDVARIANDTO que contenga los 3 id
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
    public void getCardVariantList() {

        ObjectMapper objectMapper = new ObjectMapper();

        // Obtengo los id de cada edición
        List<Long> expansionList = expansionDAO.getExpansionList();

        // Itero sobre la lista
        for (Long id : expansionList) {

            String jsonCards = cardTraderAPI.fetchBlueprints(id); // JSON bruto

            try {
                List<CardVariantDTO> list = objectMapper.readValue(
                        jsonCards,
                        new TypeReference<List<CardVariantDTO>>() {}
                );

                // Itero sobre la lista obtenida
                for (CardVariantDTO dto : list) {
                    cardVariantDAO.insertCardVariant(dto);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
