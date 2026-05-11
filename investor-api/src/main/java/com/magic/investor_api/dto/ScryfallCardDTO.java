package com.magic.investor_api.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
        "iconSvgUri",
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

public class ScryfallCardDTO {

    private Long id;
    private String scryfallId;
    private Long cardmarketId;
    private Long cardtraderId;
    private String name;
    private String printedName;
    private String lang;
    private String imageUrl;
    private String rarity;
    private String setName;
    private String setCode;
    private String iconSvgUri;
    private String collectorNumber;
    private String cardmarketURL;
    private BigDecimal price;
    private BigDecimal priceFoil;
    private String typeLine;
    private String borderColor;
    private String frame;
    private boolean isFoil;
    private boolean isReprint;
    private LocalDate  releasedAt;
    private CardPrice cardPrice;

   // CONSTRUCTOR

    public ScryfallCardDTO(Long id, String scryfallId, Long cardmarketId, Long cardtraderId,
                           String name, String printedName, String lang, String imageUrl,
                           String rarity, String setName, String setCode, String iconSvgUri, String collectorNumber,
                           String cardmarketURL, BigDecimal price, BigDecimal priceFoil,
                           String typeLine, String borderColor, String frame,
                           boolean isFoil, boolean isReprint,
                           LocalDate releasedAt, CardPrice cardPrice) {
        this.id = id;
        this.scryfallId = scryfallId;
        this.cardmarketId = cardmarketId;
        this.cardtraderId = cardtraderId;
        this.name = name;
        this.printedName = printedName;
        this.lang = lang;
        this.imageUrl = imageUrl;
        this.rarity = rarity;
        this.setName = setName;
        this.setCode = setCode;
        this.iconSvgUri = iconSvgUri;
        this.collectorNumber = collectorNumber;
        this.cardmarketURL = cardmarketURL;
        this.price = price;
        this.priceFoil = priceFoil;
        this.typeLine = typeLine;
        this.borderColor = borderColor;
        this.frame = frame;
        this.isFoil = isFoil;
        this.isReprint = isReprint;
        this.releasedAt = releasedAt;
        this.cardPrice = cardPrice;
    }

    public ScryfallCardDTO() {
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

    public String getPrintedName() {
        return printedName;
    }

    public void setPrintedName(String printedName) {
        this.printedName = printedName;
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

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {this.setName = setName;}

    public String getSetCode() {
        return setCode;
    }

    public void setSetCode(String setCode) {
        this.setCode = setCode;
    }

    public String getIconSvgUri() {
        return iconSvgUri;
    }

    public void setIconSvgUri(String iconSvgUri) {
        this.iconSvgUri = iconSvgUri;
    }

    public String getCollectorNumber() {
        return collectorNumber;
    }

    public void setCollectorNumber(String collectorNumber) {
        this.collectorNumber = collectorNumber;
    }

    public String getCardmarketURL() {
        return cardmarketURL;
    }

    public void setCardmarketURL(String cardmarketURL) {
        this.cardmarketURL = cardmarketURL;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPriceFoil() {
        return priceFoil;
    }

    public void setPriceFoil(BigDecimal priceFoil) {
        this.priceFoil = priceFoil;
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

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
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

    public LocalDate getReleasedAt() {
        return releasedAt;
    }

    public void setReleasedAt(LocalDate releasedAt) {
        this.releasedAt = releasedAt;
    }

    public CardPrice getCardPrice() {
        return cardPrice;
    }

    public void setCardPrice(CardPrice cardPrice) {
        this.cardPrice = cardPrice;
    }
}
