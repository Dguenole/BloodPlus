/*
 * Package MODEL : contient les "objets métier"
 */
package model;

import java.util.Date;

/**
 * Classe StockSanguin : représente le stock de sang disponible
 * 
 * 💡 EXPLICATION :
 * Chaque poche de sang a une date de péremption (environ 42 jours)
 * On doit surveiller les stocks pour éviter les ruptures
 * 
 * @author dteach
 */
public class StockSanguin {
    
    // ============ ATTRIBUTS ============
    private int id;
    private String groupeSanguin;      // Le groupe sanguin
    private int quantite;              // Quantité en ml
    private Date datePrelevement;      // Date du prélèvement
    private Date datePeremption;       // Date limite d'utilisation
    private int donId;                 // ID du don d'origine
    private String statut;             // "DISPONIBLE", "RESERVE", "UTILISE", "PERIME"

    // ============ CONSTANTES ============
    public static final String STATUT_DISPONIBLE = "DISPONIBLE";
    public static final String STATUT_RESERVE = "RESERVE";
    public static final String STATUT_UTILISE = "UTILISE";
    public static final String STATUT_PERIME = "PERIME";
    
    // Durée de vie d'une poche de sang en jours
    public static final int DUREE_VIE_JOURS = 42;
    
    // Seuil d'alerte : si le stock descend en dessous, on alerte
    public static final int SEUIL_ALERTE_ML = 2000; // 2 litres

    // ============ CONSTRUCTEURS ============
    
    public StockSanguin() {
    }

    public StockSanguin(String groupeSanguin, int quantite, Date datePrelevement) {
        this.groupeSanguin = groupeSanguin;
        this.quantite = quantite;
        this.datePrelevement = datePrelevement;
        this.statut = STATUT_DISPONIBLE;
        
        // Calcul automatique de la date de péremption (42 jours après)
        this.datePeremption = new Date(datePrelevement.getTime() + 
                                       (DUREE_VIE_JOURS * 24L * 60L * 60L * 1000L));
    }

    // ============ GETTERS ============
    
    public int getId() {
        return id;
    }

    public String getGroupeSanguin() {
        return groupeSanguin;
    }

    public int getQuantite() {
        return quantite;
    }

    public Date getDatePrelevement() {
        return datePrelevement;
    }

    public Date getDatePeremption() {
        return datePeremption;
    }

    public int getDonId() {
        return donId;
    }

    public String getStatut() {
        return statut;
    }

    // ============ SETTERS ============
    
    public void setId(int id) {
        this.id = id;
    }

    public void setGroupeSanguin(String groupeSanguin) {
        this.groupeSanguin = groupeSanguin;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public void setDatePrelevement(Date datePrelevement) {
        this.datePrelevement = datePrelevement;
    }

    public void setDatePeremption(Date datePeremption) {
        this.datePeremption = datePeremption;
    }

    public void setDonId(int donId) {
        this.donId = donId;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    // ============ MÉTHODES UTILES ============
    
    /**
     * Vérifie si le stock est périmé
     */
    public boolean estPerime() {
        return new Date().after(datePeremption);
    }

    /**
     * Calcule le nombre de jours avant péremption
     */
    public int joursAvantPeremption() {
        long diff = datePeremption.getTime() - new Date().getTime();
        return (int) (diff / (24 * 60 * 60 * 1000));
    }

    /**
     * Vérifie si le stock est disponible pour utilisation
     */
    public boolean estDisponible() {
        return STATUT_DISPONIBLE.equals(statut) && !estPerime();
    }

    @Override
    public String toString() {
        return "StockSanguin{" + 
               "id=" + id + 
               ", groupeSanguin='" + groupeSanguin + '\'' + 
               ", quantite=" + quantite + "ml" +
               ", joursRestants=" + joursAvantPeremption() +
               ", statut='" + statut + '\'' + 
               '}';
    }
}
