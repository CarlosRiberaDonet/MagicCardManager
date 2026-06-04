package com.magic.investor_api.expansion.service;


import com.magic.investor_api.expansion.ScryfallSet;
import com.magic.investor_api.expansion.dao.ExpansionDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpansionService {

    private final ExpansionDAO expansionDAO;

    public List<ScryfallSet> getSets(){
        return expansionDAO.selectScryfallExpansionList();
    }
}
