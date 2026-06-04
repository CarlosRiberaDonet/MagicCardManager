package com.magic.investor_api.cardtraderPrice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cardtrader_price")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardtraderPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_id")
    private Long cardId;

    @Column(name = "lang")
    private String lang;

    @Column(name ="card_condition")
    private String condition;

    @Column(name = "is_foil")
    private boolean isFoil;

    @Column(name = "avg", precision = 10, scale = 2)
    private BigDecimal avg;

    @Column(name = "low", precision = 10, scale = 2)
    private BigDecimal low;

    @Column(name = "trend", precision = 10, scale = 2)
    private BigDecimal trend;

    @Column(name = "avg1", precision = 10, scale = 2)
    private BigDecimal avg1;

    @Column(name = "avg7", precision = 10, scale = 2)
    private BigDecimal avg7;

    @Column(name = "avg30", precision = 10, scale = 2)
    private BigDecimal avg30;

    @Column(name = "fetched_at")
    private LocalDateTime fetchedAt;
}
