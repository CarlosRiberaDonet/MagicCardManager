package com.magic.investor_api.dto;

import java.util.List;

public class CardPageDTO {

    private int totalCards;
    private int page;
    private int size;
    private List<CardDTO> cardDTOList;

    // CONSTRUCTOR
    public CardPageDTO(int totalCards, int page, int size, List<CardDTO> cardDTOList) {
        this.totalCards = totalCards;
        this.page = page;
        this.size = size;
        this.cardDTOList = cardDTOList;
    }

    public CardPageDTO() {

    }

    // GETTERS Y  SETTERS

    public List<CardDTO> getCardDTOList() {
        return cardDTOList;
    }

    public void setCardDTOList(List<CardDTO> cardDTOList) {
        this.cardDTOList = cardDTOList;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalCards() {
        return totalCards;
    }

    public void setTotalCards(int totalCards) {
        this.totalCards = totalCards;
    }
}
