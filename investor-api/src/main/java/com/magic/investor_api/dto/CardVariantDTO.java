package com.magic.investor_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardVariantDTO {

    private Long id; // CardTrader blueprint id

    private String name;

    private String version;

    @JsonProperty("expansion_id")
    private Long expansionId;

    @JsonProperty("scryfall_id")
    private String scryfallId;

    @JsonProperty("card_market_ids")
    private List<Long> cardMarketIds;

    @JsonProperty("fixed_properties")
    private FixedProperties fixedProperties;

    // --- INNER CLASS ---
    public static class FixedProperties {

        @JsonProperty("collector_number")
        private String collectorNumber;

        public String getCollectorNumber() {
            return collectorNumber;
        }

        public void setCollectorNumber(String collectorNumber) {
            this.collectorNumber = collectorNumber;
        }
    }

    // --- GETTERS & SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getExpansionId() {
        return expansionId;
    }

    public void setExpansionId(Long expansionId) {
        this.expansionId = expansionId;
    }

    public String getScryfallId() {
        return scryfallId;
    }

    public void setScryfallId(String scryfallId) {
        this.scryfallId = scryfallId;
    }

    public List<Long> getCardMarketIds() {
        return cardMarketIds;
    }

    public void setCardMarketIds(List<Long> cardMarketIds) {
        this.cardMarketIds = cardMarketIds;
    }

    public FixedProperties getFixedProperties() {
        return fixedProperties;
    }

    public void setFixedProperties(FixedProperties fixedProperties) {
        this.fixedProperties = fixedProperties;
    }
}
