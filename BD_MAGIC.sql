CREATE DATABASE magic;
DROP DATABASE magic;
USE magic;

CREATE TABLE card (
    id VARCHAR(36) NOT NULL PRIMARY KEY,           -- UUID de Scryfall
    oracle_id VARCHAR(36) NOT NULL,                -- Para agrupar versiones
    cardmarket_id BIGINT NULL,                     -- ID europeo
    tcgplayer_id VARCHAR(36) NULL,                      -- ID americano
    name VARCHAR(255) NOT NULL,                    -- Nombre en inglés
    lang CHAR(3) NOT NULL,                         -- 'en', 'es', etc.
    image_url VARCHAR(255),                        -- URL de la imagen normal
    rarity VARCHAR(20) NOT NULL,                   -- common, rare, etc.
    released_at DATE,                              -- Fecha de salida
    set_code VARCHAR(10) NOT NULL,                 -- ej: 'blb'
    set_name VARCHAR(150),                         -- ej: 'Bloomburrow'
    collector_number VARCHAR(20),                  -- ej: '280'
    type_line VARCHAR(255),                        -- ej: 'Basic Land — Forest'
    mana_cost VARCHAR(100),                        -- ej: '{2}{G}'
    cmc DECIMAL(5,1) DEFAULT 0.0,                  -- Coste convertido
    border_color VARCHAR(20),                      -- black, white, etc.
    is_foil TINYINT(1) DEFAULT 0,                  -- 1 si existe versión foil
    is_reprint TINYINT(1) DEFAULT 0,               -- 1 si es reedición
    
    -- Índices para que tu buscador vuele
    INDEX idx_name (name),
    INDEX idx_cardmarket (cardmarket_id),
    INDEX idx_oracle (oracle_id),
    INDEX idx_set (set_code)
);

CREATE TABLE card_price (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_id VARCHAR(36) NOT NULL,                  -- FK hacia CARD.id
    price_eur DECIMAL(10,2) NULL,                  -- Precio normal
    price_eur_foil DECIMAL(10,2) NULL,             -- Precio brillante
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Fecha del registro
    
    -- Relación e integridad
    CONSTRAINT fk_price_card FOREIGN KEY (card_id) 
        REFERENCES CARD(id) ON DELETE CASCADE,
    
    -- Índice para buscar precios de una carta rápidamente
    INDEX idx_card_price (card_id, updated_at)
);

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

SELECT * FROM card where name ='Black Lotus' AND set_code = '2ed';