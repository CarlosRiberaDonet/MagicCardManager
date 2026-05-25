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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scryfall_id")
    private String scryfallId;

    @Column(name = "cardmarket_id")
    private Long cardmarketId;

    @Column(name = "cardtrader_id")
    private Long cardtraderId;
}
