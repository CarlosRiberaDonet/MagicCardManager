package com.magic.investor_api.service;

import com.magic.investor_api.API.CardTraderAPI;
import com.magic.investor_api.dao.ExpansionDAO;
import com.magic.investor_api.model.Expansion;
import com.magic.investor_api.repository.CardtraderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpansionService {


    private final ExpansionDAO expansionDAO;

    public ExpansionService(ExpansionDAO expansionDAO) {
        this.expansionDAO = expansionDAO;
    }

    // Obtengo lista con los nombres de las expansiones
    public List<Expansion> getExpansionListName(){
        return expansionDAO.selectExpansionList();
    }
}
