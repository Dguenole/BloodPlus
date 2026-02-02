/*
 * Package MODEL : contient les "objets métier"
 */
package model;

/**
 * Classe Hopital : représente un hôpital qui demande du sang
 * 
 * 💡 EXPLICATION :
 * Les hôpitaux sont les "clients" de la banque de sang
 * Ils font des demandes de sang pour leurs patients
 * 
 * @author dteach
 */
public class Hopital {
    
    // ============ ATTRIBUTS ============
    private int id;
    private String nom;            // Nom de l'hôpital
    private String adresse;        // Adresse complète
    private String ville;          // Ville
    private String telephone;      // Numéro de contact
    private String email;          // Email de contact
    private String responsable;    // Nom du responsable
    private boolean actif;         // L'hôpital est-il actif ?

    // ============ CONSTRUCTEURS ============
    
    public Hopital() {
        this.actif = true;
    }

    public Hopital(String nom, String adresse, String telephone) {
        this.nom = nom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.actif = true;
    }

    // ============ GETTERS ============
    
    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getVille() {
        return ville;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getEmail() {
        return email;
    }

    public String getResponsable() {
        return responsable;
    }

    public boolean isActif() {
        return actif;
    }

    // ============ SETTERS ============
    
    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    @Override
    public String toString() {
        return "Hopital{" + 
               "id=" + id + 
               ", nom='" + nom + '\'' + 
               ", ville='" + ville + '\'' + 
               '}';
    }
}
