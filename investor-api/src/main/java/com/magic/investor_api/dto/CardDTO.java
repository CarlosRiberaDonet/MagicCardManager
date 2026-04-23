package com.magic.investor_api.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.magic.investor_api.model.Card;
import com.magic.investor_api.model.CardPrice;

import java.math.BigDecimal;
import java.time.LocalDate;
@JsonPropertyOrder({
        "id",
        "cardmarketId",
        "name",
        "lang",
        "imageUrl",
        "rarity",
        "releasedAt",
        "setCode",
        "setName",
        "collectorNumber",
        "typeLine",
        "borderColor",
        "foil",
        "reprint",
        "cardmarketURL",
        "avg",
        "low",
        "avgFoil",
        "lowFoil",
        "updatedAt"
})

public class CardDTO {

    private Long id;
    private String scryfallId;
    private Long cardmarketId;
    private Long cardtraderId;
    private String name;
    private String lang;
    private String imageUrl;
    private String rarity;
    private LocalDate releasedAt;
    private String setCode;
    private String setName;
    private String collectorNumber;
    private String typeLine;
    private String borderColor;
    private String frameEffects;
    private boolean isFoil;
    private boolean isReprint;
    private String cardmarketURL;

    // Precios
    private BigDecimal avg;
    private BigDecimal low;
    private BigDecimal avgFoil;
    private BigDecimal lowFoil;

    // CONSTRUCTOR
    public CardDTO(Card card, CardPrice cardPrice){
        // Campos de la carta
        this.id = card.getId();
        this.scryfallId = card.getScryfallId();
        this.cardmarketId = card.getCardmarketId();
        this.cardtraderId = card.getCardtraderId();
        this.name = card.getName();
        this.lang = card.getLang();
        this.imageUrl = card.getImageUrl();
        this.rarity = card.getRarity();
        this.releasedAt = card.getReleasedAt();
        this.setCode = card.getSetCode();
        this.setName = card.getSetName();
        this.collectorNumber = card.getCollectorNumber();
        this.typeLine = card.getTypeLine();
        this.borderColor = card.getBorderColor();
        this.isFoil = card.isFoil();
        this.isReprint = card.isReprint();
        this.cardmarketURL = card.getCardmarketURL();

        // Precios normales
        if (cardPrice != null) {
            this.avg = cardPrice.getAvg();
            this.low = cardPrice.getLow();
        }
    }

    public CardDTO(){

    }

    // GETTERS Y SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScryfallId() {
        return scryfallId;
    }

    public void setScryfallId(String scryfallId) {
        this.scryfallId = scryfallId;
    }

    public Long getCardmarketId() {
        return cardmarketId;
    }

    public void setCardmarketId(Long cardmarketId) {
        this.cardmarketId = cardmarketId;
    }

    public Long getCardtraderId() {
        return cardtraderId;
    }

    public void setCardtraderId(Long cardtraderId) {
        this.cardtraderId = cardtraderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public LocalDate getReleasedAt() {
        return releasedAt;
    }

    public void setReleasedAt(LocalDate releasedAt) {
        this.releasedAt = releasedAt;
    }

    public String getSetCode() {
        return setCode;
    }

    public void setSetCode(String setCode) {
        this.setCode = setCode;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public String getCollectorNumber() {
        return collectorNumber;
    }

    public void setCollectorNumber(String collectorNumber) {
        this.collectorNumber = collectorNumber;
    }

    public String getTypeLine() {
        return typeLine;
    }

    public void setTypeLine(String typeLine) {
        this.typeLine = typeLine;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public String getFrameEffects() {
        return frameEffects;
    }

    public void setFrameEffects(String frameEffects) {
        this.frameEffects = frameEffects;
    }

    public boolean isFoil() {
        return isFoil;
    }

    public void setFoil(boolean foil) {
        isFoil = foil;
    }

    public boolean isReprint() {
        return isReprint;
    }

    public void setReprint(boolean reprint) {
        isReprint = reprint;
    }

    public String getCardmarketURL() {
        return cardmarketURL;
    }

    public void setCardmarketURL(String cardmarketURL) {
        this.cardmarketURL = cardmarketURL;
    }

    public BigDecimal getAvg() {
        return avg;
    }

    public void setAvg(BigDecimal avg) {
        this.avg = avg;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getAvgFoil() {
        return avgFoil;
    }

    public void setAvgFoil(BigDecimal avgFoil) {
        this.avgFoil = avgFoil;
    }

    public BigDecimal getLowFoil() {
        return lowFoil;
    }

    public void setLowFoil(BigDecimal lowFoil) {
        this.lowFoil = lowFoil;
    }
}
