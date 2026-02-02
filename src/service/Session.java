/*
 * Package SERVICE
 */
package service;

import model.Utilisateur;

/**
 * Session : Gère la session de l'utilisateur connecté
 * 
 * 💡 EXPLICATION DU PATTERN SINGLETON :
 * - Une seule instance de Session pour toute l'application
 * - Permet d'accéder à l'utilisateur connecté depuis n'importe où
 * - getInstance() retourne toujours la même instance
 * 
 * @author dteach
 */
public class Session {
    
    // L'instance unique (Singleton)
    private static Session instance;
    
    // L'utilisateur actuellement connecté
    private Utilisateur utilisateurConnecte;
    
    /**
     * Constructeur privé (empêche la création d'instances externes)
     */
    private Session() {
    }
    
    /**
     * Retourne l'instance unique de Session
     */
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }
    
    // ============ GESTION DE LA CONNEXION ============
    
    /**
     * Connecte un utilisateur (stocke dans la session)
     */
    public void connecter(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
        System.out.println("📌 Session ouverte pour: " + utilisateur.getNomComplet());
    }
    
    /**
     * Déconnecte l'utilisateur actuel
     */
    public void deconnecter() {
        if (utilisateurConnecte != null) {
            System.out.println("👋 Session fermée pour: " + utilisateurConnecte.getNomComplet());
        }
        this.utilisateurConnecte = null;
    }
    
    /**
     * Vérifie si un utilisateur est connecté
     */
    public boolean estConnecte() {
        return utilisateurConnecte != null;
    }
    
    /**
     * Retourne l'utilisateur connecté
     */
    public Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }
    
    // ============ RACCOURCIS POUR LES PERMISSIONS ============
    
    /**
     * Vérifie si l'utilisateur connecté est admin
     */
    public boolean estAdmin() {
        return estConnecte() && utilisateurConnecte.isAdmin();
    }
    
    /**
     * Vérifie si l'utilisateur connecté est opérateur
     */
    public boolean estOperateur() {
        return estConnecte() && utilisateurConnecte.isOperateur();
    }
    
    /**
     * Vérifie si l'utilisateur connecté est lecteur
     */
    public boolean estLecteur() {
        return estConnecte() && utilisateurConnecte.isLecteur();
    }
    
    /**
     * Vérifie si l'utilisateur peut modifier des données
     */
    public boolean peutModifier() {
        return estConnecte() && utilisateurConnecte.peutModifier();
    }
    
    /**
     * Vérifie si l'utilisateur peut supprimer des données
     */
    public boolean peutSupprimer() {
        return estConnecte() && utilisateurConnecte.peutSupprimer();
    }
    
    /**
     * Vérifie si l'utilisateur peut gérer les utilisateurs
     */
    public boolean peutGererUtilisateurs() {
        return estConnecte() && utilisateurConnecte.peutGererUtilisateurs();
    }
    
    /**
     * Retourne le nom de l'utilisateur connecté
     */
    public String getNomUtilisateur() {
        return estConnecte() ? utilisateurConnecte.getNomComplet() : "Non connecté";
    }
    
    /**
     * Retourne le rôle de l'utilisateur connecté
     */
    public String getRole() {
        return estConnecte() ? utilisateurConnecte.getRole() : "";
    }
}
