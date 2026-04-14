package com.magic.investor_api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "card_variant")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(name = "cardtrader_id", nullable = false, unique = true)
    private Long cardtraderId;

    @Column(name = "cardmarket_id")
    private Long cardmarketId;

    @Column(name = "scryfall_id")
    private String scryfallId;

    @Column(name = "expansion_id")
    private Long expansionId;

    @Column(name = "version")
    private String version;
}
