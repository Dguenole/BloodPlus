-- ============================================================
-- 🩸 BLOODPLUS - Script de création de la base de données
-- ============================================================
-- Ce script crée toutes les tables nécessaires pour l'application
-- 
-- 💡 COMMENT UTILISER CE SCRIPT :
--    1. Ouvre MySQL (phpMyAdmin, MySQL Workbench, ou terminal)
--    2. Copie-colle ce script
--    3. Exécute-le
-- ============================================================

-- Supprimer la base si elle existe (pour repartir à zéro)
DROP DATABASE IF EXISTS bloodplus;

-- Créer la base de données
CREATE DATABASE bloodplus
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Utiliser cette base
USE bloodplus;

-- ============================================================
-- TABLE : donneurs
-- Stocke les informations des personnes qui donnent leur sang
-- ============================================================
CREATE TABLE donneurs (
    id INT PRIMARY KEY AUTO_INCREMENT,      -- Identifiant unique (auto-généré)
    nom VARCHAR(100) NOT NULL,              -- Nom de famille (obligatoire)
    prenom VARCHAR(100) NOT NULL,           -- Prénom (obligatoire)
    date_naissance DATE,                    -- Date de naissance
    sexe ENUM('M', 'F') DEFAULT 'M',        -- Sexe : M ou F
    groupe_sanguin VARCHAR(5) NOT NULL,     -- Ex: A+, O-, AB+
    telephone VARCHAR(20),                  -- Numéro de téléphone
    email VARCHAR(150),                     -- Adresse email
    adresse TEXT,                           -- Adresse complète
    date_inscription DATETIME DEFAULT CURRENT_TIMESTAMP,  -- Date d'inscription
    apte BOOLEAN DEFAULT TRUE,              -- Apte à donner ?
    
    -- Index pour recherches rapides
    INDEX idx_groupe (groupe_sanguin),
    INDEX idx_nom (nom, prenom)
);

-- ============================================================
-- TABLE : dons
-- Enregistre chaque don de sang effectué
-- ============================================================
CREATE TABLE dons (
    id INT PRIMARY KEY AUTO_INCREMENT,
    donneur_id INT NOT NULL,                -- Lien vers le donneur
    date_don DATETIME NOT NULL,             -- Date et heure du don
    quantite INT DEFAULT 450,               -- Quantité en ml (standard = 450ml)
    statut ENUM('EN_ATTENTE', 'VALIDE', 'REJETE') DEFAULT 'EN_ATTENTE',
    notes TEXT,                             -- Remarques
    
    -- Clé étrangère : lie le don au donneur
    FOREIGN KEY (donneur_id) REFERENCES donneurs(id) ON DELETE CASCADE,
    
    INDEX idx_date (date_don),
    INDEX idx_statut (statut)
);

-- ============================================================
-- TABLE : stocks_sanguins
-- Gère le stock de sang disponible
-- ============================================================
CREATE TABLE stocks_sanguins (
    id INT PRIMARY KEY AUTO_INCREMENT,
    groupe_sanguin VARCHAR(5) NOT NULL,     -- Groupe sanguin
    quantite INT NOT NULL,                  -- Quantité en ml
    date_prelevement DATE NOT NULL,         -- Date du prélèvement
    date_peremption DATE NOT NULL,          -- Date limite (42 jours après)
    don_id INT,                             -- Lien vers le don d'origine
    statut ENUM('DISPONIBLE', 'RESERVE', 'UTILISE', 'PERIME') DEFAULT 'DISPONIBLE',
    
    FOREIGN KEY (don_id) REFERENCES dons(id) ON DELETE SET NULL,
    
    INDEX idx_groupe (groupe_sanguin),
    INDEX idx_peremption (date_peremption),
    INDEX idx_statut (statut)
);

-- ============================================================
-- TABLE : hopitaux
-- Liste des hôpitaux partenaires
-- ============================================================
CREATE TABLE hopitaux (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(200) NOT NULL,              -- Nom de l'hôpital
    adresse TEXT,                           -- Adresse
    ville VARCHAR(100),                     -- Ville
    telephone VARCHAR(20),                  -- Téléphone
    email VARCHAR(150),                     -- Email
    responsable VARCHAR(150),               -- Nom du responsable
    actif BOOLEAN DEFAULT TRUE,             -- Hôpital actif ?
    
    INDEX idx_ville (ville)
);

