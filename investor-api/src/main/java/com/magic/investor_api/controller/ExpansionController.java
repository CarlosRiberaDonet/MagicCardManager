package com.magic.investor_api.controller;

import com.magic.investor_api.dao.ExpansionDAO;
import com.magic.investor_api.service.ExpansionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/expansion")
public class ExpansionController {

    private final ExpansionService expansionService;
    public ExpansionController(ExpansionService expansionService){
        this.expansionService = expansionService;
    }
    // Obtiene lista con todas las expansiones
    @GetMapping("/all")
    public List<String> downloadEdition() {
       return expansionService.getExpansionListName();
    }
}
