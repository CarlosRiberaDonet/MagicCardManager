package com.magic.investor_api.Scheduler;

import com.magic.investor_api.API.CardmarketDownloader;
import com.magic.investor_api.API.ScryfallAPI;
import com.magic.investor_api.controller.CardTraderController;
import com.magic.investor_api.dao.ScryfallCardDAO;
import com.magic.investor_api.service.CardmarketService;
import com.magic.investor_api.service.ScryfallService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SchedulerTask {

    private final CardmarketDownloader cardmarketDownloader;
    private final CardmarketService cardmarketImportService;
    private final ScryfallAPI scryfallDownloader;
    private final ScryfallCardDAO scryfallCardDAO;
    private final ScryfallService scryfallService;

    private final CardTraderController cardTraderController;

    public SchedulerTask(CardmarketDownloader cardmarketDownloader, CardmarketService cardmarketImportService,
                         ScryfallAPI scryfallDownloader, ScryfallCardDAO scryfallCardDAO, ScryfallService scryfallService,
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

        // cardmarketDownloader.downloadGuidePrice();

        // cardmarketImportService.importGuidePricesToBD();

        // scryfallDownloader.downloadCards();

        // scryfallService.importScryfallCardsToBD();

        // scryfallCardDAO.updateScryfallPrices();


    }
}