-- ============================================================
-- TABLE : distributions
-- Historique des distributions de sang aux hôpitaux
-- ============================================================
CREATE TABLE distributions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    hopital_id INT NOT NULL,                -- Hôpital destinataire
    groupe_sanguin VARCHAR(5) NOT NULL,     -- Groupe distribué
    quantite INT NOT NULL,                  -- Quantité en ml
    date_distribution DATETIME DEFAULT CURRENT_TIMESTAMP,
    statut ENUM('EN_COURS', 'LIVREE', 'ANNULEE') DEFAULT 'EN_COURS',
    motif VARCHAR(255),                     -- Raison de la demande
    
    FOREIGN KEY (hopital_id) REFERENCES hopitaux(id) ON DELETE CASCADE,
    
    INDEX idx_date (date_distribution),
    INDEX idx_hopital (hopital_id)
);

-- ============================================================
-- TABLE : alertes
-- Système de notifications/alertes
-- ============================================================
CREATE TABLE alertes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    type ENUM('STOCK_BAS', 'PEREMPTION_PROCHE', 'BESOIN_URGENT', 'RUPTURE_STOCK') NOT NULL,
    message TEXT NOT NULL,                  -- Message de l'alerte
    groupe_sanguin VARCHAR(5),              -- Groupe concerné
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    lue BOOLEAN DEFAULT FALSE,              -- Alerte lue ?
    priorite ENUM('BASSE', 'MOYENNE', 'HAUTE', 'CRITIQUE') DEFAULT 'MOYENNE',
    
    INDEX idx_lue (lue),
    INDEX idx_priorite (priorite)
);

-- ============================================================
-- TABLE : utilisateurs (optionnel - pour connexion)
-- Gestion des utilisateurs de l'application
-- ============================================================
CREATE TABLE utilisateurs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,   -- Nom d'utilisateur
    password VARCHAR(255) NOT NULL,         -- Mot de passe (à hasher!)
    nom_complet VARCHAR(150),               -- Nom complet
    role ENUM('ADMIN', 'OPERATEUR', 'LECTEUR') DEFAULT 'OPERATEUR',
    actif BOOLEAN DEFAULT TRUE,
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    derniere_connexion DATETIME
);

-- ============================================================
-- DONNÉES DE TEST (pour commencer)
-- ============================================================

-- Quelques donneurs de test
INSERT INTO donneurs (nom, prenom, date_naissance, sexe, groupe_sanguin, telephone, email) VALUES
('Dupont', 'Jean', '1985-03-15', 'M', 'A+', '0612345678', 'jean.dupont@email.com'),
('Martin', 'Marie', '1990-07-22', 'F', 'O-', '0698765432', 'marie.martin@email.com'),
('Bernard', 'Pierre', '1978-11-08', 'M', 'B+', '0654321987', 'pierre.bernard@email.com'),
('Petit', 'Sophie', '1995-01-30', 'F', 'AB+', '0687654321', 'sophie.petit@email.com'),
('Robert', 'Lucas', '1982-09-12', 'M', 'O+', '0623456789', 'lucas.robert@email.com');

-- Quelques hôpitaux
INSERT INTO hopitaux (nom, adresse, ville, telephone, responsable) VALUES
('CHU Central', '1 Avenue de la Santé', 'Paris', '0145678901', 'Dr. Martin'),
('Hôpital Saint-Louis', '25 Rue de la Médecine', 'Lyon', '0478901234', 'Dr. Dubois'),
('Clinique du Parc', '10 Boulevard des Soins', 'Marseille', '0491234567', 'Dr. Leroy');

-- Quelques dons de test
INSERT INTO dons (donneur_id, date_don, quantite, statut) VALUES
(1, '2026-01-15 10:30:00', 450, 'VALIDE'),
(2, '2026-01-16 14:00:00', 450, 'VALIDE'),
(3, '2026-01-17 09:15:00', 450, 'VALIDE'),
(4, '2026-01-18 11:45:00', 450, 'EN_ATTENTE'),
(5, '2026-01-19 16:00:00', 450, 'VALIDE');

