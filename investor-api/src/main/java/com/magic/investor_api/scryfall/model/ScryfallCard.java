package com.magic.investor_api.scryfall.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="scryfall_card")
@Data // Genera getters, setters, toString, equals y hashCode automáticamente
@NoArgsConstructor // Constructor vacío necesario para Hibernate
@AllArgsConstructor // Constructor con todos los campos
public class ScryfallCard {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scryfall_id", length = 36)
    private String scryfallId; // UUID de Scryfall

    @Column(name = "cardmarket_id")
    private Long cardmarketId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name="printed_name")
    private String printedName;

    @Column(name = "lang", length = 3, nullable = false)
    private String lang;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "rarity", length = 20, nullable = false)
    private String rarity;

    @Column(name = "set_name", length = 150)
    private String setName;

    @Column(name = "set_code")
    private String setCode;

    @Column(name = "collector_number", length = 20)
    private String collectorNumber;

    @Column(name="cardmarket_url")
    private String cardmarketURL;

    @Column(name="price")
    private BigDecimal price;

    @Column(name="price_foil")
    private BigDecimal priceFoil;

    @Column(name = "type_line")
    private String typeLine;

    @Column(name = "border_color", length = 20)
    private String borderColor;

    @Column(name="frame")
    private String frame;

    @Column(name = "is_foil")
    private boolean isFoil;

    @Column(name = "is_reprint")
    private boolean isReprint;

    @Column(name = "released_at")
    private LocalDate releasedAt;
}
