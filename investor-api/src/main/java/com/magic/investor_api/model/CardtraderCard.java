package com.magic.investor_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "cardtrader_card")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardtraderCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cardtrader_id", unique = true)
    private Long cardtraderId;

    @Column(name = "cardmarket_id")
    private Long cardmarketId;

    @Column(name = "scryfall_id")
    private String scryfallId;

    @Column(name = "expansion_id")
    private Long expansionId;

    @Column(name = "name")
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "rarity")
    private String rarity;

    @Column(name = "collector_number")
    private String collectorNumber;

    @Column(name = "version")
    private String version;
}