package com.magic.investor_api.service;


import com.magic.investor_api.dao.ExpansionDAO;
import com.magic.investor_api.model.ScryfallSet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SetService {

    private final ExpansionDAO expansionDAO;

    public List<ScryfallSet> getSets(){
        return expansionDAO.selectScryfallExpansionList();
    }
}
