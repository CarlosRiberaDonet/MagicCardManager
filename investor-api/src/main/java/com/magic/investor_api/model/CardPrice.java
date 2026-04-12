package com.magic.investor_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "card_price")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_variant_id", nullable = false)
    private CardVariant cardVariant;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private Source source;

    @Column(name = "avg", precision = 10, scale = 2)
    private BigDecimal avg;

    @Column(name = "low", precision = 10, scale = 2)
    private BigDecimal low;

    @Column(name = "avg_foil", precision = 10, scale = 2)
    private BigDecimal avgFoil;

    @Column(name = "low_foil", precision = 10, scale = 2)
    private BigDecimal lowFoil;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum Source {
        CARDMARKET,
        CARDTRADER
    }
}