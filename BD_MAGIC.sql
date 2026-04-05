CREATE DATABASE magic;
DROP DATABASE magic;
USE magic;

CREATE TABLE card (
    id VARCHAR(36) NOT NULL PRIMARY KEY,           -- UUID de Scryfall
    oracle_id VARCHAR(36) NOT NULL,                -- Para agrupar versiones
    cardmarket_id BIGINT,                    	   -- ID europeo
    tcgplayer_id VARCHAR(36),                      -- ID americano
    name VARCHAR(255) NOT NULL,                    -- Nombre en inglés
    lang CHAR(3) NOT NULL,                         -- 'en', 'es', etc.
    image_url VARCHAR(255),                        -- URL de la imagen normal
    rarity VARCHAR(20) NOT NULL,                   -- common, rare, etc.
    released_at DATE,                              -- Fecha de salida
    set_code VARCHAR(10) NOT NULL,                 -- ej: 'blb'
    set_name VARCHAR(150),                         -- ej: 'Bloomburrow'
    collector_number VARCHAR(20),                  -- ej: '280'
    type_line VARCHAR(255),                        -- ej: 'Basic Land — Forest'
    border_color VARCHAR(20),                      -- black, white, etc.
    is_foil TINYINT(1) DEFAULT 0,                  -- 1 si existe versión foil
    is_reprint TINYINT(1) DEFAULT 0,               -- 1 si es reedición
    cardmarket_url VARCHAR(255),
    
    -- Índices para que tu buscador vuele
    INDEX idx_name (name),
    INDEX idx_cardmarket (cardmarket_id),
    INDEX idx_oracle (oracle_id),
    INDEX idx_set (set_code)
);

CREATE TABLE card_price (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_id VARCHAR(36), -- UUID de Scryfall 
    cardmarket_id BIGINT,  -- FK hacia card.id
    -- Precio normal
    avg DECIMAL(10,2),
    low DECIMAL(10,2),                  
    trend DECIMAL(10,2),
    avg1 DECIMAL (10,2),
    avg7 DECIMAL (10,2),
    avg30 DECIMAL (10,2),
    -- Precio brillante
	avg_foil DECIMAL(10,2),
    low_foil DECIMAL(10,2),                  
    trend_foil DECIMAL(10,2),
    avg1_foil DECIMAL (10,2),
    avg7_foil DECIMAL (10,2),
    avg30_foil DECIMAL (10,2),

    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Fecha del registro
    
    -- Relación e integridad
    CONSTRAINT fk_price_card FOREIGN KEY (card_id) REFERENCES card(id) ON DELETE CASCADE,
    
    -- Índice para buscar precios de una carta rápidamente
    INDEX idx_card_price (card_id, updated_at),
    INDEX idx_card_price_recent (card_id, updated_at DESC)
  
)  ENGINE=InnoDB
    DEFAULT CHARACTER SET utf8mb4 
	COLLATE utf8mb4_unicode_ci;
    
    DROP TABLE card_price;

CREATE TABLE user_inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_id VARCHAR(36) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    purchase_price DECIMAL(10,2) NOT NULL, -- Lo que pagaste de verdad
    is_foil_owned TINYINT(1) DEFAULT 0,    -- ¿Es foil la que tienes tú?
    card_condition VARCHAR(5) DEFAULT 'NM', -- Simplemente para saber qué tienes
    added_at DATE,
    CONSTRAINT fk_inventory_card FOREIGN KEY (card_id) REFERENCES CARD(id) ON DELETE CASCADE
);

-- 1. Desactivamos las comprobaciones de claves foráneas temporalmente
SET FOREIGN_KEY_CHECKS = 0;

-- 2. Cambiamos la tabla de cartas (la que dio el error original)
ALTER TABLE card CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 3. Cambiamos la tabla de inventario (para que coincidan los charsets)
ALTER TABLE user_inventory CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 4. Volvemos a activar la seguridad
SET FOREIGN_KEY_CHECKS = 1;

SELECT * FROM card;

SELECT COUNT(*) AS total_cartas
FROM card;

SELECT * FROM card WHERE name = "Altar's Light";

SELECT * FROM card WHERE id = 'dd037f62-7cef-4737-b575-942c5959f1ea';

SELECT * FROM card_price;

SELECT * FROM card_price WHERE cardmarket_id = 1;

SELECT COUNT(*) AS total_cartas
FROM card_price;

SELECT c.id, c.name, p.avg, p.low, p.trend, p.avg1, p.avg7, p.avg30 
FROM card c
JOIN card_price p ON c.id = p.card_id
WHERE c.name = "Altar's Light";
