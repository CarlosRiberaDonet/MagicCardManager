package com.magic.investor_api.scryfall.controller;

import com.magic.investor_api.CardPageDTO;
import com.magic.investor_api.expansion.ScryfallSet;
import com.magic.investor_api.scryfall.dto.ScryfallCardDTO;
import com.magic.investor_api.scryfall.service.ScryfallService;
import com.magic.investor_api.expansion.service.ExpansionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/scryfall")
@RequiredArgsConstructor
public class ScryfallController {

    private final ScryfallService scryfallService;
    private final ExpansionService expansionService;

    // Descargar ediciones desde scryfall
    @PostMapping("/editions")
    public ResponseEntity<String> editions(){
        try{
            System.out.println("Proceso de descarga iniciado...");
            scryfallService.importScryfallEditionsToDB();
            return ResponseEntity.ok("Descarga completada.");
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al conectar con Scryfall: " + e.getMessage());
        }
    }

    // Descargar cartas de scryfall e importarlas a la BD
    @PostMapping("/cards")
    public ResponseEntity<String> cards() {
        try {
            System.out.println("Proceso de descarga iniciado...");
            scryfallService.importScryfallCardsToBD();
            return ResponseEntity.ok("Descarga completada.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al conectar con Scryfall: " + e.getMessage());
        }
    }

    // Obtiene y devuelve lista de expansiones de scryfall_set
    @GetMapping("/sets")
    public List<ScryfallSet> getSetsList(){
        return expansionService.getSets();
    }

    // Buscar carta con filtros
    @GetMapping("/search")
    public CardPageDTO searchCards(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String setCode,  // Edición
            @RequestParam(required = false) String rarity,   // Rareza
            @RequestParam(required = false) String lang,     // Idioma
            @RequestParam(required = false) String typeLine, // Tipo de carta
            @RequestParam(required = false) Double minPrice, // Precio mínimo
            @RequestParam(required = false) Double maxPrice, // Precio máximo
            @RequestParam(required = false) String orderBy,   // "price_asc" o "price_desc"
            @RequestParam(required = false, defaultValue = "false") boolean hideNA, // false -> muestra cartas con precio N/A, true oculta
            @RequestParam int page,
            @RequestParam int size
    )
    {
        return scryfallService.searchCards(name, setCode, rarity, lang, typeLine, orderBy,
                minPrice, maxPrice, page, size, hideNA);
    }


    // Obtener detalles de carta por scryfall_id
    @GetMapping("/scryfallId/{scryfallId}")
    public ScryfallCardDTO searchCardById(@PathVariable  String scryfallId){
        System.out.println(scryfallId);
        return scryfallService.getCardByscryfallId(scryfallId);
    }
}
