/*
 * Package UTILS : fonctions utilitaires réutilisables
 */
package utils;

/**
 * Classe ValidationUtils : valide les données saisies par l'utilisateur
 * 
 * 💡 EXPLICATION :
 * Avant d'enregistrer des données, on doit vérifier qu'elles sont correctes
 * Ex: un email doit contenir @, un téléphone doit avoir des chiffres, etc.
 * 
 * @author dteach
 */
public class ValidationUtils {

    /**
     * Vérifie si une chaîne est vide ou null
     */
    public static boolean estVide(String texte) {
        return texte == null || texte.trim().isEmpty();
    }

    /**
     * Vérifie si un email est valide
     * Ex: "test@gmail.com" -> true
     *     "test" -> false
     */
    public static boolean estEmailValide(String email) {
        if (estVide(email)) return false;
        // Vérification simple : contient @ et un point après
        return email.contains("@") && email.contains(".") && 
               email.indexOf("@") < email.lastIndexOf(".");
    }

    /**
     * Vérifie si un numéro de téléphone est valide
     * Accepte les formats : 0612345678, 06 12 34 56 78, +33612345678
     */
    public static boolean estTelephoneValide(String telephone) {
        if (estVide(telephone)) return false;
        // Enlever les espaces et le +
        String tel = telephone.replaceAll("[\\s+\\-]", "");
        // Vérifier que c'est que des chiffres et longueur correcte
        return tel.matches("\\d{10,12}");
    }

    /**
     * Vérifie si un groupe sanguin est valide
     */
    public static boolean estGroupeSanguinValide(String groupe) {
        if (estVide(groupe)) return false;
        String[] groupesValides = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String g : groupesValides) {
            if (g.equals(groupe)) return true;
        }
        return false;
    }

    /**
     * Vérifie si un nom/prénom est valide (pas de chiffres, min 2 caractères)
     */
    public static boolean estNomValide(String nom) {
        if (estVide(nom)) return false;
        if (nom.trim().length() < 2) return false;
        // Pas de chiffres dans un nom
        return !nom.matches(".*\\d.*");
    }

    /**
     * Vérifie si une quantité est valide (positive)
     */
    public static boolean estQuantiteValide(int quantite) {
        return quantite > 0;
    }

    /**
     * Vérifie si une quantité de don est valide (entre 200 et 500 ml)
     */
    public static boolean estQuantiteDonValide(int quantite) {
        return quantite >= 200 && quantite <= 500;
    }
}
