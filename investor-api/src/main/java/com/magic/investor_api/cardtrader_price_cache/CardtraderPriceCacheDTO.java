package com.magic.investor_api.cardtrader_price_cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardtraderPriceCacheDTO {

    private Long id;
    private Long cardId;
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
