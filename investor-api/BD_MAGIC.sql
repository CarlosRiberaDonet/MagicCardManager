-- CREATE DATABASE magic;

USE magic;

-- SELECT @@hostname, @@port, @@version;

-- SHOW DATABASES;

-- CREATE USER 'investor_app'@'%' IDENTIFIED BY 'unaPasswordSegura';

-- GRANT ALL PRIVILEGES ON magic.* TO 'investor_app'@'%';

-- FLUSH PRIVILEGES;

-- SELECT user, host FROM mysql.user WHERE user = 'investor_app';

-- SHOW GRANTS FOR 'investor_app'@'%';

-- SELECT CURRENT_USER();


-- Ediciones descargadas desde scryfall
CREATE TABLE scryfall_set (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
set_code VARCHAR(10) UNIQUE NOT NULL,
name VARCHAR(255) NOT NULL,
released_at DATE,
icon_svg_uri VARCHAR(255)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Ediciones descargadas desde cardtrader
CREATE TABLE cardtrader_set (
    id BIGINT PRIMARY KEY,   -- expansion_id de CardTrader (externo)
    code VARCHAR(100),
    name VARCHAR(255),
    INDEX idx_code (code),
    INDEX idx_name (name)
)CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Tabla que uso como checkpoint para contuniar actualizando cartas a través de su expansion (cardtrader)
CREATE TABLE sync_progress (
    code_expansion VARCHAR(255) PRIMARY KEY,
    last_expansion_id BIGINT NOT NULL
);

-- Inicialización mínima
INSERT INTO sync_progress (code_expansion, last_expansion_id)
VALUES ('card_variant_sync', 0);

-- Fuente: Scryfall (catálogo base, todos los idiomas)
CREATE TABLE scryfall_card (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    scryfall_id VARCHAR(100) NOT NULL UNIQUE,
    cardmarket_id BIGINT,
    name VARCHAR(255),
    printed_name VARCHAR(255),
    lang CHAR(5),
    image_url VARCHAR(255),
    rarity VARCHAR(20),
    set_name VARCHAR(150),
    set_code VARCHAR(100),
    collector_number VARCHAR(20),
    cardmarket_url VARCHAR(255),
    type_line VARCHAR(500),
    border_color VARCHAR(10),
    frame VARCHAR (30),
    is_foil BOOLEAN,
    is_reprint BOOLEAN,
    released_at DATE,
    game_changer BOOLEAN DEFAULT FALSE,
    INDEX idx_name (name),
    INDEX idx_printed_name (printed_name),
    INDEX idx_cardmarket (cardmarket_id),
    INDEX idx_scryfall (scryfall_id),
    INDEX idx_rarity (rarity),
    INDEX idx_lang (lang),
    INDEX idx_set_name (set_name),
    INDEX idx_set_code (set_code),
    INDEX idx_type_line (type_line)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Fuente: CardTrader (blueprints, para precios en tiempo real)
CREATE TABLE cardtrader_card (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    scryfall_id VARCHAR(100),
    cardmarket_id BIGINT,
    cardtrader_id BIGINT,
    name VARCHAR(255),
    rarity VARCHAR(20),
    expansion_id BIGINT NOT NULL,
    set_name VARCHAR(255),
    set_code VARCHAR(100),
    collector_number VARCHAR(20),
    INDEX idx_cardtrader (cardtrader_id),
    INDEX idx_cardmarket (cardmarket_id),
    INDEX idx_scryfall (scryfall_id),
    
    CONSTRAINT fk_expansion_id FOREIGN KEY (expansion_id) REFERENCES cardtrader_set(id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Fuente: CardMarket pricelist (precios de referencia diarios)
CREATE TABLE cardmarket_price (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cardmarket_id BIGINT NOT NULL UNIQUE,
    avg DECIMAL(10,2),
    low DECIMAL(10,2),
    trend DECIMAL(10,2),
    avg1 DECIMAL(10,2),
    avg7 DECIMAL(10,2),
    avg30 DECIMAL(10,2),
	avg_foil DECIMAL(10,2),
    low_foil DECIMAL(10,2),
    trend_foil DECIMAL(10,2),
    avg1_foil DECIMAL(10,2),
    avg7_foil DECIMAL(10,2),
    avg30_foil DECIMAL(10,2),
    updated_at DATETIME,
    INDEX idx_cardmarket (cardmarket_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE cardtrader_listing (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
	card_id BIGINT NOT NULL,
    scryfall_id VARCHAR(100),
    cardtrader_id BIGINT NOT NULL,
    price DECIMAL(10,2),
	card_condition VARCHAR(20),
    lang CHAR(5),
    is_foil BOOLEAN DEFAULT FALSE,
	url VARCHAR(500),
    fetched_at DATETIME NOT NULL,

	CONSTRAINT uk_listing UNIQUE (card_id, lang, card_condition, is_foil),
    INDEX idx_lookup (card_id, scryfall_id, cardtrader_id, lang, card_condition, is_foil)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE cardtrader_price(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_id BIGINT,
    cardtrader_id BIGINT NOT NULL,
    scryfall_id VARCHAR(100),
    lang CHAR(5) NOT NULL,
    card_condition VARCHAR(20) NOT NULL,
    is_foil BOOLEAN DEFAULT FALSE,
    avg DECIMAL(10,2),
    low DECIMAL(10,2),
    trend DECIMAL(10,2),
    avg1 DECIMAL(10,2),
    avg7 DECIMAL(10,2),
    avg30 DECIMAL(10,2),
    updated_at DATETIME NOT NULL,
CONSTRAINT uk_price UNIQUE (card_id, lang, card_condition, is_foil),

    INDEX idx_lookup (
		card_id,
        cardtrader_id,
        scryfall_id,
        lang,
        card_condition,
        is_foil
    )
)CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR (20) NOT NULL DEFAULT 'USER'
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE user_collection (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    card_id BIGINT NOT NULL,
	purchase_price DECIMAL(10,2),
    quantity INT DEFAULT 1,
    card_condition VARCHAR(20),
    is_foil BOOLEAN DEFAULT FALSE,
    lang CHAR(5),
	added_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT fk_card_id FOREIGN KEY (card_id) REFERENCES scryfall_card(id)
)CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE user_watchlist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    card_id BIGINT NOT NULL,
    last_price DECIMAL(10,2),
    card_condition VARCHAR(20),
    is_foil BOOLEAN DEFAULT FALSE,
    lang CHAR(5),
    added_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_id_watchlist FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT fk_card_id_watchlist FOREIGN KEY (card_id) REFERENCES scryfall_card(id),
    CONSTRAINT uq_watchlist UNIQUE (user_id, card_id, card_condition, is_foil)
)
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


SELECT * FROM user;
SELECT COUNT(*) FROM scryfall_set;