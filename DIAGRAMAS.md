# Diagramas técnicos — Magic Investor

> Generado a partir del código real de [`investor-api`](../investor-api) en el repositorio `MagicCardManager`. Complementa a [`DOCUMENTACION_TECNICA.md`](DOCUMENTACION_TECNICA.md).

## Índice

1. [Diagrama de clases — Entidades JPA](#1-diagrama-de-clases--entidades-jpa)
2. [Diagrama de clases — DTOs](#2-diagrama-de-clases--dtos)
3. [Diagrama entidad-relación (BD real)](#3-diagrama-entidad-relación-bd-real)
4. [Notas sobre fuentes y discrepancias](#4-notas-sobre-fuentes-y-discrepancias)

---

## 1. Diagrama de clases — Entidades JPA

Entidades `@Entity` reales del proyecto (paquete `com.magic.investor_api.*.model`). Ninguna declara relaciones JPA (`@ManyToOne`, `@OneToMany`...): todas las claves foráneas son campos planos (`Long`), y las uniones se resuelven a mano en los DAO con SQL (`JOIN`). Las relaciones del diagrama son, por tanto, **relaciones lógicas por clave foránea**, no relaciones JPA declaradas.

```mermaid
classDiagram
    class ScryfallCard {
        -Long id
        -String scryfallId
        -Long cardmarketId
        -String name
        -String printedName
        -String lang
        -String imageUrl
        -String rarity
        -String setName
        -String setCode
        -String collectorNumber
        -String cardmarketURL
        -String typeLine
        -String borderColor
        -String frame
        -boolean isFoil
        -boolean isReprint
        -LocalDate releasedAt
    }

    class ScryfallSet {
        -Long id
        -String setCode
        -String name
        -String iconSvgUri
        -LocalDate releasedAt
    }

    class CardmarketPrice {
        -Long id
        -Long cardmarketId
        -BigDecimal avg
        -BigDecimal low
        -BigDecimal trend
        -BigDecimal avg1
        -BigDecimal avg7
        -BigDecimal avg30
        -BigDecimal avgFoil
        -BigDecimal lowFoil
        -BigDecimal trendFoil
        -BigDecimal avg1Foil
        -BigDecimal avg7Foil
        -BigDecimal avg30Foil
        -LocalDateTime updatedAt
    }

    class CardtraderCard {
        -Long id
        -Long cardtraderId
        -Long cardmarketId
        -String scryfallId
        -String name
        -String rarity
        -Long expansionId
        -String setName
        -String setCode
        -String collectorNumber
    }

    class CardtraderSet {
        -Long id
        -String code
        -String name
    }

    class CardtraderPrice {
        -Long id
        -Long cardId
        -Long cardtraderId
        -String lang
        -String condition
        -boolean isFoil
        -BigDecimal avg
        -BigDecimal low
        -BigDecimal trend
        -BigDecimal avg1
        -BigDecimal avg7
        -BigDecimal avg30
        -LocalDateTime updatedAt
    }

    class CardtraderListing {
        -Long id
        -Long cardId
        -String scryfallId
        -Long cardtraderId
        -BigDecimal price
        -String condition
        -String lang
        -boolean isFoil
        -String url
        -LocalDateTime fetchedAt
    }

    class User {
        -Long id
        -String email
        -String password
        -String role
    }

    ScryfallCard "0..*" --> "1" ScryfallSet : setCode → set_code
    ScryfallCard "1" --> "0..1" CardmarketPrice : cardmarketId
    CardtraderCard "0..*" --> "1" CardtraderSet : expansion_id
    CardtraderCard "1" --> "0..1" CardtraderPrice : cardtraderId
    CardtraderListing "0..*" --> "1" ScryfallCard : cardId
    CardtraderListing "0..*" --> "1" CardtraderCard : cardtraderId
```

---

## 2. Diagrama de clases — DTOs

Objetos de transferencia entre capas y hacia el frontend (paquetes `*.dto`). Se muestran junto a la entidad `User`, que no tiene DTO propio salvo `UserDTO`.

```mermaid
classDiagram
    class UserDTO {
        -Long id
        -String email
        -String password
        -String role
    }

    class ModifyUserRequest {
        -String newEmail
        -String newPassword
        -String oldPassword
    }

    class UserCollectionDTO {
        -Long userId
        -Long cardId
        -Double purchasePrice
        -String lang
        -int quantity
        -String condition
        -boolean isFoil
        -LocalDate addedAt
        -ScryfallCardDTO card
    }

    class UserWatchlistDTO {
        -Long userId
        -Long cardId
        -Double lastPrice
        -String condition
        -boolean isFoil
        -LocalDate addedAt
        -ScryfallCardDTO scryfallCardDTO
    }

    class ScryfallCardDTO {
        -Long id
        -Long cardTraderId
        -String scryfallId
        -Long cardmarketId
        -String name
        -String printedName
        -String lang
        -String imageUrl
        -String rarity
        -String setName
        -String setCode
        -String iconSvgUri
        -String collectorNumber
        -String cardmarketURL
        -String typeLine
        -String borderColor
        -String frame
        -String condition
        -boolean isFoil
        -boolean isReprint
        -LocalDate releasedAt
        -Object cardPrice
        -String priceSource
    }

    class CardPageDTO {
        -int totalCards
        -int page
        -List~ScryfallCardDTO~ cardDTOList
    }

    class CardtraderCardDTO {
        -Long cardtraderId
        -List~Long~ cardmarketIds
        -String scryfallId
        -Long expansionId
        -String name
        -String imageUrl
        -String version
    }

    class CardtraderPriceDTO {
        -Long cardId
        -Long cardtraderId
        -String scryfallId
        -String lang
        -String condition
        -boolean isFoil
        -BigDecimal avg
        -BigDecimal low
        -BigDecimal trend
        -BigDecimal avg1
        -BigDecimal avg7
        -BigDecimal avg30
        -LocalDate updatedAt
    }

    UserCollectionDTO "1" *-- "0..1" ScryfallCardDTO : card
    UserWatchlistDTO "1" *-- "0..1" ScryfallCardDTO : scryfallCardDTO
    CardPageDTO "1" *-- "0..*" ScryfallCardDTO : cardDTOList
```

**Nota:** `ScryfallCardDTO.cardPrice` está tipado como `Object` en el código — agrega el precio final de la carta, que puede provenir de Cardmarket o de CardTrader según `priceSource`. Esto resuelve, a nivel de DTO, la falta de un identificador común entre las tres fuentes externas.

---

## 3. Diagrama entidad-relación (BD real)

Basado en los nombres de tabla y `JOIN`s usados realmente en los DAO (`ScryfallCardDAO`, `UserDAO`, `CardmarketPriceDAO`), no en el fichero `BD_MAGIC.sql` del repositorio, que está desactualizado.

```mermaid
erDiagram
    scryfall_card ||--o| cardmarket_price : "cardmarket_id"
    scryfall_card }o--|| scryfall_set : "set_code"
    scryfall_card ||--o{ user_collection : "card_id"
    scryfall_card ||--o{ user_watchlist : "card_id"
    scryfall_card ||--o{ cardtrader_listing : "card_id"
    cardtrader_card }o--|| cardtrader_set : "expansion_id"
    cardtrader_card ||--o| cardtrader_price : "cardtrader_id"
    cardtrader_card ||--o{ cardtrader_listing : "cardtrader_id"
    user ||--o{ user_collection : "user_id"
    user ||--o{ user_watchlist : "user_id"

    scryfall_card {
        bigint id PK
        string scryfall_id
        bigint cardmarket_id
        string name
        string set_code FK
        string rarity
        string lang
        boolean is_foil
        boolean is_reprint
    }

    scryfall_set {
        bigint id PK
        string set_code
        string name
        date released_at
    }

    cardmarket_price {
        bigint id PK
        bigint cardmarket_id
        decimal avg
        decimal trend
        datetime updated_at
    }

    cardtrader_card {
        bigint id PK
        bigint cardtrader_id
        bigint cardmarket_id
        string scryfall_id
        bigint expansion_id FK
    }

    cardtrader_set {
        bigint id PK
        string code
        string name
    }

    cardtrader_price {
        bigint id PK
        bigint cardtrader_id
        decimal avg
        datetime updated_at
    }

    cardtrader_listing {
        bigint id PK
        bigint card_id FK
        bigint cardtrader_id FK
        decimal price
        string card_condition
    }

    user {
        bigint id PK
        string email UK
        string password
        string role
    }

    user_collection {
        bigint id PK
        bigint user_id FK
        bigint card_id FK
        double purchase_price
        int quantity
        string card_condition
        boolean is_foil
    }

    user_watchlist {
        bigint id PK
        bigint user_id FK
        bigint card_id FK
        double last_price
        string card_condition
        boolean is_foil
    }
```

---

## 4. Notas sobre fuentes y discrepancias

- Estos diagramas se han construido leyendo directamente `investor-api/src/main/java/...` (entidades `@Entity`, DTOs) y las sentencias SQL de los DAO — no a partir de documentación previa ni de memoria de conversaciones anteriores.
- `BD_MAGIC.sql` (raíz del repo) **no coincide** con el esquema real usado por el código: tablas (`card` vs `scryfall_card`), claves primarias (`VARCHAR(36)` vs `BIGINT AUTO_INCREMENT`) y ausencia de `user_collection`/`user_watchlist`. Si ese `.sql` se usa para desplegar la BD en algún entorno, conviene regenerarlo o eliminarlo para evitar confusión.
- Ninguna entidad declara relaciones JPA (`@ManyToOne`, etc.); todo el acceso relacional se hace manualmente vía JDBC en los DAO. Esto es coherente con la decisión de diseño ya documentada de usar DAO manual para tener control total sobre las queries.
- Este documento no sustituye a `DOCUMENTACION_TECNICA.md`; lo complementa. Si tras tu revisión decides ajustar la documentación técnica (por ejemplo, para reflejar que CardTrader ya está implementado), dímelo y lo actualizo.
