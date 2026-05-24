package com.magic.investor_api.Scheduler;

import com.magic.investor_api.API.CardmarketDownloader;
import com.magic.investor_api.API.ScryfallAPI;
import com.magic.investor_api.controller.CardTraderController;
import com.magic.investor_api.dao.ScryfallCardDAO;
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

    private final CardmarketDownloader cardmarketDownloader;
    private final ScryfallAPI scryfallDownloader;
    private final ScryfallCardDAO scryfallCardDAO;
    private final ScryfallService scryfallService;
    private final CardTraderService cardTraderService;
    private final CardmarketService cardmarketService;


    @Scheduled(cron = "0 0 6 * * *") // todos los días a las 6:00 AM
    public void updateBBDD() throws IOException {

        // Inserta en la BBD las ediciones de scryfall
        scryfallService.importScryfallEditionsToDB();
        // Inserta en la BBD el JSON de cartas de scryfall
        scryfallService.importScryfallCardsToBD();
        //Descargar ediciones de cardtrader
        cardTraderService.downloadCardtraderExpansion();
        // Descargar cartas de cardtrader por número de edición
        cardTraderService.cardsByExpansion();
        // Descargar JSON con guia de precios de cardmarket
        cardmarketDownloader.downloadGuidePrice();
        // Importa precios de JSON cardmarket a BD
        cardmarketService.importGuidePricesToBD();
        // Mapeo cardtrader_card con scryfall_card

        // scryfallCardDAO.updateScryfallPrices();


    }
}
