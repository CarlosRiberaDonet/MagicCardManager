package com.magic.investor_api.dto;

import lombok.Data;

import java.util.List;

@Data
public class CardtraderCardDTO {
    private Long cardtraderId;
    private List<Long> cardmarketIds;
    private String scryfallId;
    private Long expansionId;
    private String name;
    private String imageUrl;
    private String version;
}