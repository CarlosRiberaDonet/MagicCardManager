package com.magic.investor_api.API;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.investor_api.model.Expansion;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;


@Service
public class CardTraderAPI {

    private final RestTemplate restTemplate = new RestTemplate();

    // La URL base de CardTrader
    private static final String BLUEPRINTS_URL = "https://api.cardtrader.com/api/v2/blueprints";

    private static final String BASE_URL = "https://api.cardtrader.com/api/v2/marketplace/products";

    private final String apiToken = [REDACTED-SECRET];

    // Obtener lista de expansiones de CardTrader
    public List<Expansion> fetchExpansions() {

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
                    new TypeReference<List<Expansion>>() {}
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Obtener cartas mediante su expansion
    public String fetchBlueprints(Long expansionId) {

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

        return response.getBody();
    }

    // Obtener cartas mediante id de cardtrader (blueprint)
    public String fetchCardProducts(String blueprintId, int page) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(MediaType.parseMediaTypes("application/json"));
        headers.setBearerAuth(apiToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Construye la URL con parámetros
        String url = UriComponentsBuilder.fromUriString(BASE_URL)
                .queryParam("blueprint_id", blueprintId)
                .queryParam("page", page)
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