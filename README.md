# 🩸 BloodPlus

**Application de Gestion de Banque de Sang**

BloodPlus est une application de bureau développée en Java pour la gestion complète d'une banque de sang. Elle permet de gérer les donneurs, suivre les dons, contrôler les stocks sanguins et distribuer le sang aux hôpitaux partenaires.

![Java](https://img.shields.io/badge/Java-8%2B-orange)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![License](https://img.shields.io/badge/License-MIT-green)

---

## ✨ Fonctionnalités

- 👤 **Gestion des donneurs** - CRUD complet avec informations médicales
- 🩸 **Suivi des dons** - Enregistrement et validation des dons de sang
- 📦 **Gestion des stocks** - Contrôle par groupe sanguin (A+, A-, B+, B-, AB+, AB-, O+, O-)
- 🏥 **Hôpitaux partenaires** - Gestion des établissements de santé
- 🚚 **Distribution** - Traçabilité complète des distributions
- ⚠️ **Alertes automatiques** - Notification des stocks critiques
- 🔐 **Multi-utilisateurs** - Système de rôles (Admin, Opérateur, Lecteur)
- 📋 **Journal d'audit** - Historique complet des actions

---

## 🛠️ Technologies

| Technologie | Version | Rôle |
|-------------|---------|------|
| Java | 8+ | Langage principal |
| Java Swing | - | Interface graphique |
| MySQL | 8.0 | Base de données |
| MySQL Connector/J | 9.0.0 | Pilote JDBC |
| Apache Ant | - | Build |

---

## 📁 Structure du Projet

```
BloodPlus/
├── src/
│   ├── bloodplus/      # Point d'entrée
│   ├── model/          # Classes métier
│   ├── dao/            # Accès aux données
│   ├── ui/             # Interface graphique
│   ├── service/        # Session utilisateur
│   └── utils/          # Utilitaires
├── database/
│   └── bloodplus.sql   # Script de création BDD
├── docs/               # Documentation
├── lib/                # Dépendances (JDBC)
└── build.xml           # Script Ant
```

---

## 🚀 Installation

### Prérequis

- Java JDK 8 ou supérieur
- MySQL 8.0
- Apache NetBeans (recommandé) ou tout IDE Java

### Étapes

1. **Cloner le repository**
   ```bash
   git clone https://github.com/VOTRE_USERNAME/BloodPlus.git
   cd BloodPlus
   ```

2. **Créer la base de données**
   ```bash
   mysql -u root -p < database/bloodplus.sql
   ```

3. **Configurer la connexion**
   
   Modifier les paramètres dans `src/dao/DatabaseConnection.java` :
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/bloodplus";
   private static final String USER = "votre_utilisateur";
   private static final String PASSWORD = "votre_mot_de_passe";
   ```

4. **Compiler et exécuter**
   ```bash
   ant run
   ```
   Ou ouvrir le projet dans NetBeans et cliquer sur ▶️ Run.

---

## 👥 Rôles Utilisateurs

| Rôle | Permissions |
|------|-------------|
| **ADMIN** | Accès complet + gestion des utilisateurs |
| **OPERATEUR** | Gestion donneurs, dons, stocks, distributions |
| **LECTEUR** | Consultation uniquement |

### Compte par défaut
- **Utilisateur** : `admin`
- **Mot de passe** : `admin123`

> ⚠️ Changez ce mot de passe après la première connexion !

---

## 📸 Aperçu

L'application comprend :
- 🔐 Écran de connexion sécurisé
- 📊 Tableau de bord avec statistiques
- 📋 Interfaces de gestion pour chaque module
- 🔔 Système d'alertes en temps réel

---

## 📖 Documentation

Une documentation complète est disponible dans le dossier [`docs/`](docs/) :
- [Documentation technique](docs/Documentation_BloodPlus.md)
- [Historique de développement](docs/Historique_Developpement.md)

---

## 🤝 Contribution

Les contributions sont les bienvenues ! 

1. Fork le projet
2. Créer une branche (`git checkout -b feature/NouvelleFeature`)
3. Commit (`git commit -m 'feat: ajout nouvelle feature'`)
4. Push (`git push origin feature/NouvelleFeature`)
5. Ouvrir une Pull Request

---

## 📝 License

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

---

## 👨‍💻 Auteur

**dteach** - Projet scolaire 2026

---

<p align="center">
  Fait avec ❤️ pour sauver des vies 🩸
</p>
