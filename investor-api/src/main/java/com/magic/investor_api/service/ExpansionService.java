package com.magic.investor_api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.investor_api.API.CardTraderAPI;
import com.magic.investor_api.dao.ExpansionDAO;
import com.magic.investor_api.dto.CardtraderCardDTO;
import com.magic.investor_api.model.CardtraderCard;
import com.magic.investor_api.model.Expansion;
import com.magic.investor_api.repository.CardtraderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ExpansionService {

    private final CardtraderRepository cardtraderRepository;
    private final CardTraderAPI cardTraderAPI;
    private final ExpansionDAO expansionDAO;

    public ExpansionService(CardtraderRepository cardtraderRepository, CardTraderAPI cardTraderAPI, ExpansionDAO expansionDAO) {
        this.cardtraderRepository = cardtraderRepository;
        this.cardTraderAPI = cardTraderAPI;
        this.expansionDAO = expansionDAO;
    }

    // Obtengo las expansiones desde la API de cardtrader
    public void importExpansion() {
        List<Expansion> expansions = cardTraderAPI.getExpansions();

        // Inserto la lista de expansiones en la tabla card_trader_expansion
        expansionDAO.insertExpansion(expansions);
    }

    // Recupero de la BD el campo id de las expansiones de card_trader_expansion
    public void cardsByExpansion(){

        List<CardtraderCard> batch = new ArrayList<>();
        // id de expansiones ordenados
        List<Long> expansionList = expansionDAO.getExpansionListId();
        Collections.sort(expansionList);
        // Obtengo el id de la última expansión procesada
        Long lasExpansionProcessed = expansionDAO.getLastExpansionId();
        int count = 0; // Contador

        for (Long id : expansionList) {
            if (id <= lasExpansionProcessed) continue; // Salta los ya procesados

            try{
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonCards = cardTraderAPI.getCardtraderCards(id);

                List<CardtraderCardDTO> cardList = objectMapper.readValue(jsonCards,
                        new TypeReference<List<CardtraderCardDTO>>() {});

                for (CardtraderCardDTO dto : cardList) {
                    CardtraderCard card = new CardtraderCard();
                    card.setCardtraderId(dto.getCardtraderId());
                    card.setCardmarketId(dto.getCardmarketIds() != null && !dto.getCardmarketIds().isEmpty()
                            ? dto.getCardmarketIds().get(0) : null);
                    card.setScryfallId(dto.getScryfallId());
                    card.setExpansionId(dto.getExpansionId());
                    card.setName(dto.getName());
                    card.setImageUrl(dto.getImageUrl());
                    card.setVersion(dto.getVersion());
                    card.setRarity(dto.getFixedProperties() != null ? dto.getFixedProperties().getRarity() : null);
                    card.setCollectorNumber(dto.getFixedProperties() != null ? dto.getFixedProperties().getCollectorNumber() : null);
                    batch.add(card);
                    count++;

                    // Cada 1000 cartas, vuelco a la BD
                    if (count % 1000 == 0) {
                        cardtraderRepository.saveAll(batch);
                        batch.clear();
                        System.out.println("Cartas en BD: " + count);
                    }
                }
                // Volcar las cartas que queden
                if (!batch.isEmpty()) {
                    cardtraderRepository.saveAll(batch);
                }

                expansionDAO.updateLastExpansionId(id);
            } catch (Exception e) {
                System.out.println("Error en expansión " + id + ": " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    // Obtengo lista con los nombres de las expansiones
    public List<String> getExpansionListName(){
        return expansionDAO.selectExpansionNamesList();
    }
}
