package com.magic.investor_api.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.magic.investor_api.model.Card;
import com.magic.investor_api.model.CardPrice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        "trend",
        "avg1",
        "avg7",
        "avg30",
        "avgFoil",
        "lowFoil",
        "trendFoil",
        "avg1Foil",
        "avg7Foil",
        "avg30Foil",
        "updatedAt"
})

public class CardDTO {

    private String id; // El UUID de Scryfall
    private Long cardmarketId;
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
    private BigDecimal trend;
    private BigDecimal avg1;
    private BigDecimal avg7;
    private BigDecimal avg30;

    // Precios FOIL
    private BigDecimal avgFoil;
    private BigDecimal lowFoil;
    private BigDecimal trendFoil;
    private BigDecimal avg1Foil;
    private BigDecimal avg7Foil;
    private BigDecimal avg30Foil;
    private LocalDateTime updatedAt;

    // CONSTRUCTOR
    public CardDTO(Card card, CardPrice cardPrice){
        // Campos de la carta
        this.id = card.getId();
        this.cardmarketId = card.getCardmarketId();
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
        this.frameEffects = card.getFrameEffects();
        this.isFoil = card.isFoil();
        this.isReprint = card.isReprint();
        this.cardmarketURL = card.getCardmarketURL();

        // Precios normales
        if (cardPrice != null) {
            this.avg = cardPrice.getAvg();
            this.low = cardPrice.getLow();
            this.trend = cardPrice.getTrend();
            this.avg1 = cardPrice.getAvg1();
            this.avg7 = cardPrice.getAvg7();
            this.avg30 = cardPrice.getAvg30();
            this.updatedAt = cardPrice.getUpdatedAt();
            this.avgFoil = cardPrice.getAvgFoil();
            this.lowFoil = cardPrice.getLowFoil();
            this.trendFoil = cardPrice.getTrendFoil();
            this.avg1Foil = cardPrice.getAvg1Foil();
            this.avg7Foil = cardPrice.getAvg7Foil();
            this.avg30Foil = cardPrice.getAvg30Foil();
        }
    }

    public CardDTO(){

    }

    // GETTERS Y SETTERS

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCardmarketId() {
        return cardmarketId;
    }

    public void setCardmarketId(Long cardmarketId) {
        this.cardmarketId = cardmarketId;
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

    public BigDecimal getTrend() {
        return trend;
    }

    public void setTrend(BigDecimal trend) {
        this.trend = trend;
    }

    public BigDecimal getAvg1() {
        return avg1;
    }

    public void setAvg1(BigDecimal avg1) {
        this.avg1 = avg1;
    }

    public BigDecimal getAvg7() {
        return avg7;
    }

    public void setAvg7(BigDecimal avg7) {
        this.avg7 = avg7;
    }

    public BigDecimal getAvg30() {
        return avg30;
    }

    public void setAvg30(BigDecimal avg30) {
        this.avg30 = avg30;
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

    public BigDecimal getTrendFoil() {
        return trendFoil;
    }

    public void setTrendFoil(BigDecimal trendFoil) {
        this.trendFoil = trendFoil;
    }

    public BigDecimal getAvg1Foil() {
        return avg1Foil;
    }

    public void setAvg1Foil(BigDecimal avg1Foil) {
        this.avg1Foil = avg1Foil;
    }

    public BigDecimal getAvg7Foil() {
        return avg7Foil;
    }

    public void setAvg7Foil(BigDecimal avg7Foil) {
        this.avg7Foil = avg7Foil;
    }

    public BigDecimal getAvg30Foil() {
        return avg30Foil;
    }

    public void setAvg30Foil(BigDecimal avg30Foil) {
        this.avg30Foil = avg30Foil;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
