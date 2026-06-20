package com.magic.investor_api.user.dto;

import com.magic.investor_api.scryfall.dto.ScryfallCardDTO;

import java.time.LocalDate;

public class UserCollectionDTO {

    private Long userId;
    private Long cardId;
    private Double purchasePrice;
    private int quantity;
    private String cardCondition;
    private LocalDate addedAt;
    private ScryfallCardDTO card;

    public UserCollectionDTO() {}

    public UserCollectionDTO(Long userId, Long cardId){
        this.userId = userId;
        this.cardId = cardId;
    }

    public UserCollectionDTO(Long userId, Long cardId, Double purchasePrice,
                             int quantity, String cardCondition,
                             LocalDate addedAt, ScryfallCardDTO card) {
        this.userId = userId;
        this.cardId = cardId;
        this.purchasePrice = purchasePrice;
        this.quantity = quantity;
        this.cardCondition = cardCondition;
        this.addedAt = addedAt;
        this.card = card;
    }

    // getters/setters únicos

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

    public String getCardCondition() {
        return cardCondition;
    }

    public void setCardCondition(String cardCondition) {
        this.cardCondition = cardCondition;
    }

    public LocalDate getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDate addedAt) {
        this.addedAt = addedAt;
    }

    public ScryfallCardDTO getCard() {
        return card;
    }

    public void setCard(ScryfallCardDTO card) {
        this.card = card;
    }
}