-- Stock initial
INSERT INTO stocks_sanguins (groupe_sanguin, quantite, date_prelevement, date_peremption, don_id, statut) VALUES
('A+', 450, '2026-01-15', '2026-02-26', 1, 'DISPONIBLE'),
('O-', 450, '2026-01-16', '2026-02-27', 2, 'DISPONIBLE'),
('B+', 450, '2026-01-17', '2026-02-28', 3, 'DISPONIBLE'),
('O+', 450, '2026-01-19', '2026-03-02', 5, 'DISPONIBLE');

-- ============================================================
-- UTILISATEURS DE TEST (3 rôles différents)
-- ============================================================
-- 🔴 ADMIN : accès total + gestion utilisateurs
-- 🟡 OPERATEUR : gestion donneurs, dons, stock, distributions
-- 🟢 LECTEUR : consultation seulement (pas de modification)
-- ============================================================

INSERT INTO utilisateurs (username, password, nom_complet, role) VALUES
('admin', 'admin123', 'Administrateur Système', 'ADMIN'),
('operateur', 'oper123', 'Jean Opérateur', 'OPERATEUR'),
('lecteur', 'lect123', 'Marie Lectrice', 'LECTEUR');

-- ============================================================
-- TABLE : actions_log
-- Journal des actions effectuées par les utilisateurs (AUDIT)
-- ============================================================
CREATE TABLE actions_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    utilisateur_id INT NOT NULL,            -- Qui a fait l'action
    utilisateur_nom VARCHAR(150),           -- Nom de l'utilisateur
    action VARCHAR(50) NOT NULL,            -- Type: AJOUTER, MODIFIER, SUPPRIMER, etc.
    entite VARCHAR(50) NOT NULL,            -- Sur quoi: DONNEUR, DON, STOCK, etc.
    description TEXT,                       -- Description détaillée
    date_action DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_utilisateur (utilisateur_id),
    INDEX idx_date (date_action),
    INDEX idx_action (action),
    INDEX idx_entite (entite)
);

-- Quelques actions de test
INSERT INTO actions_log (utilisateur_id, utilisateur_nom, action, entite, description) VALUES
(1, 'Administrateur Système', 'CONNEXION', 'SYSTEME', 'Connexion à l''application'),
(1, 'Administrateur Système', 'AJOUTER', 'DONNEUR', 'Ajout du donneur: Jean Dupont'),
(1, 'Administrateur Système', 'AJOUTER', 'DON', 'Nouveau don enregistré pour Jean Dupont'),
(2, 'Jean Opérateur', 'CONNEXION', 'SYSTEME', 'Connexion à l''application'),
(2, 'Jean Opérateur', 'MODIFIER', 'DONNEUR', 'Modification du donneur: Marie Martin'),
(2, 'Jean Opérateur', 'VALIDER', 'DON', 'Validation du don #1');

-- ============================================================
-- COMPTES DE CONNEXION DISPONIBLES :
-- ============================================================
-- | Username   | Mot de passe | Rôle      | Accès                    |
-- |------------|--------------|-----------|--------------------------|
-- | admin      | admin123     | ADMIN     | Tout + gestion users     |
-- | operateur  | oper123      | OPERATEUR | Gestion complète         |
-- | lecteur    | lect123      | LECTEUR   | Consultation seulement   |
-- ============================================================

-- ============================================================
-- VÉRIFICATION
-- ============================================================
SELECT '✅ Base de données BloodPlus créée avec succès !' AS Message;
SELECT CONCAT('📊 ', COUNT(*), ' donneurs créés') AS Info FROM donneurs
UNION ALL
SELECT CONCAT('📊 ', COUNT(*), ' hôpitaux créés') FROM hopitaux
UNION ALL
SELECT CONCAT('📊 ', COUNT(*), ' dons enregistrés') FROM dons
UNION ALL
SELECT CONCAT('📊 ', COUNT(*), ' stocks créés') FROM stocks_sanguins;
