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

    private final String apiToken = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJjYXJkdHJhZGVyLXByb2R1Y3Rpb24iLCJzdWIiOiJhcHA6MjA2M" +
            "jQiLCJhdWQiOiJhcHA6MjA2MjQiLCJleHAiOjQ5MzE0MzI0MDYsImp0aSI6IjA5YmQ4N2UxLWY0N2ItNDY2Yi05NjBhLTFjOTUyMzE3NWM2MCI" +
            "sImlhdCI6MTc3NTc1NTIwNiwibmFtZSI6IkNhcmxvc3JpYmVyYWRvbmV0IEFwcCAyMDI2MDQwNzA0MjUyNCJ9.lq8kpmY4G0uIgnlEIxrTh" +
            "gQg27BKw51HAt9j6RzBfSz3KmkiEbHCMXxZVHV6Lx95J_PuTDmBHnpDXJSXSRB7Co7u1Z0d2bRB5dMY2CeRmUmysURg1EGZpgYwoT33ec47pz" +
            "9YTYDZiM1zaWkVHlQzmVFOOC_YW2R5LMUDxSPiTjRz24pLmgJYeWtiSK32AAikuXFzbXWimNFfkeI6UJN46Zk0T2XNPWuPYbrMXJkxHj3x6i" +
            "Uu51Wc-ku3TePnhGJ2u1_qcS5R2Rw-NKxWdtS9lTELhxxEVxkfe85Zji5VFiWISTZlNcWIIOPU-Wqdh8nS4q8mAdJzN6wKzTy2jVtbVw";

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
}