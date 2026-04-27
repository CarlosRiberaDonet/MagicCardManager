package com.magic.investor_api.dto;

import java.util.List;

public class CardPageDTO {

    private int totalCards;
    private int page;
    private List<ScryfallCardDTO> cardDTOList;

    // CONSTRUCTOR
    public CardPageDTO(int totalCards, int page, List<ScryfallCardDTO> cardDTOList) {
        this.totalCards = totalCards;
        this.page = page;
        this.cardDTOList = cardDTOList;
    }

    public CardPageDTO() {

    }

    // GETTERS Y  SETTERS

    public List<ScryfallCardDTO> getCardDTOList() {
        return cardDTOList;
    }

    public void setCardDTOList(List<ScryfallCardDTO> cardDTOList) {
        this.cardDTOList = cardDTOList;
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
