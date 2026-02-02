/*
 * Package MODEL
 */
package model;

import java.util.Date;

/**
 * ActionLog : Enregistre une action effectuée par un utilisateur
 * 
 * 💡 EXPLICATION :
 * C'est un "journal d'audit" qui trace tout ce que font les utilisateurs
 * Très utile pour :
 * - Savoir qui a fait quoi et quand
 * - Détecter des problèmes ou erreurs
 * - Avoir un historique complet
 * 
 * @author dteach
 */
public class ActionLog {
    
    // ============ ATTRIBUTS ============
    private int id;
    private int utilisateurId;          // Qui a fait l'action
    private String utilisateurNom;      // Nom de l'utilisateur (pour affichage)
    private String action;              // Type d'action (AJOUTER, MODIFIER, etc.)
    private String entite;              // Sur quoi (DONNEUR, DON, STOCK, etc.)
    private String description;         // Description détaillée
    private Date dateAction;            // Quand
    private String adresseIP;           // Optionnel: depuis où

    // ============ CONSTANTES - TYPES D'ACTIONS ============
    public static final String ACTION_CONNEXION = "CONNEXION";
    public static final String ACTION_DECONNEXION = "DECONNEXION";
    public static final String ACTION_AJOUTER = "AJOUTER";
    public static final String ACTION_MODIFIER = "MODIFIER";
    public static final String ACTION_SUPPRIMER = "SUPPRIMER";
    public static final String ACTION_VALIDER = "VALIDER";
    public static final String ACTION_REJETER = "REJETER";
    public static final String ACTION_CONSULTER = "CONSULTER";
    public static final String ACTION_LIVRER = "LIVRER";
    public static final String ACTION_ANNULER = "ANNULER";
    public static final String ACTION_UTILISER = "UTILISER";
    
    // ============ CONSTANTES - ENTITÉS ============
    public static final String ENTITE_DONNEUR = "DONNEUR";
    public static final String ENTITE_DON = "DON";
    public static final String ENTITE_STOCK = "STOCK";
    public static final String ENTITE_HOPITAL = "HOPITAL";
    public static final String ENTITE_DISTRIBUTION = "DISTRIBUTION";
    public static final String ENTITE_UTILISATEUR = "UTILISATEUR";
    public static final String ENTITE_SYSTEME = "SYSTEME";

    // ============ CONSTRUCTEURS ============
    
    public ActionLog() {
        this.dateAction = new Date();
    }

    public ActionLog(int utilisateurId, String utilisateurNom, String action, 
                     String entite, String description) {
        this();
        this.utilisateurId = utilisateurId;
        this.utilisateurNom = utilisateurNom;
        this.action = action;
        this.entite = entite;
        this.description = description;
    }

    // ============ GETTERS ============
    
    public int getId() {
        return id;
    }

    public int getUtilisateurId() {
        return utilisateurId;
    }

    public String getUtilisateurNom() {
        return utilisateurNom;
    }

    public String getAction() {
        return action;
    }

    public String getEntite() {
        return entite;
    }

    public String getDescription() {
        return description;
    }

    public Date getDateAction() {
        return dateAction;
    }

    public String getAdresseIP() {
        return adresseIP;
    }

    // ============ SETTERS ============
    
    public void setId(int id) {
        this.id = id;
    }

    public void setUtilisateurId(int utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public void setUtilisateurNom(String utilisateurNom) {
        this.utilisateurNom = utilisateurNom;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setEntite(String entite) {
        this.entite = entite;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDateAction(Date dateAction) {
        this.dateAction = dateAction;
    }

    public void setAdresseIP(String adresseIP) {
        this.adresseIP = adresseIP;
    }

    /**
     * Retourne l'emoji correspondant à l'action
     */
    public String getActionEmoji() {
        switch (action) {
            case ACTION_CONNEXION:
                return "🔑";
            case ACTION_DECONNEXION:
                return "🚪";
            case ACTION_AJOUTER:
                return "➕";
            case ACTION_MODIFIER:
                return "✏️";
            case ACTION_SUPPRIMER:
                return "🗑️";
            case ACTION_VALIDER:
                return "✅";
            case ACTION_REJETER:
                return "❌";
            case ACTION_CONSULTER:
                return "👁️";
            default:
                return "📝";
        }
    }

    /**
     * Retourne l'emoji correspondant à l'entité
     */
    public String getEntiteEmoji() {
        switch (entite) {
            case ENTITE_DONNEUR:
                return "👤";
            case ENTITE_DON:
                return "💉";
            case ENTITE_STOCK:
                return "🩸";
            case ENTITE_HOPITAL:
                return "🏥";
            case ENTITE_DISTRIBUTION:
                return "📦";
            case ENTITE_UTILISATEUR:
                return "👥";
            case ENTITE_SYSTEME:
                return "⚙️";
            default:
                return "📋";
        }
    }

    @Override
    public String toString() {
        return getActionEmoji() + " " + action + " - " + 
               getEntiteEmoji() + " " + entite + ": " + description;
    }
}
