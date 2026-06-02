package com.magic.investor_api.dto;

import com.magic.investor_api.scryfall.dto.ScryfallCardDTO;

import java.time.LocalDate;

public class UserCollectionDTO {

    private Long userId;
    private Long cardId;
    private Double purchasePrice;
    private int quantity;
    private LocalDate addedAt;
    private ScryfallCardDTO card;

    // CONSTRUTOR
    public UserCollectionDTO() {
    }

    public UserCollectionDTO(Long userId, Long cardId){
        this.userId = userId;
        this.cardId = cardId;
    }

    // GETTERS Y SETTERS
    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public LocalDate getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDate addedAt) {
        this.addedAt = addedAt;
    }

    public ScryfallCardDTO getScryfallCard() {
        return card;
    }

    public void setScryfallCard(ScryfallCardDTO card) {
        this.card = card;
    }

    @Override
    public String toString() {
        return "UserCollectionDTO{" +
                "userId=" + userId +
                ", cardId=" + cardId +
                ", purchasePrice=" + purchasePrice +
                ", quantity=" + quantity +
                ", addedAt=" + addedAt +
                ", card=" + card +
                '}';
    }
}
