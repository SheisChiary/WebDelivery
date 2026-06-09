/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  chiar
 * Created: 9 giu 2026
 */
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS Dettaglio_Ordine_Caratteristiche;
DROP TABLE IF EXISTS Dettaglio_Ordine;
DROP TABLE IF EXISTS Storico_Stati_Ordine;
DROP TABLE IF EXISTS Ordine;
DROP TABLE IF EXISTS Caratteristica;
DROP TABLE IF EXISTS Gruppo_Esclusione;
DROP TABLE IF EXISTS Prodotto;
DROP TABLE IF EXISTS Utente;


SET FOREIGN_KEY_CHECKS = 1;


-- 1. Tabella Utente
CREATE TABLE Utente (
    id_utente INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    ruolo ENUM('cliente', 'personale', 'proprietario') NOT NULL,
    nome_completo VARCHAR(255) NOT NULL,
    telefono VARCHAR(50),
    indirizzo TEXT
);

-- 2. Tabella Prodotto
CREATE TABLE Prodotto (
    id_prodotto INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
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

-- 6. Tabella Storico_Stati_Ordine
CREATE TABLE Storico_Stati_Ordine (
    id_storico INT AUTO_INCREMENT PRIMARY KEY,
    id_ordine INT NOT NULL,
    id_personale INT NOT NULL COMMENT 'Il membro del personale che ha cambiato lo stato',
    stato ENUM('inserito', 'in preparazione', 'pronto', 'in consegna', 'consegnato') NOT NULL,
    data_ora_modifica DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_ordine) REFERENCES Ordine(id_ordine) ON DELETE CASCADE,
    FOREIGN KEY (id_personale) REFERENCES Utente(id_utente)
);

-- 7. Tabella Dettaglio_Ordine
CREATE TABLE Dettaglio_Ordine (
    id_dettaglio INT AUTO_INCREMENT PRIMARY KEY,
    id_ordine INT NOT NULL,
    id_prodotto INT NOT NULL,
    quantita INT NOT NULL DEFAULT 1,
    FOREIGN KEY (id_ordine) REFERENCES Ordine(id_ordine) ON DELETE CASCADE,
    FOREIGN KEY (id_prodotto) REFERENCES Prodotto(id_prodotto)
);

-- 8. Tabella Dettaglio_Ordine_Caratteristiche
CREATE TABLE Dettaglio_Ordine_Caratteristiche (
    id_dettaglio INT NOT NULL,
    id_caratteristica INT NOT NULL,
    PRIMARY KEY (id_dettaglio, id_caratteristica),
    FOREIGN KEY (id_dettaglio) REFERENCES Dettaglio_Ordine(id_dettaglio) ON DELETE CASCADE,
    FOREIGN KEY (id_caratteristica) REFERENCES Caratteristica(id_caratteristica)
);

-- INSERIAMO UN PROPRIETARIO E UN CLIENTE DI PROVA
INSERT INTO Utente (email, password, ruolo, nome_completo, telefono, indirizzo) VALUES 
('admin@delivery.it', 'admin123', 'proprietario', 'Chiara e Bassma Admin', '08620000', 'Via Università 1'),
('cliente@test.it', 'cliente123', 'cliente', 'Mario Rossi', '3331234567', 'Via Roma 10, L''Aquila');

-- INSERIAMO IL PRODOTTO CAFFÈ (ID 1)
INSERT INTO Prodotto (nome, descrizione, prezzo_base, tempo_preparazione, ingredienti, ricetta) 
VALUES ('Caffè', 'Un ottimo caffè espresso.', 1.00, 2, 'Caffè in grani, Acqua', 'Macinare ed estrarre.');

-- CREIAMO IL GRUPPO DI ESCLUSIONE PER LO ZUCCHERO (ID 1)
INSERT INTO Gruppo_Esclusione (id_prodotto, nome_gruppo) 
VALUES (1, 'Zucchero');

-- CARICHIAMO LE CARATTERISTICHE DELLO ZUCCHERO (Legate al gruppo 1)
INSERT INTO Caratteristica (id_prodotto, id_gruppo, nome, descrizione, differenza_prezzo, is_default) VALUES 
(1, 1, 'Senza Zucchero', 'Niente zucchero', -0.05, FALSE),
(1, 1, 'Zuccherato', 'Zucchero normale', 0.00, TRUE),
(1, 1, 'Molto Zuccherato', 'Zucchero extra', 0.00, FALSE);

-- CARICHIAMO UNA CARATTERISTICA LIBERA (id_gruppo è NULL, tipo "Con Panna")
INSERT INTO Caratteristica (id_prodotto, id_gruppo, nome, descrizione, differenza_prezzo, is_default) 
VALUES (1, NULL, 'Con Panna', 'Aggiunta di panna montata', 0.50, FALSE);
