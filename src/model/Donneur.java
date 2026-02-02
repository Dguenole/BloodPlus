/*
 * Package MODEL : contient les "objets métier"
 * Un objet métier représente une chose réelle de ton application
 */
package model;

import java.util.Date;

/**
 * Classe Donneur : représente une personne qui donne son sang
 * 
 * 💡 EXPLICATION :
 * - Les attributs (private) = les caractéristiques du donneur
 * - Le constructeur = pour créer un nouveau donneur
 * - Les getters = pour LIRE les valeurs
 * - Les setters = pour MODIFIER les valeurs
 * 
 * @author dteach
 */
public class Donneur {
    
    // ============ ATTRIBUTS (les caractéristiques) ============
    private int id;                    // Identifiant unique en base de données
    private String nom;                // Nom de famille
    private String prenom;             // Prénom
    private Date dateNaissance;        // Date de naissance
    private String sexe;               // "M" ou "F"
    private String groupeSanguin;      // Ex: "A+", "O-", "AB+"...
    private String telephone;          // Numéro de téléphone
    private String email;              // Adresse email
    private String adresse;            // Adresse postale
    private Date dateInscription;      // Date d'inscription comme donneur
    private boolean apte;              // Est-il apte à donner ? (true/false)

    // ============ CONSTRUCTEURS ============
    
    /**
     * Constructeur vide : crée un donneur sans infos
     * Utile quand on récupère les données de la BD
     */
    public Donneur() {
    }

    /**
     * Constructeur avec les infos essentielles
     */
    public Donneur(String nom, String prenom, String groupeSanguin, String telephone) {
        this.nom = nom;
        this.prenom = prenom;
        this.groupeSanguin = groupeSanguin;
        this.telephone = telephone;
        this.dateInscription = new Date(); // Date du jour
        this.apte = true; // Par défaut, apte à donner
    }

    // ============ GETTERS (pour lire les valeurs) ============
    
    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public String getSexe() {
        return sexe;
    }

    public String getGroupeSanguin() {
        return groupeSanguin;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getEmail() {
        return email;
    }

    public String getAdresse() {
        return adresse;
    }

    public Date getDateInscription() {
        return dateInscription;
    }

    public boolean isApte() {
        return apte;
    }

    // ============ SETTERS (pour modifier les valeurs) ============
    
    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public void setGroupeSanguin(String groupeSanguin) {
        this.groupeSanguin = groupeSanguin;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setDateInscription(Date dateInscription) {
        this.dateInscription = dateInscription;
    }

    public void setApte(boolean apte) {
        this.apte = apte;
    }

    // ============ MÉTHODES UTILES ============
    
    /**
     * Retourne le nom complet du donneur
     */
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    /**
     * Affichage du donneur (utile pour debug)
     */
    @Override
    public String toString() {
        return "Donneur{" + 
               "id=" + id + 
               ", nom='" + nom + '\'' + 
               ", prenom='" + prenom + '\'' + 
               ", groupeSanguin='" + groupeSanguin + '\'' + 
               '}';
    }
}
