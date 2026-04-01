package com.magic.investor_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="card")
@Data // Genera getters, setters, toString, equals y hashCode automáticamente
@NoArgsConstructor // Constructor vacío necesario para Hibernate
@AllArgsConstructor // Constructor con todos los campos
public class Card {

    @Id
    @Column(name = "id", length = 36)
    private String id; // El UUID de Scryfall

    @Column(name = "oracle_id", length = 36, nullable = false)
    private String oracleId;

    @Column(name = "cardmarket_id")
    private Long cardmarketId;

    @Column(name = "tcgplayer_id")
    private String tcgplayerId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "lang", length = 3, nullable = false)
    private String lang;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "rarity", length = 20, nullable = false)
    private String rarity;

    @Column(name = "released_at")
    private LocalDate releasedAt;

    @Column(name = "set_code", length = 10, nullable = false)
    private String setCode;

    @Column(name = "set_name", length = 150)
    private String setName;

    @Column(name = "collector_number", length = 20)
    private String collectorNumber;

    @Column(name = "type_line")
    private String typeLine;

    @Column(name = "border_color", length = 20)
    private String borderColor;

    @Column(name = "is_foil")
    private boolean isFoil;

    @Column(name = "is_reprint")
    private boolean isReprint;

    @Column(name="cardmarket_url")
    private String cardmarketURL;
}
