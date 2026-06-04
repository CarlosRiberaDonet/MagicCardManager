package com.magic.investor_api.cardtraderListing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cardtrader_listing")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardtraderListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_id")
    private Long cardId;

    @Column(name ="cardtrader_id")
    private Long cardtraderId;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name ="card_condition")
    private String condition;

    @Column(name = "lang")
    private String lang;

    @Column(name = "is_foil")
    private boolean isFoil;

    @Column(name = "url")
    private String url;

    @Column(name = "fetched_at")
    private LocalDateTime fetchedAt;
}
