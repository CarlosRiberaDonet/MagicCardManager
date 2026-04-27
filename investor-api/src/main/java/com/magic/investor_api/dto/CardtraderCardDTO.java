package com.magic.investor_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardtraderCardDTO {

    @JsonProperty("id")
    private Long cardtraderId;

    @JsonProperty("card_market_ids")
    private List<Long> cardmarketIds;

    @JsonProperty("scryfall_id")
    private String scryfallId;

    @JsonProperty("expansion_id")
    private Long expansionId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("version")
    private String version;

    @JsonProperty("fixed_properties")
    private FixedProperties fixedProperties;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FixedProperties {
        @JsonProperty("mtg_rarity")
        private String rarity;

        @JsonProperty("collector_number")
        private String collectorNumber;

        public String getRarity() {
            return rarity;
        }

        public String getCollectorNumber() {
            return collectorNumber;
        }
    }

    // GETTERS Y SETTERS

    public Long getCardtraderId() {
        return cardtraderId;
    }

    public void setCardtraderId(Long cardtraderId) {
        this.cardtraderId = cardtraderId;
    }

    public List<Long> getCardmarketIds() {
        return cardmarketIds;
    }

    public void setCardmarketIds(List<Long> cardmarketIds) {
        this.cardmarketIds = cardmarketIds;
    }

    public String getScryfallId() {
        return scryfallId;
    }

    public void setScryfallId(String scryfallId) {
        this.scryfallId = scryfallId;
    }

    public Long getExpansionId() {
        return expansionId;
    }

    public void setExpansionId(Long expansionId) {
        this.expansionId = expansionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public FixedProperties getFixedProperties() {
        return fixedProperties;
    }

    public void setFixedProperties(FixedProperties fixedProperties) {
        this.fixedProperties = fixedProperties;
    }
}