/*
 * Package MODEL : contient les "objets métier"
 */
package model;

import java.util.Date;

/**
 * Classe Don : représente un acte de don de sang
 * 
 * 💡 EXPLICATION :
 * Un don est l'action de prélever du sang chez un donneur
 * Il a une date, une quantité, et est lié à un donneur
 * 
 * @author dteach
 */
public class Don {
    
    // ============ ATTRIBUTS ============
    private int id;                    // Identifiant unique
    private int donneurId;             // ID du donneur (clé étrangère)
    private Date dateDon;              // Date du don
    private int quantite;              // Quantité en millilitres (ml)
    private String statut;             // "EN_ATTENTE", "VALIDE", "REJETE"
    private String notes;              // Remarques éventuelles
    
    // Objet Donneur associé (optionnel, pour affichage)
    private Donneur donneur;

    // ============ CONSTANTES pour les statuts ============
    public static final String STATUT_EN_ATTENTE = "EN_ATTENTE";
    public static final String STATUT_VALIDE = "VALIDE";
    public static final String STATUT_REJETE = "REJETE";
    
    // Quantité standard d'un don de sang en ml
    public static final int QUANTITE_STANDARD = 450;

    // ============ CONSTRUCTEURS ============
    
    public Don() {
    }

    public Don(int donneurId, Date dateDon, int quantite) {
        this.donneurId = donneurId;
        this.dateDon = dateDon;
        this.quantite = quantite;
        this.statut = STATUT_EN_ATTENTE; // Par défaut en attente de validation
    }

    // ============ GETTERS ============
    
    public int getId() {
        return id;
    }

    public int getDonneurId() {
        return donneurId;
    }

    public Date getDateDon() {
        return dateDon;
    }

    public int getQuantite() {
        return quantite;
    }

    public String getStatut() {
        return statut;
    }

    public String getNotes() {
        return notes;
    }

    public Donneur getDonneur() {
        return donneur;
    }

    // ============ SETTERS ============
    
    public void setId(int id) {
        this.id = id;
    }

    public void setDonneurId(int donneurId) {
        this.donneurId = donneurId;
    }

    public void setDateDon(Date dateDon) {
        this.dateDon = dateDon;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setDonneur(Donneur donneur) {
        this.donneur = donneur;
    }

    // ============ MÉTHODES UTILES ============
    
    /**
     * Vérifie si le don est validé
     */
    public boolean estValide() {
        return STATUT_VALIDE.equals(this.statut);
    }

    @Override
    public String toString() {
        return "Don{" + 
               "id=" + id + 
               ", donneurId=" + donneurId + 
               ", dateDon=" + dateDon + 
               ", quantite=" + quantite + "ml" +
               ", statut='" + statut + '\'' + 
               '}';
    }
}
