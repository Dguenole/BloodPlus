/*
 * Package MODEL : contient les "objets métier"
 */
package model;

import java.util.Date;

/**
 * Classe Alerte : représente une alerte du système
 * 
 * 💡 EXPLICATION :
 * Les alertes servent à notifier quand :
 * - Un stock est trop bas
 * - Du sang va bientôt périmer
 * - Besoin urgent d'un groupe sanguin
 * 
 * @author dteach
 */
public class Alerte {
    
    // ============ ATTRIBUTS ============
    private int id;
    private String type;            // Type d'alerte
    private String message;         // Message de l'alerte
    private String groupeSanguin;   // Groupe concerné (si applicable)
    private Date dateCreation;      // Date de création
    private boolean lue;            // L'alerte a-t-elle été lue ?
    private String priorite;        // "BASSE", "MOYENNE", "HAUTE", "CRITIQUE"

    // ============ CONSTANTES - Types d'alertes ============
    public static final String TYPE_STOCK_BAS = "STOCK_BAS";
    public static final String TYPE_PEREMPTION = "PEREMPTION_PROCHE";
    public static final String TYPE_URGENT = "BESOIN_URGENT";
    public static final String TYPE_RUPTURE = "RUPTURE_STOCK";
    
    // ============ CONSTANTES - Priorités ============
    public static final String PRIORITE_BASSE = "BASSE";
    public static final String PRIORITE_MOYENNE = "MOYENNE";
    public static final String PRIORITE_HAUTE = "HAUTE";
    public static final String PRIORITE_CRITIQUE = "CRITIQUE";

    // ============ CONSTRUCTEURS ============
    
    public Alerte() {
        this.dateCreation = new Date();
        this.lue = false;
    }

    public Alerte(String type, String message, String priorite) {
        this.type = type;
        this.message = message;
        this.priorite = priorite;
        this.dateCreation = new Date();
        this.lue = false;
    }

    /**
     * Crée une alerte de stock bas
     */
    public static Alerte creerAlerteStockBas(String groupeSanguin, int quantiteActuelle) {
        Alerte alerte = new Alerte();
        alerte.type = TYPE_STOCK_BAS;
        alerte.groupeSanguin = groupeSanguin;
        alerte.message = "⚠️ Stock bas pour le groupe " + groupeSanguin + 
                        " : " + quantiteActuelle + " ml restants";
        alerte.priorite = PRIORITE_HAUTE;
        return alerte;
    }

    /**
     * Crée une alerte de péremption proche
     */
    public static Alerte creerAlertePeremption(String groupeSanguin, int joursRestants) {
        Alerte alerte = new Alerte();
        alerte.type = TYPE_PEREMPTION;
        alerte.groupeSanguin = groupeSanguin;
        alerte.message = "⏰ Du sang " + groupeSanguin + " va périmer dans " + 
                        joursRestants + " jours";
        alerte.priorite = joursRestants <= 3 ? PRIORITE_CRITIQUE : PRIORITE_MOYENNE;
        return alerte;
    }

    /**
     * Crée une alerte de rupture de stock
     */
    public static Alerte creerAlerteRupture(String groupeSanguin) {
        Alerte alerte = new Alerte();
        alerte.type = TYPE_RUPTURE;
        alerte.groupeSanguin = groupeSanguin;
        alerte.message = "🚨 RUPTURE DE STOCK pour le groupe " + groupeSanguin;
        alerte.priorite = PRIORITE_CRITIQUE;
        return alerte;
    }

    // ============ GETTERS ============
    
    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getGroupeSanguin() {
        return groupeSanguin;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public boolean isLue() {
        return lue;
    }

    public String getPriorite() {
        return priorite;
    }

    // ============ SETTERS ============
    
    public void setId(int id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setGroupeSanguin(String groupeSanguin) {
        this.groupeSanguin = groupeSanguin;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setLue(boolean lue) {
        this.lue = lue;
    }

    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }

    /**
     * Marque l'alerte comme lue
     */
    public void marquerCommeLue() {
        this.lue = true;
    }

    @Override
    public String toString() {
        return "Alerte{" + 
               "type='" + type + '\'' + 
               ", message='" + message + '\'' + 
               ", priorite='" + priorite + '\'' + 
               ", lue=" + lue + 
               '}';
    }
}
