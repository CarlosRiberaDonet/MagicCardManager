package com.magic.investor_api.cardtrader.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardtraderCardDTO {
    private Long cardtraderId;
    private List<Long> cardmarketIds;
    private String scryfallId;
    private Long expansionId;
    private String name;
    private String imageUrl;
    private String version;
}