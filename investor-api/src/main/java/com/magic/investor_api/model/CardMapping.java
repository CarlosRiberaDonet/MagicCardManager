package com.magic.investor_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "card_mapping")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardMapping {

    @id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scryfall_card_id")
    private String scryfallId;

    @Column(name = "carmarket_card_id")
    private Long cardmarketId;

    @Column(name = "cardtrader_card_id")
    private Long cardtraderId;
}
