package com.magic.investor_api.cardmarketPrice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cardmarket_price")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardmarketPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cardmarket_id")
    private Long cardmarketId;

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

    @Column(name = "avg_foil", precision = 10, scale = 2)
    private BigDecimal avgFoil;

    @Column(name = "low_foil", precision = 10, scale = 2)
    private BigDecimal lowFoil;

    @Column(name = "trend_foil", precision = 10, scale = 2)
    private BigDecimal trendFoil;

    @Column(name = "avg1_foil", precision = 10, scale = 2)
    private BigDecimal avg1Foil;

    @Column(name = "avg7_foil", precision = 10, scale = 2)
    private BigDecimal avg7Foil;

    @Column(name = "avg30_foil", precision = 10, scale = 2)
    private BigDecimal avg30Foil;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}