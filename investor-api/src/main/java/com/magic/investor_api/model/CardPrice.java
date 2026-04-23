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
    //@Column(name = "source", nullable = false)
    private Source source;

    @Column(name = "avg", precision = 10, scale = 2)
    private BigDecimal avg;

    @Column(name = "low", precision = 10, scale = 2)
    private BigDecimal low;

    @Column(name = "avg1", precision = 10, scale = 2)
    private BigDecimal avg1;

    @Column(name = "avg7", precision = 10, scale = 2)
    private BigDecimal avg7;

    @Column(name = "avg30", precision = 10, scale = 2)
    private BigDecimal avg30;

    @Column(name = "foil")
    private Boolean foil;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Source {
        CARDMARKET,
        CARDTRADER
    }
}