/*
 * Package MODEL : contient les "objets métier"
 */
package model;

import java.util.Date;

/**
 * Classe Distribution : représente une distribution de sang vers un hôpital
 * 
 * 💡 EXPLICATION :
 * Quand un hôpital a besoin de sang, on crée une distribution
 * Cela diminue le stock et garde un historique
 * 
 * @author dteach
 */
public class Distribution {
    
    // ============ ATTRIBUTS ============
    private int id;
    private int hopitalId;          // ID de l'hôpital destinataire
    private String groupeSanguin;   // Groupe sanguin distribué
    private int quantite;           // Quantité en ml
    private Date dateDistribution;  // Date de la distribution
    private String statut;          // "EN_COURS", "LIVREE", "ANNULEE"
    private String motif;           // Raison de la demande (urgence, opération, etc.)
    
    // Objet Hopital associé (pour affichage)
    private Hopital hopital;

    // ============ CONSTANTES ============
    public static final String STATUT_EN_COURS = "EN_COURS";
    public static final String STATUT_LIVREE = "LIVREE";
    public static final String STATUT_ANNULEE = "ANNULEE";

    // ============ CONSTRUCTEURS ============
    
    public Distribution() {
    }

    public Distribution(int hopitalId, String groupeSanguin, int quantite) {
        this.hopitalId = hopitalId;
        this.groupeSanguin = groupeSanguin;
        this.quantite = quantite;
        this.dateDistribution = new Date();
        this.statut = STATUT_EN_COURS;
    }

    // ============ GETTERS ============
    
    public int getId() {
        return id;
    }

    public int getHopitalId() {
        return hopitalId;
    }

    public String getGroupeSanguin() {
        return groupeSanguin;
    }

    public int getQuantite() {
        return quantite;
    }

    public Date getDateDistribution() {
        return dateDistribution;
    }

    public String getStatut() {
        return statut;
    }

    public String getMotif() {
        return motif;
    }

    public Hopital getHopital() {
        return hopital;
    }

    // ============ SETTERS ============
    
    public void setId(int id) {
        this.id = id;
    }

    public void setHopitalId(int hopitalId) {
        this.hopitalId = hopitalId;
    }

    public void setGroupeSanguin(String groupeSanguin) {
        this.groupeSanguin = groupeSanguin;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public void setDateDistribution(Date dateDistribution) {
        this.dateDistribution = dateDistribution;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public void setHopital(Hopital hopital) {
        this.hopital = hopital;
    }

    @Override
    public String toString() {
        return "Distribution{" + 
               "id=" + id + 
               ", hopitalId=" + hopitalId + 
               ", groupeSanguin='" + groupeSanguin + '\'' + 
               ", quantite=" + quantite + "ml" +
               ", statut='" + statut + '\'' + 
               '}';
    }
}
