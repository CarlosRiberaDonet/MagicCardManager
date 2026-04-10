package com.magic.investor_api.dao;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class CardTraderAPI {

    private final RestTemplate restTemplate = new RestTemplate();

    // La URL base de CardTrader
    private static final String BLUEPRINTS_URL = "https://api.cardtrader.com/api/v2/blueprints";

    private static final String BASE_URL = "https://api.cardtrader.com/api/v2/marketplace/products";

    private final String apiToken = [REDACTED-SECRET];

    /**
     * Llama a la API de CardTrader y devuelve el JSON como String.
     *
     * @param blueprintId Id de la carta en CardTrader
     //* @param page Página de resultados
     * @return JSON de la API
     */
    public String fetchCardProducts(String blueprintId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(MediaType.parseMediaTypes("application/json"));
        headers.setBearerAuth(apiToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Construye la URL con parámetros
        String url = UriComponentsBuilder.fromUriString(BASE_URL)
                .queryParam("blueprint_id", blueprintId)
                .queryParam("page")
                .toUriString();

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        return response.getBody();
    }

    /*public String fetchBlueprints(int page) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(MediaType.parseMediaTypes("application/json"));
        headers.setBearerAuth(apiToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromUriString(BLUEPRINTS_URL)
                .queryParam("page", page)
                .toUriString();

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        return response.getBody();
    }*/

}