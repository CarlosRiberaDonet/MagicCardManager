package com.magic.investor_api.scryfall.service;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.investor_api.CardPageDTO;
import com.magic.investor_api.api.ScryfallAPI;
import com.magic.investor_api.cardmapping.service.CardMappingService;
import com.magic.investor_api.cardmarketPrice.model.CardmarketPrice;
import com.magic.investor_api.cardmarketPrice.service.CardmarketPriceService;
import com.magic.investor_api.cardtraderListing.service.CardtraderListingService;
import com.magic.investor_api.cardtraderPrice.dto.CardtraderPriceDTO;
import com.magic.investor_api.cardtraderPrice.service.CardtraderPriceService;
import com.magic.investor_api.expansion.dao.ExpansionDAO;
import com.magic.investor_api.expansion.ScryfallSet;
import com.magic.investor_api.scryfall.dto.ScryfallCardDTO;
import com.magic.investor_api.scryfall.model.ScryfallCard;
import com.magic.investor_api.scryfall.repository.ScryfallRepository;
import com.magic.investor_api.scryfall.dao.ScryfallCardDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScryfallService {

    private static final String path= System.getProperty("user.dir");
    private ObjectMapper objectMapper = new ObjectMapper();
    private final ScryfallAPI scryfallDownloader;
    private final ScryfallRepository ScryfallCardRepository;
    private final ExpansionDAO expansionDAO;
    private final ScryfallCardDAO scryfallCardDAO;
    private final CardmarketPriceService cardmarketPriceService;
    private final CardMappingService cardMappingService;
    private final CardtraderPriceService cardtraderPriceService;
    private final CardtraderListingService cardtraderListingService;
    private CardPageDTO cardPageDTO;


    private static final Map<String, String> LANGUAGE_MAP = Map.ofEntries(
            Map.entry("en", "language=1"),
            Map.entry("fr", "language=2"),
            Map.entry("de", "language=3"),
            Map.entry("es", "language=4"),
            Map.entry("it", "language=5"),
            Map.entry("zhs", "language=6"),
            Map.entry("jp", "language=7"),
            Map.entry("pt", "language=8"),
            Map.entry("ru", "language=9"),
            Map.entry("ko", "language=10"),
            Map.entry("zht", "language=11")
    );

    // Insertar ediciones de scryfall json en la BD
    public void importScryfallEditionsToDB() {
        // Descarga JSON con las ediciones
        scryfallDownloader.getEditions();
        String EDITIONS = path + "/src/main/resources/editions.json";
        try{
            InputStream input = new FileInputStream(EDITIONS);
            List<ScryfallSet> scryfallExpansionList = new ArrayList<>();
            JsonNode root = objectMapper.readTree(input);
            JsonNode data = root.get("data");
            for(JsonNode node : data){
                ScryfallSet edition = mapNodeToScryfallSet(node);
                scryfallExpansionList.add(edition);
            }
            // Insertar lista de expansiones en la BD
            expansionDAO.insertScryfallSet(scryfallExpansionList);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // Inserta cards de scryfall json en la BD
    public void importScryfallCardsToBD() throws IOException {

        // Descargar cartas de scryfall
        // scryfallDownloader.downloadCards();
        String CARDS = path + "/src/main/resources/cards.json";

        InputStream input = new FileInputStream(CARDS);
        JsonFactory factory = new JsonFactory();

        try (JsonParser parser = factory.createParser(input)) {

            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new IOException("Formato JSON inválido");
            }

            List<ScryfallCard> batch = new ArrayList<>();
            int totalProcessed = 0;

            while (parser.nextToken() == JsonToken.START_OBJECT) {
                JsonNode node = objectMapper.readTree(parser);

                // Lógica de mapeo
                ScryfallCard card = mapNodeToScryfallCard(node);
                batch.add(card);
                totalProcessed++;

                // Cada 1000 cartas, vuelco a la BD
                if (totalProcessed % 1000 == 0) {
                    ScryfallCardRepository.saveAll(batch);
                    batch.clear();
                    System.out.println("Cartas en BD: " + totalProcessed);
                }
            }

            // Volcar las cartas que queden
            if (!batch.isEmpty()) {
                ScryfallCardRepository.saveAll(batch);
                batch.clear();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private ScryfallSet mapNodeToScryfallSet(JsonNode node){
        ScryfallSet set = new ScryfallSet();

        set.setSetCode(node.path("code").asText());
        set.setName(node.path("name").asText());
        set.setIconSvgUri(node.path("icon_svg_uri").asText());
        String releasedAtStr = node.path("released_at").asText();
        set.setReleasedAt(LocalDate.parse(releasedAtStr));
        return set;
    }

    // Convierte JSON en objeto de tipo ScryfallCard
    private ScryfallCard mapNodeToScryfallCard(JsonNode node) {
        ScryfallCard card = new ScryfallCard();

        // Campos de identidad
        card.setScryfallId(node.path("id").asText("")); // scryfall UUID
        card.setCardmarketId(node.path("cardmarket_id").asLong());

        card.setName(node.path("name").asText(""));
        card.setPrintedName(node.path("printed_name").asText(""));

        card.setLang(node.path("lang").asText("en"));

        // Métodos para extraer la url de la carta
        card.setImageUrl(extractImageUrl(node));
        card.setRarity(node.path("rarity").asText("common"));
        card.setSetName(node.path("set_name").asText(""));
        card.setSetCode(node.path("set").asText(""));
        card.setCollectorNumber(node.path("collector_number").asText(""));
        card.setCardmarketURL(buildCardmarketUrl(node));

        // Obtener precios de la carta
        JsonNode prices = node.path("prices");
        card.setPrice(prices.path("eur").isNull() ? null : prices.path("eur").decimalValue());
        card.setPriceFoil(prices.path("eur_foil").isNull() ? null : prices.path("eur_foil").decimalValue());
        card.setTypeLine(node.path("type_line").asText(""));
        card.setBorderColor(node.path("border_color").asText(""));
        card.setFrame(node.path("frame").asText());
        card.setFoil(node.path("foil").asBoolean(false));
        card.setReprint(node.path("reprint").asBoolean(false));
        String releasedAtStr = node.path("released_at").asText("");
        card.setReleasedAt(LocalDate.parse(releasedAtStr));

        return card;
    }

    // Extraer las imagen de la URL
    public String extractImageUrl(JsonNode node) {
        // Buscar la URL de la imagen en la raíz del JSON
        JsonNode imageUris = node.path("image_uris");

        // Si no está en la raíz, buscar en el array de card_faces del JSON
        if (imageUris.isMissingNode() && node.has("card_faces")) {
            // Acceder de forma segura a la primera cara y sus uris
            imageUris = node.path("card_faces").path(0).path("image_uris");
        }

        // return imageUris.path("normal").asText(null);
        return imageUris.path("normal").isMissingNode() ? null : imageUris.path("normal").asText();
    }

    // Obtener URL de cardmarket
    public String buildCardmarketUrl(JsonNode node) {
        // Acceso seguro al nodo de la URL
        JsonNode cmNode = node.path("purchase_uris").path("cardmarket");

        // Si no existe, devuelve null
        if (cmNode.isMissingNode() || cmNode.isNull()) {
            return null;
        }

        String url = cmNode.asText();

        // Extraer metadatos con fallback para evitar errores en los replace
        String name = node.path("name").asText("");
        String lang = node.path("lang").asText("");
        String edition = node.path("set_name").asText("");

        // Reconstrucción de la URL de búsqueda a URL de Single
        // toLowerCase() porque Cardmarket es sensible a esto en sus slugs
        if (url.contains("Search?searchString=")) {
            // Limpiar caracteres que rompen URLs (comas, puntos, apóstrofes)
            String formattedEdition = edition.replace(" ", "-").replaceAll("[^a-zA-Z0-9-]", "");
            String formattedName = name.replace(" ", "-").replaceAll("[^a-zA-Z0-9-]", "");

            url = url.replace("Search?searchString=", "/Singles/" + formattedEdition + "/" + formattedName);
        }

        // Filtro de lenguaje y limpieza de tracking
        // Valido que LANGUAGE_MAP esté inicializado para que no pete aquí
        if (LANGUAGE_MAP != null && !lang.isEmpty()) {
            String languageParam = LANGUAGE_MAP.get(lang);
            if (languageParam != null) {
                // Reemplazamos el tracking de Scryfall por tu parámetro de idioma
                url = url.replace("utm_campaign=card_prices&utm_medium=text&utm_source=scryfall", languageParam);
            }
        }

        return url;
    }

    // Obtiene lista de cartas filtradas
    public CardPageDTO searchCards(String name, String setCode, String rarity, String lang,
                                   String typeLine, String orderBy, Double minPrice, Double maxPrice,
                                   int page, int size, boolean hideNA) {

        // Calcula la fila de inicio según la página — ej: página 2 con 20 resultados empieza en la fila 20
        int offset = (page - 1) * size;

        // Total de cartas que coinciden con la búsqueda
        int totalCards = scryfallCardDAO.countCardsByFilter(name, setCode, rarity, lang, typeLine, minPrice, maxPrice, hideNA);

        // Busco la carta mediante su nombre
        List<ScryfallCardDTO> cardListDTO = scryfallCardDAO.selectFiltersCard(name, setCode, rarity,
                lang, typeLine, orderBy, minPrice, maxPrice, size, offset, hideNA);
        cardPageDTO = new CardPageDTO(totalCards, page, cardListDTO);

        return cardPageDTO;
    }

    // Obtiene carta con datos completos mediante su id
    public ScryfallCardDTO getCardByscryfallId(String scryfallId){

        // Obtengo datos de la carta
        ScryfallCardDTO card = scryfallCardDAO.getScryfallCardById(scryfallId);

        // Si existe cardmarketId
        if(card.getCardmarketId() != null){
            // Trato de obtener los precios de carmarket_price
            CardmarketPrice cardmarketPrice = cardmarketPriceService.getCardmarketPrice(card.getCardmarketId());

            // Si cardmarket_price tiene precios para la carta
            if(cardmarketPrice != null){
                card.setCardPrice(cardmarketPrice);  // Le asigno los precios obtenidos
            }
        }
        // Trato de obtener cardtrader_id
        else{
           long cardTraderId = cardMappingService.getCardtraderId(scryfallId);
           if(cardTraderId > 0){
               // Trato de obtener precios de cardtrader_price
               CardtraderPriceDTO cardPrice = cardtraderPriceService.getCardtraderPriceCacheDTO(cardTraderId, card.getLang());
               if(cardPrice != null){
                   card.setCardPrice(cardPrice);
               }
           }
        }

        return card;
    }
}
