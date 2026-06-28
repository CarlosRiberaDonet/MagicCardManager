package com.magic.investor_api.user.dto;

import com.magic.investor_api.scryfall.dto.ScryfallCardDTO;

import java.time.LocalDate;

public class UserWatchlistDTO {

    private Long userId;
    private Long cardId;
    private Double lastPrice;
    private String condition;
    private boolean isFoil;
    private LocalDate addedAt;
    private ScryfallCardDTO scryfallCardDTO;

    // CONSTRUCTOR

    public UserWatchlistDTO() {
    }

    public UserWatchlistDTO(Long userId, Long cardId, Double lastPrice, String condition, boolean isFoil ) {
        this.userId = userId;
        this.cardId = cardId;
        this.lastPrice = lastPrice;
        this.condition = condition;
        this.isFoil = isFoil;
    }

    // GETTERS Y SETTERS

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

    public Double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(Double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public boolean isFoil() {
        return isFoil;
    }

    public void setFoil(boolean foil) {
        isFoil = foil;
    }

    public LocalDate getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDate addedAt) {
        this.addedAt = addedAt;
    }

    public ScryfallCardDTO getScryfallCardDTO() {
        return scryfallCardDTO;
    }

    public void setScryfallCardDTO(ScryfallCardDTO scryfallCardDTO) {
        this.scryfallCardDTO = scryfallCardDTO;
    }

    @Override
    public String toString() {
        return "UserWatchlistDTO{" +
                "userId=" + userId +
                ", cardId=" + cardId +
                ", lastPrice=" + lastPrice +
                ", condition='" + condition + '\'' +
                ", isFoil=" + isFoil +
                ", addedAt=" + addedAt +
                ", scryfallCardDTO=" + scryfallCardDTO +
                '}';
    }
}
