DROP DATABASE IF EXISTS webdelivery;
CREATE DATABASE webdelivery;
USE webdelivery;

CREATE TABLE Utente (
    id_utente INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    ruolo ENUM('cliente', 'personale', 'proprietario') NOT NULL,
    nome_completo VARCHAR(100) NOT NULL,
    telefono VARCHAR(50),
    indirizzo TEXT
);

CREATE TABLE Prodotto (
    id_prodotto INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descrizione TEXT,
    prezzo_base DECIMAL(10, 2) NOT NULL,
    tempo_preparazione INT NOT NULL COMMENT 'Tempo in minuti',
    ingredienti TEXT,
    ricetta TEXT,
    categoria VARCHAR(50) DEFAULT 'tutti',
    immagine_url TEXT,
    badge VARCHAR(50)
);

CREATE TABLE Gruppo_Esclusione (
    id_gruppo INT AUTO_INCREMENT PRIMARY KEY,
    id_prodotto INT NOT NULL,
    nome_gruppo VARCHAR(100) NOT NULL,
    FOREIGN KEY (id_prodotto) REFERENCES Prodotto(id_prodotto) ON DELETE CASCADE
);

CREATE TABLE Caratteristica (
    id_caratteristica INT AUTO_INCREMENT PRIMARY KEY,
    id_prodotto INT NOT NULL,
    id_gruppo INT DEFAULT NULL,
    nome VARCHAR(150) NOT NULL,
    descrizione TEXT,
    differenza_prezzo DECIMAL(10, 2) DEFAULT 0.00,
    is_default BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (id_prodotto) REFERENCES Prodotto(id_prodotto) ON DELETE CASCADE,
    FOREIGN KEY (id_gruppo) REFERENCES Gruppo_Esclusione(id_gruppo) ON DELETE SET NULL
);

CREATE TABLE Ordine (
    id_ordine INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    data_creazione DATETIME DEFAULT CURRENT_TIMESTAMP,
    orario_consegna_richiesto DATETIME NOT NULL,
    stato_attuale ENUM('inserito', 'in preparazione', 'pronto', 'in consegna', 'consegnato') DEFAULT 'inserito',
    prezzo_totale DECIMAL(10, 2) NOT NULL,
    tempo_stimato_consegna INT NOT NULL,
    FOREIGN KEY (id_cliente) REFERENCES Utente(id_utente) ON DELETE CASCADE
);

CREATE TABLE Dettaglio_Ordine (
    id_dettaglio INT AUTO_INCREMENT PRIMARY KEY,
    id_ordine INT NOT NULL,
    id_prodotto INT NOT NULL,
    quantita INT NOT NULL DEFAULT 1,
    FOREIGN KEY (id_ordine) REFERENCES Ordine(id_ordine) ON DELETE CASCADE,
    FOREIGN KEY (id_prodotto) REFERENCES Prodotto(id_prodotto)
);

CREATE TABLE Dettaglio_Ordine_Caratteristiche (
    id_dettaglio INT NOT NULL,
    id_caratteristica INT NOT NULL,
    PRIMARY KEY (id_dettaglio, id_caratteristica),
    FOREIGN KEY (id_dettaglio) REFERENCES Dettaglio_Ordine(id_dettaglio) ON DELETE CASCADE,
    FOREIGN KEY (id_caratteristica) REFERENCES Caratteristica(id_caratteristica)
);

CREATE TABLE Storico_Stati_Ordine (
    id_storico INT AUTO_INCREMENT PRIMARY KEY,
    id_ordine INT NOT NULL,
    id_personale INT NOT NULL,
    stato ENUM('inserito', 'in preparazione', 'pronto', 'in consegna', 'consegnato') NOT NULL,
    data_ora_modifica DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_ordine) REFERENCES Ordine(id_ordine) ON DELETE CASCADE,
    FOREIGN KEY (id_personale) REFERENCES Utente(id_utente)
);
