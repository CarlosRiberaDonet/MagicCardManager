package com.magic.investor_api.scryfall.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.magic.investor_api.cardmarketPrice.model.CardmarketPrice;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
@JsonPropertyOrder({
        "id",
        "cardmarketId",
        "scryfall_id",
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

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private LocalDate releasedAt;
    private CardmarketPrice cardmarketPrice;
}
