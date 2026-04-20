package com.magic.investor_api.model;

public class CardVariantUnmatched {

    private String name;
    private Long expansionId;
    private String collectorNumber;
    private Long cardtraderId;
    private Long cardmarketId;
    private String scryfallId;

    // CONSTRUCTOR

    public CardVariantUnmatched(String name, Long expansionId, String collectorNumber, Long cardtraderId, Long cardmarketId, String scryfallId) {
        this.name = name;
        this.expansionId = expansionId;
        this.collectorNumber = collectorNumber;
        this.cardtraderId = cardtraderId;
        this.cardmarketId = cardmarketId;
        this.scryfallId = scryfallId;
    }

    public CardVariantUnmatched() {

    }

    // GETTERS Y SETTERS

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getExpansionId() {
        return expansionId;
    }

    public void setExpansionId(Long expansionId) {
        this.expansionId = expansionId;
    }

    public String getCollectorNumber() {
        return collectorNumber;
    }

    public void setCollectorNumber(String collectorNumber) {
        this.collectorNumber = collectorNumber;
    }

    public Long getCardtraderId() {
        return cardtraderId;
    }

    public void setCardtraderId(Long cardtraderId) {
        this.cardtraderId = cardtraderId;
    }

    public Long getCardmarketId() {
        return cardmarketId;
    }

    public void setCardmarketId(Long cardmarketId) {
        this.cardmarketId = cardmarketId;
    }

    public String getScryfallId() {
        return scryfallId;
    }

    public void setScryfallId(String scryfallId) {
        this.scryfallId = scryfallId;
    }
}
