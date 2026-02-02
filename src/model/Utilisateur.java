/*
 * Package MODEL
 */
package model;

import java.util.Date;

/**
 * Classe Utilisateur : représente un utilisateur de l'application
 * 
 * 💡 EXPLICATION DES RÔLES :
 * - ADMIN : Accès total + gestion des utilisateurs
 * - OPERATEUR : Peut gérer donneurs, dons, stock, distributions
 * - LECTEUR : Consultation seulement (pas de modification)
 * 
 * @author dteach
 */
public class Utilisateur {
    
    // ============ ATTRIBUTS ============
    private int id;
    private String username;           // Nom d'utilisateur (login)
    private String password;           // Mot de passe
    private String nomComplet;         // Nom affiché
    private String role;               // ADMIN, OPERATEUR, LECTEUR
    private boolean actif;             // Compte actif ?
    private Date dateCreation;
    private Date derniereConnexion;

    // ============ CONSTANTES - RÔLES ============
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_OPERATEUR = "OPERATEUR";
    public static final String ROLE_LECTEUR = "LECTEUR";
    
    public static final String[] TOUS_LES_ROLES = {ROLE_ADMIN, ROLE_OPERATEUR, ROLE_LECTEUR};

    // ============ CONSTRUCTEURS ============
    
    public Utilisateur() {
        this.actif = true;
        this.dateCreation = new Date();
    }

    public Utilisateur(String username, String password, String nomComplet, String role) {
        this();
        this.username = username;
        this.password = password;
        this.nomComplet = nomComplet;
        this.role = role;
    }

    // ============ GETTERS ============
    
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    public String getRole() {
        return role;
    }

    public boolean isActif() {
        return actif;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public Date getDerniereConnexion() {
        return derniereConnexion;
    }

    // ============ SETTERS ============
    
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setDerniereConnexion(Date derniereConnexion) {
        this.derniereConnexion = derniereConnexion;
    }

    // ============ MÉTHODES DE VÉRIFICATION DES PERMISSIONS ============
    
    /**
     * Vérifie si l'utilisateur est admin
     */
    public boolean isAdmin() {
        return ROLE_ADMIN.equals(this.role);
    }

    /**
     * Vérifie si l'utilisateur est opérateur
     */
    public boolean isOperateur() {
        return ROLE_OPERATEUR.equals(this.role);
    }

    /**
     * Vérifie si l'utilisateur est lecteur
     */
    public boolean isLecteur() {
        return ROLE_LECTEUR.equals(this.role);
    }

    /**
     * Vérifie si l'utilisateur peut modifier des données
     * (Admin et Opérateur peuvent modifier, Lecteur non)
     */
    public boolean peutModifier() {
        return isAdmin() || isOperateur();
    }

    /**
     * Vérifie si l'utilisateur peut supprimer des données
     * (Seul l'Admin peut supprimer)
     */
    public boolean peutSupprimer() {
        return isAdmin();
    }

    /**
     * Vérifie si l'utilisateur peut gérer les utilisateurs
     * (Seul l'Admin)
     */
    public boolean peutGererUtilisateurs() {
        return isAdmin();
    }

    /**
     * Retourne l'emoji correspondant au rôle
     */
    public String getRoleEmoji() {
        switch (role) {
            case ROLE_ADMIN:
                return "🔴";
            case ROLE_OPERATEUR:
                return "🟡";
            case ROLE_LECTEUR:
                return "🟢";
            default:
                return "⚪";
        }
    }

    @Override
    public String toString() {
        return nomComplet + " (" + role + ")";
    }
}
