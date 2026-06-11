CREATE DATABASE IF NOT EXISTS webdelivery;
USE webdelivery;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS Dettaglio_Ordine_Caratteristiche;
DROP TABLE IF EXISTS Dettaglio_Ordine;
DROP TABLE IF EXISTS Ordine;
DROP TABLE IF EXISTS Caratteristica;
DROP TABLE IF EXISTS Gruppo_Esclusione;
DROP TABLE IF EXISTS Prodotto;
DROP TABLE IF EXISTS Utente;

SET FOREIGN_KEY_CHECKS = 1;

-- 1. Tabella Utente
CREATE TABLE Utente (
    id_utente INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    ruolo ENUM('cliente', 'personale', 'proprietario') NOT NULL,
    nome_completo VARCHAR(100) NOT NULL,
    telefono VARCHAR(50),
    indirizzo TEXT
);

-- 2. Tabella Prodotto
CREATE TABLE Prodotto (
    id_prodotto INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descrizione TEXT,
    prezzo_base DECIMAL(10, 2) NOT NULL,
    tempo_preparazione INT NOT NULL COMMENT 'Tempo in minuti',
    ingredienti TEXT,
    ricetta TEXT
);

-- 3. Tabella Gruppo_Esclusione
CREATE TABLE Gruppo_Esclusione (
    id_gruppo INT AUTO_INCREMENT PRIMARY KEY,
    id_prodotto INT NOT NULL,
    nome_gruppo VARCHAR(100) NOT NULL,
    FOREIGN KEY (id_prodotto) REFERENCES Prodotto(id_prodotto) ON DELETE CASCADE
);

-- 4. Tabella Caratteristica
CREATE TABLE Caratteristica (
    id_caratteristica INT AUTO_INCREMENT PRIMARY KEY,
    id_prodotto INT NOT NULL,
    id_gruppo INT DEFAULT NULL COMMENT 'Se NULL, è una caratteristica libera',
    nome VARCHAR(150) NOT NULL,
    descrizione TEXT,
    differenza_prezzo DECIMAL(10, 2) DEFAULT 0.00,
    is_default BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (id_prodotto) REFERENCES Prodotto(id_prodotto) ON DELETE CASCADE,
    FOREIGN KEY (id_gruppo) REFERENCES Gruppo_Esclusione(id_gruppo) ON DELETE SET NULL
);

-- 5. Tabella Ordine
CREATE TABLE Ordine (
    id_ordine INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    data_creazione DATETIME DEFAULT CURRENT_TIMESTAMP,
    orario_consegna_richiesto DATETIME NOT NULL,
    stato_attuale ENUM('inserito', 'in preparazione', 'pronto', 'in consegna', 'consegnato') DEFAULT 'inserito',
    prezzo_totale DECIMAL(10, 2) NOT NULL,
    tempo_stimato_consegna INT NOT NULL COMMENT 'Tempo in minuti calcolato al momento dell''ordine',
    FOREIGN KEY (id_cliente) REFERENCES Utente(id_utente) ON DELETE CASCADE
);

-- 6. Tabella Dettaglio_Ordine
CREATE TABLE Dettaglio_Ordine (
    id_dettaglio INT AUTO_INCREMENT PRIMARY KEY,
    id_ordine INT NOT NULL,
    id_prodotto INT NOT NULL,
    quantita INT NOT NULL DEFAULT 1,
    FOREIGN KEY (id_ordine) REFERENCES Ordine(id_ordine) ON DELETE CASCADE,
    FOREIGN KEY (id_prodotto) REFERENCES Prodotto(id_prodotto)
);

-- 7. Tabella Dettaglio_Ordine_Caratteristiche
CREATE TABLE Dettaglio_Ordine_Caratteristiche (
    id_dettaglio INT NOT NULL,
    id_caratteristica INT NOT NULL,
    PRIMARY KEY (id_dettaglio, id_caratteristica),
    FOREIGN KEY (id_dettaglio) REFERENCES Dettaglio_Ordine(id_dettaglio) ON DELETE CASCADE,
    FOREIGN KEY (id_caratteristica) REFERENCES Caratteristica(id_caratteristica)
);