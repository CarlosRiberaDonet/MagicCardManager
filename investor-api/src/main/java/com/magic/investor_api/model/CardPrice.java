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

    // Relación: Muchos registros de precio pertenecen a una sola carta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(name = "price_eur", precision = 10, scale = 2)
    private BigDecimal priceEur;

    @Column(name = "price_eur_foil", precision = 10, scale = 2)
    private BigDecimal priceEurFoil;

    @CreationTimestamp // Esto rellena automáticamente la fecha al insertar
    @Column(name = "updated_at", nullable = false, updatable = false)
    private LocalDateTime updatedAt;
}