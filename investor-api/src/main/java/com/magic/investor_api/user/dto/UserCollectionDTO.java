package com.magic.investor_api.user.dto;

import com.magic.investor_api.scryfall.dto.ScryfallCardDTO;

import java.time.LocalDate;

public class UserCollectionDTO {

    private Long userId;
    private Long cardId;
    private Double purchasePrice;
    private String lang;
    private int quantity;
    private String condition;
    private boolean isFoil;
    private LocalDate addedAt;
    private ScryfallCardDTO card;

    public UserCollectionDTO() {}

    public UserCollectionDTO(Long userId, Long cardId){
        this.userId = userId;
        this.cardId = cardId;
    }

    public UserCollectionDTO(Long userId, Long cardId, Double purchasePrice,
                             String cardCondition, boolean isFoil) {
        this.userId = userId;
        this.cardId = cardId;
        this.purchasePrice = purchasePrice;
        this.condition = cardCondition;
        this.isFoil = isFoil;
    }

    // getters/setters

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public boolean isFoil() {
        return isFoil;
    }

    public void setFoil(boolean foil) {
        isFoil = foil;
    }

    public ScryfallCardDTO getCard() {
        return card;
    }

    public void setCard(ScryfallCardDTO card) {
        this.card = card;
    }

    public LocalDate getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDate addedAt) {
        this.addedAt = addedAt;
    }

    @Override
    public String toString() {
        return "UserCollectionDTO{" +
                "userId=" + userId +
                ", cardId=" + cardId +
                ", purchasePrice=" + purchasePrice +
                ", lang=" + lang +
                ", quantity=" + quantity +
                ", condition='" + condition + '\'' +
                ", isFoil=" + isFoil +
                ", addedAt=" + addedAt +
                ", card=" + card +
                '}';
    }
}

