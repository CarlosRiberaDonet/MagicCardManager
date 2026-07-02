package com.magic.investor_api.admin;

import com.magic.investor_api.cardtrader.dao.CardtraderDAO;
import com.magic.investor_api.api.CardTraderAPI;
import com.magic.investor_api.api.CardmarketDownloader;
import com.magic.investor_api.cardtrader.service.CardTraderService;
import com.magic.investor_api.cardtraderPrice.service.CardtraderPriceService;
import com.magic.investor_api.cardmarketPrice.service.CardmarketPriceService;
import com.magic.investor_api.scryfall.dao.ScryfallCardDAO;
import com.magic.investor_api.scryfall.service.ScryfallService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SchedulerTask {

    private final ScryfallService scryfallService;
    private final ScryfallCardDAO scryfallCardDAO;
    private final CardTraderService cardTraderService;
    private final CardtraderDAO cardtraderDAO;
    private final CardmarketDownloader cardmarketDownloader;
    private final CardmarketPriceService cardmarketPriceService;
    private final CardtraderPriceService cardtraderPriceService;

    private final CardTraderAPI cardTraderAPI;

    @Scheduled(cron = "0 0 6 * * *") // todos los días a las 6:00 AM
    public void updateBBDD() throws IOException {

        //1. Descarga e inserta en la BBD las ediciones de scryfall
        //scryfallService.importScryfallEditionsToDB();

        //2. Descargar cartas de scryfallInserta y las inserta en BBD
        //scryfallService.importScryfallCardsToBD();

        //3. Descargar e insertar ediciones de cardtrader
        //cardTraderService.downloadCardtraderExpansion();

        //4. Descargar cartas de cardtrader por número de edición
        //cardTraderService.cardsByExpansion();

        //5. Mapear campos set_name/set_code a cardtrader_card
        //cardtraderDAO.mappingCardtraderSets();

        //9. Descargar JSON con guia de precios de cardmarket
        //cardmarketDownloader.downloadGuidePrice();

        //10. Descargar precio de cardmarket
        //cardmarketDownloader.downloadGuidePrice();

        //12. Importa precios de JSON cardmarket a BD
        //cardmarketPriceService.importGuidePricesToBD();

        //13. Actualizar precios de scryfall_card desde carmarket_price
        //scryfallCardDAO.updateScryfallPrice();
    }
}
