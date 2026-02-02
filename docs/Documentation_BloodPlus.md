# 🩸 BloodPlus - Application de Gestion de Banque de Sang

## Documentation Complète

---

# Table des Matières

1. [Présentation du Projet](#1-présentation-du-projet)
2. [Architecture Technique](#2-architecture-technique)
3. [Installation et Configuration](#3-installation-et-configuration)
4. [Structure de la Base de Données](#4-structure-de-la-base-de-données)
5. [Guide Utilisateur](#5-guide-utilisateur)
6. [Modules de l'Application](#6-modules-de-lapplication)
7. [Système d'Authentification](#7-système-dauthentification)
8. [Gestion des Rôles et Permissions](#8-gestion-des-rôles-et-permissions)
9. [Journalisation des Actions](#9-journalisation-des-actions)
10. [Structure du Code Source](#10-structure-du-code-source)
11. [Diagrammes](#11-diagrammes)
12. [Annexes](#12-annexes)

---

# 1. Présentation du Projet

## 1.1 Objectif

**BloodPlus** est une application de bureau développée en Java pour la gestion complète d'une banque de sang. Elle permet de :

- Gérer les donneurs de sang
- Enregistrer et suivre les dons
- Contrôler les stocks sanguins
- Distribuer le sang aux hôpitaux partenaires
- Surveiller les alertes de stock critique
- Tracer toutes les actions des utilisateurs

## 1.2 Technologies Utilisées

| Technologie | Version | Rôle |
|-------------|---------|------|
| Java | 8+ | Langage de programmation |
| Java Swing | - | Interface graphique |
| MySQL | 8.0 | Base de données |
| MySQL Connector/J | 9.0.0 | Pilote JDBC |
| Apache NetBeans | 25 | IDE de développement |

## 1.3 Fonctionnalités Principales

- ✅ Gestion complète des donneurs (CRUD)
- ✅ Enregistrement et validation des dons
- ✅ Suivi des stocks sanguins par groupe
- ✅ Gestion des hôpitaux partenaires
- ✅ Distribution du sang avec traçabilité
- ✅ Alertes automatiques de stock critique
- ✅ Système d'authentification multi-rôles
- ✅ Journal d'audit complet

---

# 2. Architecture Technique

## 2.1 Pattern MVC (Modèle-Vue-Contrôleur)

L'application suit le pattern **MVC** pour séparer les responsabilités :

```
┌─────────────────────────────────────────────────────────────┐
│                        PRÉSENTATION                          │
│                      (Package: ui)                           │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────────┐│
│  │ LoginFrame  │ │ MainFrame   │ │ Panels (Donneur, Don...)││
│  └─────────────┘ └─────────────┘ └─────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      ACCÈS AUX DONNÉES                       │
│                      (Package: dao)                          │
│  ┌────────────────┐ ┌───────────────┐ ┌──────────────────┐  │
│  │ DonneurDAO     │ │ DonDAO        │ │ StockSanguinDAO  │  │
│  │ HopitalDAO     │ │ DistributionDAO│ │ UtilisateurDAO  │  │
│  │ ActionLogDAO   │ │ AlerteDAO     │ │ DatabaseConnection│ │
│  └────────────────┘ └───────────────┘ └──────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                         MODÈLES                              │
│                      (Package: model)                        │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────────────┐ │
│  │ Donneur      │ │ Don          │ │ StockSanguin         │ │
│  │ Hopital      │ │ Distribution │ │ Utilisateur          │ │
│  │ ActionLog    │ │ Alerte       │ │ GroupeSanguin        │ │
│  └──────────────┘ └──────────────┘ └──────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     BASE DE DONNÉES                          │
│                        MySQL                                 │
│                    (bloodplus)                               │
└─────────────────────────────────────────────────────────────┘
```

## 2.2 Pattern DAO (Data Access Object)

Chaque entité a son propre DAO qui encapsule toutes les opérations SQL :

- **Create** : Ajouter un enregistrement
- **Read** : Lire/rechercher des enregistrements
- **Update** : Modifier un enregistrement
- **Delete** : Supprimer un enregistrement

## 2.3 Pattern Singleton

Utilisé pour :
- `DatabaseConnection` : Connexion unique à la base de données
- `Session` : Gestion de l'utilisateur connecté

---

# 3. Installation et Configuration

## 3.1 Prérequis

- Java JDK 8 ou supérieur
- MySQL Server 8.0
- Apache NetBeans (recommandé) ou tout autre IDE Java

## 3.2 Installation de la Base de Données

1. Démarrer MySQL Server
2. Se connecter à MySQL :
   ```bash
   mysql -u root -p
   ```
3. Exécuter le script SQL :
   ```bash
   source /chemin/vers/BloodPlus/database/bloodplus.sql
   ```

## 3.3 Configuration de la Connexion

Modifier le fichier `src/dao/DatabaseConnection.java` si nécessaire :

```java
private static final String URL = "jdbc:mysql://localhost:3306/bloodplus";
private static final String USER = "root";
private static final String PASSWORD = "votre_mot_de_passe";
```

## 3.4 Compilation et Exécution

### Via le Terminal :
```bash
cd /chemin/vers/BloodPlus

# Compilation
javac -d build/classes -cp "lib/*" src/model/*.java src/dao/*.java src/service/*.java src/utils/*.java src/ui/*.java

# Exécution
cd build/classes
java -cp ".:../../lib/*" ui.LoginFrame
```

### Via NetBeans :
1. Ouvrir le projet dans NetBeans
2. Clic droit → "Run"

---

# 4. Structure de la Base de Données

## 4.1 Schéma Relationnel

```
┌─────────────────┐       ┌─────────────────┐
│    donneurs     │       │   utilisateurs  │
├─────────────────┤       ├─────────────────┤
│ id (PK)         │       │ id (PK)         │
│ nom             │       │ username        │
│ prenom          │       │ password        │
│ date_naissance  │       │ nom_complet     │
│ sexe            │       │ role            │
│ groupe_sanguin  │       │ actif           │
│ telephone       │       │ date_creation   │
│ email           │       │ derniere_connexion│
│ adresse         │       └─────────────────┘
│ ville           │
│ eligible        │
│ derniere_visite │
│ notes           │
└────────┬────────┘
         │
         │ 1:N
         ▼
┌─────────────────┐       ┌─────────────────┐
│      dons       │       │    hopitaux     │
├─────────────────┤       ├─────────────────┤
│ id (PK)         │       │ id (PK)         │
│ donneur_id (FK) │───┐   │ nom             │
│ date_don        │   │   │ adresse         │
│ quantite        │   │   │ ville           │
│ statut          │   │   │ telephone       │
│ notes           │   │   │ email           │
└────────┬────────┘   │   │ responsable     │
         │            │   │ actif           │
         │ 1:1        │   └────────┬────────┘
         ▼            │            │
┌─────────────────┐   │            │ 1:N
│ stocks_sanguins │   │            ▼
├─────────────────┤   │   ┌─────────────────┐
│ id (PK)         │   │   │  distributions  │
│ groupe_sanguin  │   │   ├─────────────────┤
│ quantite        │   │   │ id (PK)         │
│ date_prelevement│   │   │ hopital_id (FK) │
│ date_peremption │   │   │ groupe_sanguin  │
│ don_id (FK)     │───┘   │ quantite        │
│ statut          │       │ date_distribution│
└─────────────────┘       │ statut          │
                          │ motif           │
                          └─────────────────┘

┌─────────────────┐
│   actions_log   │
├─────────────────┤
│ id (PK)         │
│ utilisateur_id  │
│ utilisateur_nom │
│ action          │
│ entite          │
│ description     │
│ date_action     │
└─────────────────┘
```

## 4.2 Tables Détaillées

### Table `donneurs`
| Colonne | Type | Description |
|---------|------|-------------|
| id | INT | Identifiant unique (auto-incrémenté) |
| nom | VARCHAR(100) | Nom du donneur |
| prenom | VARCHAR(100) | Prénom du donneur |
| date_naissance | DATE | Date de naissance |
| sexe | ENUM('M','F') | Sexe |
| groupe_sanguin | VARCHAR(5) | Groupe sanguin (A+, B-, O+, etc.) |
| telephone | VARCHAR(20) | Numéro de téléphone |
| email | VARCHAR(100) | Adresse email |
| adresse | TEXT | Adresse complète |
| ville | VARCHAR(100) | Ville |
| eligible | BOOLEAN | Éligibilité au don |
| derniere_visite | DATE | Date du dernier don |
| notes | TEXT | Notes supplémentaires |

### Table `dons`
| Colonne | Type | Description |
|---------|------|-------------|
| id | INT | Identifiant unique |
| donneur_id | INT | Référence au donneur |
| date_don | DATETIME | Date et heure du don |
| quantite | INT | Quantité en ml (défaut: 450) |
| statut | ENUM | EN_ATTENTE, VALIDE, REJETE |
| notes | TEXT | Remarques |

### Table `stocks_sanguins`
| Colonne | Type | Description |
|---------|------|-------------|
| id | INT | Identifiant unique |
| groupe_sanguin | VARCHAR(5) | Groupe sanguin |
| quantite | INT | Quantité en ml |
| date_prelevement | DATE | Date du prélèvement |
| date_peremption | DATE | Date d'expiration (42 jours) |
| don_id | INT | Référence au don d'origine |
| statut | ENUM | DISPONIBLE, UTILISE, PERIME |

### Table `hopitaux`
| Colonne | Type | Description |
|---------|------|-------------|
| id | INT | Identifiant unique |
| nom | VARCHAR(200) | Nom de l'hôpital |
| adresse | TEXT | Adresse |
| ville | VARCHAR(100) | Ville |
| telephone | VARCHAR(20) | Téléphone |
| email | VARCHAR(100) | Email |
| responsable | VARCHAR(100) | Nom du responsable |
| actif | BOOLEAN | Statut actif/inactif |

### Table `distributions`
| Colonne | Type | Description |
|---------|------|-------------|
| id | INT | Identifiant unique |
| hopital_id | INT | Référence à l'hôpital |
| groupe_sanguin | VARCHAR(5) | Groupe sanguin distribué |
| quantite | INT | Quantité en ml |
| date_distribution | DATETIME | Date de distribution |
| statut | ENUM | EN_COURS, LIVREE, ANNULEE |
| motif | TEXT | Motif de la demande |

### Table `utilisateurs`
| Colonne | Type | Description |
|---------|------|-------------|
| id | INT | Identifiant unique |
| username | VARCHAR(50) | Nom d'utilisateur (unique) |
| password | VARCHAR(255) | Mot de passe |
| nom_complet | VARCHAR(100) | Nom complet |
| role | ENUM | ADMIN, OPERATEUR, LECTEUR |
| actif | BOOLEAN | Compte actif |
| date_creation | DATETIME | Date de création |
| derniere_connexion | DATETIME | Dernière connexion |

### Table `actions_log`
| Colonne | Type | Description |
|---------|------|-------------|
| id | INT | Identifiant unique |
| utilisateur_id | INT | ID de l'utilisateur |
| utilisateur_nom | VARCHAR(100) | Nom de l'utilisateur |
| action | VARCHAR(50) | Type d'action |
| entite | VARCHAR(50) | Entité concernée |
| description | TEXT | Description détaillée |
| date_action | DATETIME | Horodatage |

---

# 5. Guide Utilisateur

## 5.1 Connexion

1. Lancer l'application
2. Entrer le nom d'utilisateur
3. Entrer le mot de passe
4. Cliquer sur "Se Connecter"

### Comptes par défaut :

| Utilisateur | Mot de passe | Rôle |
|-------------|--------------|------|
| admin | admin123 | ADMIN |
| operateur | oper123 | OPERATEUR |
| lecteur | lect123 | LECTEUR |

## 5.2 Navigation

L'interface principale comprend :
- **Barre latérale** : Boutons de navigation vers les différents modules
- **Zone centrale** : Affichage du module sélectionné
- **Barre d'état** : Informations sur l'utilisateur connecté

## 5.3 Opérations Communes

### Ajouter un élément :
1. Cliquer sur le bouton "Ajouter"
2. Remplir le formulaire
3. Cliquer sur "Enregistrer"

### Modifier un élément :
1. Sélectionner l'élément dans la liste
2. Cliquer sur "Modifier"
3. Modifier les champs
4. Cliquer sur "Enregistrer"

### Supprimer un élément :
1. Sélectionner l'élément
2. Cliquer sur "Supprimer"
3. Confirmer la suppression

---

# 6. Modules de l'Application

## 6.1 Tableau de Bord

Le tableau de bord affiche :
- Statistiques globales (donneurs, dons, stock total)
- Alertes de stock critique
- Graphique des stocks par groupe sanguin
- Dons récents

## 6.2 Gestion des Donneurs

Fonctionnalités :
- Liste des donneurs avec recherche
- Ajout/modification/suppression de donneurs
- Filtrage par groupe sanguin
- Vérification de l'éligibilité

Champs d'un donneur :
- Nom et prénom
- Date de naissance
- Sexe (M/F)
- Groupe sanguin
- Coordonnées (téléphone, email)
- Adresse et ville
- Statut d'éligibilité
- Notes

## 6.3 Gestion des Dons

Fonctionnalités :
- Enregistrement d'un nouveau don
- Validation ou rejet des dons
- Historique des dons par donneur
- Conversion automatique en stock

Statuts d'un don :
- **EN_ATTENTE** : Don enregistré, en attente de validation
- **VALIDE** : Don validé et ajouté au stock
- **REJETE** : Don rejeté (problème médical, etc.)

Quantité standard : **450 ml** (configurable de 200 à 500 ml)

## 6.4 Gestion des Stocks

Fonctionnalités :
- Vue d'ensemble des stocks par groupe sanguin
- Suivi des dates de péremption
- Alertes automatiques pour stocks critiques
- Gestion des statuts (disponible, utilisé, périmé)

Règles métier :
- Durée de vie du sang : **42 jours**
- Seuil d'alerte critique : configurable par groupe
- Marquage automatique des produits périmés

## 6.5 Gestion des Hôpitaux

Fonctionnalités :
- Répertoire des hôpitaux partenaires
- Informations de contact
- Historique des distributions
- Activation/désactivation

## 6.6 Gestion des Distributions

Fonctionnalités :
- Création de demandes de distribution
- Suivi du statut de livraison
- Déduction automatique du stock
- Historique par hôpital

Statuts de distribution :
- **EN_COURS** : Demande créée
- **LIVREE** : Distribution effectuée
- **ANNULEE** : Distribution annulée

## 6.7 Gestion des Utilisateurs (Admin uniquement)

Fonctionnalités :
- Création de comptes utilisateur
- Attribution des rôles
- Activation/désactivation des comptes
- Réinitialisation des mots de passe

## 6.8 Historique des Actions (Admin uniquement)

Fonctionnalités :
- Visualisation de toutes les actions
- Filtrage par utilisateur, action, entité
- Recherche textuelle
- Export des données

---

# 7. Système d'Authentification

## 7.1 Processus de Connexion

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│ LoginFrame  │────▶│UtilisateurDAO────▶│   MySQL     │
│             │     │.authentifier()    │             │
└─────────────┘     └─────────────┘     └─────────────┘
       │                   │
       │                   ▼
       │            ┌─────────────┐
       │            │  Session    │
       │            │.connecter() │
       │            └─────────────┘
       │                   │
       ▼                   ▼
┌─────────────┐     ┌─────────────┐
│ MainFrame   │◀────│ActionLogDAO │
│             │     │.logConnexion│
└─────────────┘     └─────────────┘
```

## 7.2 Gestion de Session

La classe `Session` (Singleton) maintient :
- L'utilisateur actuellement connecté
- Son rôle et ses permissions
- La date/heure de connexion

Méthodes principales :
- `getInstance()` : Obtenir l'instance unique
- `connecter(Utilisateur)` : Démarrer une session
- `deconnecter()` : Terminer la session
- `getUtilisateurConnecte()` : Obtenir l'utilisateur courant
- `estAdmin()` / `estOperateur()` / `estLecteur()` : Vérifier le rôle

---

# 8. Gestion des Rôles et Permissions

## 8.1 Rôles Disponibles

### ADMIN (Administrateur)
Accès complet à toutes les fonctionnalités :
- ✅ Gestion des donneurs
- ✅ Gestion des dons
- ✅ Gestion des stocks
- ✅ Gestion des hôpitaux
- ✅ Gestion des distributions
- ✅ Gestion des utilisateurs
- ✅ Consultation de l'historique

### OPERATEUR (Opérateur)
Accès aux opérations courantes :
- ✅ Gestion des donneurs
- ✅ Gestion des dons
- ✅ Gestion des stocks
- ✅ Gestion des hôpitaux
- ✅ Gestion des distributions
- ❌ Gestion des utilisateurs
- ❌ Consultation de l'historique

### LECTEUR (Lecture seule)
Accès en consultation uniquement :
- ✅ Consultation des donneurs
- ✅ Consultation des dons
- ✅ Consultation des stocks
- ✅ Consultation des hôpitaux
- ✅ Consultation des distributions
- ❌ Modification de données
- ❌ Gestion des utilisateurs
- ❌ Consultation de l'historique

## 8.2 Matrice des Permissions

| Module | Admin | Opérateur | Lecteur |
|--------|:-----:|:---------:|:-------:|
| Dashboard | ✅ | ✅ | ✅ |
| Donneurs - Voir | ✅ | ✅ | ✅ |
| Donneurs - Ajouter/Modifier/Supprimer | ✅ | ✅ | ❌ |
| Dons - Voir | ✅ | ✅ | ✅ |
| Dons - Ajouter/Valider/Rejeter | ✅ | ✅ | ❌ |
| Stocks - Voir | ✅ | ✅ | ✅ |
| Stocks - Modifier | ✅ | ✅ | ❌ |
| Hôpitaux - Voir | ✅ | ✅ | ✅ |
| Hôpitaux - Ajouter/Modifier/Supprimer | ✅ | ✅ | ❌ |
| Distributions - Voir | ✅ | ✅ | ✅ |
| Distributions - Créer/Livrer/Annuler | ✅ | ✅ | ❌ |
| Utilisateurs | ✅ | ❌ | ❌ |
| Historique | ✅ | ❌ | ❌ |

---

# 9. Journalisation des Actions

## 9.1 Actions Tracées

| Action | Code | Description |
|--------|------|-------------|
| Connexion | CONNEXION | Ouverture de session |
| Déconnexion | DECONNEXION | Fermeture de session |
| Ajout | AJOUTER | Création d'un enregistrement |
| Modification | MODIFIER | Modification d'un enregistrement |
| Suppression | SUPPRIMER | Suppression d'un enregistrement |
| Validation | VALIDER | Validation d'un don |
| Rejet | REJETER | Rejet d'un don |
| Livraison | LIVRER | Livraison d'une distribution |
| Annulation | ANNULER | Annulation d'une opération |

## 9.2 Entités Tracées

- DONNEUR
- DON
- STOCK
- HOPITAL
- DISTRIBUTION
- UTILISATEUR
- SYSTEME

## 9.3 Format d'un Log

```
┌────────────────────────────────────────────────────────────┐
│ ID: 125                                                     │
│ Date: 2026-01-20 14:35:22                                  │
│ Utilisateur: admin (ID: 1)                                 │
│ Action: AJOUTER                                            │
│ Entité: DONNEUR                                            │
│ Description: Ajout du donneur: Jean Dupont (O+)            │
└────────────────────────────────────────────────────────────┘
```

---

# 10. Structure du Code Source

## 10.1 Arborescence du Projet

```
BloodPlus/
├── build/
│   └── classes/           # Classes compilées
│       ├── dao/
│       ├── model/
│       ├── service/
│       ├── ui/
│       └── utils/
├── database/
│   └── bloodplus.sql      # Script de création BDD
├── docs/
│   └── Documentation_BloodPlus.md
├── lib/
│   └── mysql-connector-j-9.0.0.jar
├── nbproject/             # Configuration NetBeans
├── src/
│   ├── dao/               # Data Access Objects
│   │   ├── ActionLogDAO.java
│   │   ├── AlerteDAO.java
│   │   ├── DatabaseConnection.java
│   │   ├── DistributionDAO.java
│   │   ├── DonDAO.java
│   │   ├── DonneurDAO.java
│   │   ├── HopitalDAO.java
│   │   ├── StockSanguinDAO.java
│   │   └── UtilisateurDAO.java
│   ├── model/             # Entités métier
│   │   ├── ActionLog.java
│   │   ├── Alerte.java
│   │   ├── Distribution.java
│   │   ├── Don.java
│   │   ├── Donneur.java
│   │   ├── GroupeSanguin.java
│   │   ├── Hopital.java
│   │   ├── StockSanguin.java
│   │   └── Utilisateur.java
│   ├── service/           # Services métier
│   │   └── Session.java
│   ├── ui/                # Interfaces graphiques
│   │   ├── DashboardPanel.java
│   │   ├── DistributionPanel.java
│   │   ├── DonPanel.java
│   │   ├── DonneurPanel.java
│   │   ├── HistoriquePanel.java
│   │   ├── HopitalPanel.java
│   │   ├── LoginFrame.java
│   │   ├── MainFrame.java
│   │   ├── StockPanel.java
│   │   └── UtilisateurPanel.java
│   └── utils/             # Utilitaires
│       ├── DateUtils.java
│       └── ValidationUtils.java
├── build.xml
└── manifest.mf
```

## 10.2 Description des Packages

### Package `model`
Contient les classes POJO (Plain Old Java Objects) représentant les entités métier. Chaque classe correspond à une table de la base de données.

### Package `dao`
Contient les classes d'accès aux données. Chaque DAO encapsule les opérations CRUD pour une entité.

### Package `service`
Contient les services métier, notamment la gestion de session.

### Package `ui`
Contient les interfaces graphiques Swing (JFrame, JPanel).

### Package `utils`
Contient les classes utilitaires pour la validation et le formatage.

---

# 11. Diagrammes

## 11.1 Diagramme de Cas d'Utilisation

```
                    ┌─────────────────────────────────────┐
                    │         BloodPlus System            │
                    └─────────────────────────────────────┘
                                      │
        ┌─────────────────────────────┼─────────────────────────────┐
        │                             │                             │
        ▼                             ▼                             ▼
   ┌─────────┐                   ┌─────────┐                   ┌─────────┐
   │  Admin  │                   │Opérateur│                   │ Lecteur │
   └────┬────┘                   └────┬────┘                   └────┬────┘
        │                             │                             │
        │  ┌──────────────────────────┼─────────────────────────────┤
        │  │                          │                             │
        │  │   ┌──────────────────────┼─────────────────────────────┤
        │  │   │                      │                             │
        ▼  ▼   ▼                      ▼                             ▼
   ┌───────────────┐            ┌───────────────┐            ┌───────────────┐
   │ Gérer         │            │ Gérer         │            │ Consulter     │
   │ Utilisateurs  │            │ Donneurs      │◀───────────│ Données       │
   └───────────────┘            │ Dons          │            └───────────────┘
                                │ Stocks        │
   ┌───────────────┐            │ Distributions │
   │ Voir          │            └───────────────┘
   │ Historique    │
   └───────────────┘
```

## 11.2 Diagramme de Séquence - Enregistrement d'un Don

```
┌─────────┐      ┌──────────┐      ┌────────┐      ┌────────────┐      ┌─────────┐
│Opérateur│      │ DonPanel │      │ DonDAO │      │StockSanguinDAO    │ MySQL   │
└────┬────┘      └────┬─────┘      └───┬────┘      └──────┬─────┘      └────┬────┘
     │                │                │                  │                 │
     │ Clic "Ajouter" │                │                  │                 │
     │───────────────▶│                │                  │                 │
     │                │                │                  │                 │
     │                │ ajouter(don)   │                  │                 │
     │                │───────────────▶│                  │                 │
     │                │                │                  │                 │
     │                │                │ INSERT INTO dons │                 │
     │                │                │─────────────────────────────────────▶
     │                │                │                  │                 │
     │                │                │                  │     OK          │
     │                │                │◀─────────────────────────────────────
     │                │                │                  │                 │
     │                │ Si statut=VALIDE                  │                 │
     │                │                │ ajouter(stock)   │                 │
     │                │                │─────────────────▶│                 │
     │                │                │                  │ INSERT INTO stocks
     │                │                │                  │────────────────▶│
     │                │                │                  │      OK         │
     │                │                │                  │◀────────────────│
     │                │                │                  │                 │
     │                │   Succès       │                  │                 │
     │                │◀───────────────│                  │                 │
     │                │                │                  │                 │
     │  Message OK    │                │                  │                 │
     │◀───────────────│                │                  │                 │
```

---

# 12. Annexes

## 12.1 Groupes Sanguins

| Groupe | Rhésus | Compatible avec |
|--------|--------|-----------------|
| O- | Négatif | Donneur universel |
| O+ | Positif | O+, A+, B+, AB+ |
| A- | Négatif | A-, A+, AB-, AB+ |
| A+ | Positif | A+, AB+ |
| B- | Négatif | B-, B+, AB-, AB+ |
| B+ | Positif | B+, AB+ |
| AB- | Négatif | AB-, AB+ |
| AB+ | Positif | Receveur universel |

## 12.2 Règles Métier

### Don de Sang
- Quantité standard : 450 ml
- Intervalle minimum entre deux dons : 8 semaines (hommes), 12 semaines (femmes)
- Âge : 18-70 ans
- Poids minimum : 50 kg

### Conservation du Sang
- Durée de conservation : 42 jours à 4°C
- Alerte : 7 jours avant péremption
- Critique : 3 jours avant péremption

### Seuils d'Alerte Stock
- Normal : > 10 unités
- Bas : 5-10 unités
- Critique : < 5 unités

## 12.3 Fonctionnalités de Distribution Automatisée

### Diminution Automatique du Stock
Lors d'une distribution de sang, le système :
1. Enregistre la distribution dans la table `distributions`
2. **Diminue automatiquement le stock** dans la table `stocks_sanguins`
3. Utilise la méthode **FIFO** (First In, First Out) : les poches les plus anciennes sont utilisées en premier
4. Met à jour le statut des poches épuisées en "UTILISÉ"

### Rafraîchissement du Dashboard
Après chaque action de distribution :
- Le dashboard se rafraîchit automatiquement
- Les statistiques de stock sont mises à jour en temps réel
- Les alertes de stock critique sont recalculées

### Code de la méthode `diminuerStock()`
```java
public boolean diminuerStock(String groupeSanguin, int quantite) {
    // Sélectionne les poches disponibles, triées par date de péremption
    // Utilise les plus anciennes d'abord (FIFO)
    // Marque comme "UTILISÉ" les poches épuisées
    // Diminue partiellement les poches si nécessaire
}
```

## 12.4 Codes d'Erreur

| Code | Message | Solution |
|------|---------|----------|
| DB001 | Connexion BDD échouée | Vérifier MySQL et les credentials |
| AUTH01 | Authentification échouée | Vérifier username/password |
| VAL01 | Données invalides | Vérifier les champs requis |
| STOCK01 | Stock insuffisant | Vérifier disponibilité |

## 12.4 Contact et Support

- **Développeur** : [Votre Nom]
- **Email** : [votre.email@example.com]
- **Version** : 1.0.0
- **Date** : Janvier 2026

---

*Document généré le 20 janvier 2026*
*BloodPlus - Application de Gestion de Banque de Sang*
