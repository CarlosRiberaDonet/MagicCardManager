package com.magic.investor_api.service;

import com.magic.investor_api.API.CardTraderAPI;
import com.magic.investor_api.dao.ExpansionDAO;
import com.magic.investor_api.model.Expansion;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpansionService {

    private final CardTraderAPI cardTraderAPI;
    private final ExpansionDAO expansionDAO;

    public ExpansionService(CardTraderAPI cardTraderAPI, ExpansionDAO expansionDAO) {
        this.cardTraderAPI = cardTraderAPI;
        this.expansionDAO = expansionDAO;
    }

    public List<Expansion> importExpansion() {

        List<Expansion> expansions = cardTraderAPI.fetchExpansions();

        expansionDAO.insertExpansion(expansions);

        return expansions;
    }
}
