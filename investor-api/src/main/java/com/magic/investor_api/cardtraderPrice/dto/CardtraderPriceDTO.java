package com.magic.investor_api.cardtraderPrice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardtraderPriceDTO {

    private Long cardId;
    private Long cardtraderId;
    private String scryfallId;
    private String lang;
    private String condition;
    private boolean isFoil;
    private BigDecimal avg;
    private BigDecimal low;
    private BigDecimal trend;
    private BigDecimal avg1;
    private BigDecimal avg7;
    private BigDecimal avg30;
    private LocalDate fetchedAt;
}
