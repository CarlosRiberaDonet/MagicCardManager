package com.magic.investor_api.admin;

import com.magic.investor_api.cardtrader.ports.CardTraderAPI;
import com.magic.investor_api.api.CardmarketDownloader;
import com.magic.investor_api.cardmapping.service.CardMappingService;
import com.magic.investor_api.cardtrader.service.CardTraderService;
import com.magic.investor_api.cardtrader_price_cache.service.CardtraderPriceCacheService;
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
    private final CardtraderPriceCacheService cardtraderPriceCacheService;

    private final CardTraderAPI cardTraderAPI;

    @Scheduled(cron = "0 0 6 * * *") // todos los días a las 6:00 AM
    public void updateBBDD() throws IOException {

        //1. Inserta en la BBD las ediciones de scryfall
        //scryfallService.importScryfallEditionsToDB();

        //2. Inserta en la BBD el JSON de cartas de scryfall
        //scryfallService.importScryfallCardsToBD();

        //3. Descargar ediciones de cardtrader
        //cardTraderService.downloadCardtraderExpansion();

        //4. Descargar cartas de cardtrader por número de edición
        //cardTraderService.cardsByExpansion();

        //5. Mapear campos set_name/set_code a cardtrader_card
        //cardMappingService.mapCardtraderSets();

        ////////////////////////////////////////////////////////////////////
        // ** Comienza entity resolution por propagación iterativa de IDs **
        ////////////////////////////////////////////////////////////////////
        //6. Mapear scryfall_card desde cardtrader_card
        // cardMappingService.mapScryfallCard();

        //7. Mapear cardmarket_id desde scryfall_card
        // cardMappingService.mapCardtraderCard();

        //8. Inserto scryfall_id en card_mapping
        // cardMappingService.insertScryfallId();

        //9. Relaciono scryfall_card con cardtrader_card mediante cardmarket_id en card_mapping
        // cardMappingService.mapCardmarketIdFromScryfallToCardMapping();

        //10. Relaciono cardtrader_card con scryfall_card mediante cardmarket_id en card_mapping
        // cardMappingService.mapCardmarketIdFromCardtraderCardToCardMapping();

        //11. Cierre por CardMarket puente (grafo de identidades con propagación bidireccional)
        // cardMappingService.lastJoin();

        //12. Descargar JSON con guia de precios de cardmarket
        // cardmarketDownloader.downloadGuidePrice();

        //13. Importa precios de JSON cardmarket a BD
        // cardmarketService.importGuidePricesToBD();

        //14. Actualizo precios en scryfall_card desde card_price
        // scryfallService.updateScryfallPrices();
        cardtraderPriceCacheService.converCardtraderListingToPriceCache();
    }
}
