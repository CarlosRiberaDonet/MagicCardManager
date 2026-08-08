package com.magic.investor_api.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.investor_api.cardtrader.model.CardtraderSet;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;


@Service
public class CardTraderAPI {
    @Value("${CARDTRADER_API_TOKEN}")
    private String apiToken;

    private final RestTemplate restTemplate = new RestTemplate();

    // La URL base de CardTrader
    private static final String BLUEPRINTS_URL = "https://api.cardtrader.com/api/v2/blueprints";

    private static final String BASE_URL = "https://api.cardtrader.com/api/v2/marketplace/products";

    // Obtener lista de expansiones de CardTrader
    public List<CardtraderSet> getExpansions() {

        String url = "https://api.cardtrader.com/api/v2/expansions";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        try {
            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(
                    response.getBody(),
                    new TypeReference<List<CardtraderSet>>() {}
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Obtener cartas mediante su id de expansion
    public JsonNode getCardtraderCards(Long expansionId) {
        String url = "https://api.cardtrader.com/api/v2/blueprints/export"
                + "?expansion_id=" + expansionId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Error parsing CardTrader response", e);
        }
    }

    public JsonNode fetchCardProducts(Long cardtraderId) {

        HttpHeaders headers = new HttpHeaders();
        System.out.println("TOKEN PRESENTE: " + (apiToken != null));
        System.out.println("TOKEN LONGITUD: " + (apiToken != null ? apiToken.length() : 0));
        System.out.println("TOKEN INICIO: " +
                (apiToken != null && apiToken.length() > 20
                        ? apiToken.substring(0, 20)
                        : "INVALIDO"));
        headers.setAccept(MediaType.parseMediaTypes("application/json"));
        headers.setBearerAuth(apiToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromUriString(BASE_URL)
                .queryParam("blueprint_id", cardtraderId)
                .queryParam("page", 1)
                .toUriString();

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        try {
            return new ObjectMapper().readTree(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}