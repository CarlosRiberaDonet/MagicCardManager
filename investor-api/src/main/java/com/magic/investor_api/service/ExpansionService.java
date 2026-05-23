package com.magic.investor_api.service;

import com.magic.investor_api.API.CardTraderAPI;
import com.magic.investor_api.dao.ExpansionDAO;
import com.magic.investor_api.model.CardtraderExpansion;
import com.magic.investor_api.model.ScryfallExpansion;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpansionService {


    private final ExpansionDAO expansionDAO;
    private final CardTraderAPI cardTraderAPI;

    public ExpansionService(ExpansionDAO expansionDAO, CardTraderAPI cardTraderAPI) {

        this.expansionDAO = expansionDAO;
        this.cardTraderAPI = cardTraderAPI;
    }

    // Obtengo lista con los nombres de las expansiones de la tabla scryfall_set
    public List<ScryfallExpansion> getExpansionListName(){
        return expansionDAO.selectExpansionList();
    }

    // Obtengo todas las expansiones de cardtrader
    public void getScryfallExpansions(){
        // Inserto la lista obtenida en la BD
        expansionDAO.insertCardtraderExpansion(cardTraderAPI.getExpansions());
    }
}
