package com.magic.investor_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "name")
    private String name;

    @Column(name = "rarity")
    private String rarity;

    @Column(name = "expansion_id")
    private Long expansionId;

    @Column(name = "set_name")
    private String setName;

    @Column(name = "set_code")
    private String setCode;

    @Column(name = "collector_number")
    private String collectorNumber;
}