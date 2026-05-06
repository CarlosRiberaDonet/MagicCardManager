package com.magic.investor_api.Scheduler;

import com.magic.investor_api.API.CardmarketDownloader;
import com.magic.investor_api.API.ScryfallDownloader;
import com.magic.investor_api.controller.CardTraderController;
import com.magic.investor_api.dao.ScryfallCardDAO;
import com.magic.investor_api.service.CardmarketImportService;
import com.magic.investor_api.service.ScryfallService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SchedulerTask {

    private final CardmarketDownloader cardmarketDownloader;
    private final CardmarketImportService cardmarketImportService;
    private final ScryfallDownloader scryfallDownloader;
    private final ScryfallCardDAO scryfallCardDAO;
    private final ScryfallService scryfallService;

    private final CardTraderController cardTraderController;

    public SchedulerTask(CardmarketDownloader cardmarketDownloader, CardmarketImportService cardmarketImportService,
                         ScryfallDownloader scryfallDownloader, ScryfallCardDAO scryfallCardDAO, ScryfallService scryfallService,
                         CardTraderController cardTraderController){

        this.cardmarketDownloader = cardmarketDownloader;
        this.cardmarketImportService = cardmarketImportService;
        this.scryfallDownloader = scryfallDownloader;
        this.scryfallCardDAO = scryfallCardDAO;
        this.scryfallService = scryfallService;
        this.cardTraderController = cardTraderController;
    }

    @Scheduled(cron = "0 0 6 * * *") // todos los días a las 6:00 AM
    public void updateBBDD() throws IOException {

        //System.out.println("Descargando guía de precios de cardmarket.");
        //cardmarketDownloader.downloadGuidePrice();

        //System.out.println("Importando guide_prices.json en card_price");
        //cardmarketImportService.importGuidePricesToBD();

        System.out.println("Descargando cartas de Scryfall.");
        scryfallDownloader.startFullImport();

        // Volcar JSON de scryfall en la tabla scryfall_card de la BD
        scryfallService.importScryfallCardsToBD();

        // Actualizar precios desde card_price en scryfall_card
        scryfallCardDAO.updateScryfallPrices();
    }
}
