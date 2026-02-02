# 📜 Historique de Développement - BloodPlus

## Session du 20 Janvier 2026

---

## 🎯 Résumé du Projet

**BloodPlus** est une application de gestion de banque de sang développée en Java avec Swing pour l'interface graphique et MySQL pour la base de données.

---

## 📋 Fonctionnalités Développées

### 1. Structure de Base
- ✅ Architecture MVC (Model-View-Controller)
- ✅ Pattern DAO (Data Access Object)
- ✅ Connexion MySQL avec Singleton
- ✅ Configuration NetBeans

### 2. Modèles (Entités)
- `Donneur.java` - Gestion des donneurs
- `Don.java` - Gestion des dons de sang
- `StockSanguin.java` - Stock de sang
- `Hopital.java` - Hôpitaux partenaires
- `Distribution.java` - Distributions aux hôpitaux
- `Utilisateur.java` - Comptes utilisateurs
- `ActionLog.java` - Journal d'audit
- `Alerte.java` - Alertes de stock
- `GroupeSanguin.java` - Constantes groupes sanguins

### 3. DAOs (Accès aux données)
- `DatabaseConnection.java` - Connexion singleton
- `DonneurDAO.java` - CRUD donneurs
- `DonDAO.java` - CRUD dons + validation
- `StockSanguinDAO.java` - Gestion stock
- `HopitalDAO.java` - CRUD hôpitaux
- `DistributionDAO.java` - CRUD distributions
- `UtilisateurDAO.java` - CRUD utilisateurs + authentification
- `ActionLogDAO.java` - Journalisation
- `AlerteDAO.java` - Gestion alertes

### 4. Interfaces Graphiques
- `LoginFrame.java` - Écran de connexion
- `MainFrame.java` - Fenêtre principale avec navigation
- `DashboardPanel.java` - Tableau de bord
- `DonneurPanel.java` - Gestion donneurs
- `DonPanel.java` - Gestion dons
- `StockPanel.java` - Gestion stocks
- `HopitalPanel.java` - Gestion hôpitaux
- `DistributionPanel.java` - Gestion distributions
- `UtilisateurPanel.java` - Gestion utilisateurs (Admin)
- `HistoriquePanel.java` - Journal d'audit (Admin)

### 5. Services
- `Session.java` - Gestion de la session utilisateur (Singleton)

### 6. Utilitaires
- `DateUtils.java` - Formatage des dates
- `ValidationUtils.java` - Validation des données

---

## 🔐 Système d'Authentification

### Rôles Implémentés
| Rôle | Permissions |
|------|-------------|
| **ADMIN** | Accès complet + Gestion utilisateurs + Historique |
| **OPERATEUR** | Opérations courantes (CRUD sur données) |
| **LECTEUR** | Consultation uniquement |

### Comptes par défaut
| Username | Password | Rôle |
|----------|----------|------|
| admin | admin123 | ADMIN |
| operateur | oper123 | OPERATEUR |
| lecteur | lect123 | LECTEUR |

---

## 📊 Journalisation des Actions

### Types d'actions tracées
- `CONNEXION` / `DECONNEXION`
- `AJOUTER` / `MODIFIER` / `SUPPRIMER`
- `VALIDER` / `REJETER`
- `LIVRER` / `ANNULER` / `UTILISER`

### Entités tracées
- DONNEUR, DON, STOCK, HOPITAL, DISTRIBUTION, UTILISATEUR, SYSTEME

---

## 🐛 Bugs Corrigés

### Bug 1 : Boutons peu visibles
**Problème** : Les boutons n'étaient pas assez visibles dans l'interface
**Solution** : 
- Augmentation de la taille (150-200x45px)
- Ajout de bordures et couleurs de fond
- Effets de survol (hover)
- Police en gras 14px

### Bug 2 : Dashboard ne se met pas à jour
**Problème** : Le tableau de bord ne reflétait pas les nouveaux dons validés
**Cause** : La validation d'un don ne créait pas de stock sanguin
**Solution** : Modification de `DonDAO.valider()` pour :
1. Mettre à jour le statut du don
2. Récupérer les infos du don et du donneur
3. Créer un nouveau `StockSanguin` avec :
   - Groupe sanguin du donneur
   - Quantité du don
   - Date de péremption = don + 42 jours
   - Statut = DISPONIBLE

---

## 🩸 Règles Métier Implémentées

### Dons de Sang
- Quantité standard : **450 ml**
- Plage acceptée : 200 - 500 ml
- Statuts : EN_ATTENTE → VALIDE / REJETE

### Conservation du Sang
- Durée de vie : **42 jours**
- Statuts stock : DISPONIBLE, UTILISE, PERIME

### Groupes Sanguins
- A+, A-, B+, B-, AB+, AB-, O+, O-

---

## 📁 Structure du Projet

```
BloodPlus/
├── src/
│   ├── dao/           # Data Access Objects
│   ├── model/         # Entités métier
│   ├── service/       # Services (Session)
│   ├── ui/            # Interfaces graphiques
│   └── utils/         # Utilitaires
├── database/
│   └── bloodplus.sql  # Script création BDD
├── docs/
│   ├── Documentation_BloodPlus.md
│   └── Historique_Developpement.md
├── lib/
│   └── mysql-connector-j-9.0.0.jar
└── build/
    └── classes/       # Classes compilées
```

---

## 💻 Commandes Utiles

### Compilation
```bash
cd /Users/dteach/NetBeansProjects/BloodPlus
javac -d build/classes -cp "lib/*" src/model/*.java src/dao/*.java src/service/*.java src/utils/*.java src/ui/*.java
```

### Exécution
```bash
cd /Users/dteach/NetBeansProjects/BloodPlus/build/classes
java -cp ".:../../lib/*" ui.LoginFrame
```

### Base de données
```bash
mysql -u root -p < database/bloodplus.sql
```

---

## 📝 Notes Importantes

1. **Sécurité** : Les mots de passe sont stockés en clair (à hasher en production)
2. **Connexion BDD** : Modifier `DatabaseConnection.java` pour changer les credentials
3. **Péremption** : Le sang expire après 42 jours (norme médicale)
4. **Stock** : Créé automatiquement à la validation d'un don

---

## 🎓 Concepts Appris

- **Pattern MVC** : Séparation modèle/vue/contrôleur
- **Pattern DAO** : Encapsulation de l'accès aux données
- **Pattern Singleton** : Instance unique (DatabaseConnection, Session)
- **Java Swing** : Création d'interfaces graphiques
- **JDBC** : Connexion Java ↔ MySQL
- **Gestion des rôles** : Permissions basées sur les rôles

---

## 📞 Support

- **Développé avec** : GitHub Copilot (Claude Opus 4.5)
- **Date** : 20 Janvier 2026
- **IDE** : Apache NetBeans / VS Code

---

*Fin de l'historique de développement*
