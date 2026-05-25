package com.magic.investor_api.Scheduler;

import com.magic.investor_api.API.CardTraderAPI;
import com.magic.investor_api.API.CardmarketDownloader;
import com.magic.investor_api.API.ScryfallAPI;
import com.magic.investor_api.controller.CardTraderController;
import com.magic.investor_api.dao.CardMappingDAO;
import com.magic.investor_api.dao.ScryfallCardDAO;
import com.magic.investor_api.service.CardMappingService;
import com.magic.investor_api.service.CardTraderService;
import com.magic.investor_api.service.CardmarketService;
import com.magic.investor_api.service.ScryfallService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SchedulerTask {

    private final ScryfallService scryfallService;
    private final CardTraderService cardTraderService;
    private final CardmarketDownloader cardmarketDownloader;
    private final CardmarketService cardmarketService;
    private final CardMappingService cardMappingService;

    private final CardTraderAPI cardTraderAPI;

    @Scheduled(cron = "0 0 6 * * *") // todos los días a las 6:00 AM
    public void updateBBDD() throws IOException {

        //1. Inserta en la BBD las ediciones de scryfall
        /*scryfallService.importScryfallEditionsToDB();

        //2. Inserta en la BBD el JSON de cartas de scryfall
        scryfallService.importScryfallCardsToBD();

        //3. Descargar ediciones de cardtrader
        cardTraderService.downloadCardtraderExpansion();

        //4. Descargar cartas de cardtrader por número de edición
        cardTraderService.cardsByExpansion();

        //5. Mapear campos set_name/set_code a cardtrader_card
        cardMappingService.mapCardtraderSets();

        //6. Mapear cardtrader_card desde scryfall_card
        cardMappingService.updateCardmarketIdOnCardtraderCard()

        //7. Mapear scryfall_card desde cardtrader_card
        cardMappingService.mapScryfallCard();

        //8. Insertar scryfall_id en card_mapping
        cardMappingService.insertScryfallId();

        //9. Mapeo cardmarket_id desde scryfall_card
        cardMappingService.mapCards();

        //10. Mapeo cardmarket_id desde cardtrader
        cardMappingService.mapCardmarketCards

        //11. cierre por CardMarket puente
        cardMappingService.
        //13. Descargar JSON con guia de precios de cardmarket
        cardmarketDownloader.downloadGuidePrice();

        //14. Importa precios de JSON cardmarket a BD
        cardmarketService.importGuidePricesToBD();

        //15. scryfallCardDAO.updateScryfallPrices();*/

        // Comienza entity resolution por propagación iterativa de IDs
        //16. cierre por CardMarket puente (grafo de identidades con propagación bidireccional)
        cardMappingService.lastJoin();
    }
}
