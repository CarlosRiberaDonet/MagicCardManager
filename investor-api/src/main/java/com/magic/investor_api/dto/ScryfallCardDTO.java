package com.magic.investor_api.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.magic.investor_api.model.CardPrice;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
@JsonPropertyOrder({
        "id",
        "cardmarketId",
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
    private CardPrice cardPrice;
}
