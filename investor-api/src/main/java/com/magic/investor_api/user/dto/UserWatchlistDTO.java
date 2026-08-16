package com.magic.investor_api.user.dto;

import com.magic.investor_api.scryfall.dto.ScryfallCardDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UserWatchlistDTO {

    private Long userId;
    private Long cardId;
    private BigDecimal lastPrice;
    private String condition;
    private boolean isFoil;
    private LocalDate addedAt;
    private String lang;
    private ScryfallCardDTO scryfallCardDTO;

    // CONSTRUCTOR
    public UserWatchlistDTO() {
    }

    public UserWatchlistDTO(Long userId, Long cardId, String condition, boolean isFoil, String lang, BigDecimal lastPrice) {
        this.userId = userId;
        this.cardId = cardId;
        this.lastPrice = lastPrice;
        this.condition = condition;
        this.isFoil = isFoil;
        this.lang = lang;
    }

    public UserWatchlistDTO(Long userId, Long cardId, String condition, boolean isFoil, String lang) {
        this.userId = userId;
        this.cardId = cardId;
        this.condition = condition;
        this.isFoil = isFoil;
        this.addedAt = addedAt;
        this.lang = lang;
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

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
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

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
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
