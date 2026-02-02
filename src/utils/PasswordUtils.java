/*
 * Package Utils
 */
package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * PasswordUtils : Utilitaire pour le hachage sécurisé des mots de passe
 * 
 * 💡 EXPLICATION :
 * - Utilise SHA-256 avec un sel (salt) pour plus de sécurité
 * - Le sel rend chaque hash unique même pour des mots de passe identiques
 * - Protège contre les attaques par dictionnaire et rainbow tables
 * 
 * @author dteach
 */
public class PasswordUtils {
    
    // Longueur du sel en bytes
    private static final int SALT_LENGTH = 16;
    
    /**
     * Génère un sel aléatoire
     * 
     * @return Le sel encodé en Base64
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Hash un mot de passe avec SHA-256 et un sel
     * 
     * @param password Le mot de passe en clair
     * @param salt Le sel à utiliser
     * @return Le hash encodé en Base64
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // Combiner le sel et le mot de passe
            String saltedPassword = salt + password;
            byte[] hash = md.digest(saltedPassword.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur de hachage SHA-256", e);
        }
    }
    
    /**
     * Hash un mot de passe et retourne le format stocké: salt:hash
     * 
     * @param password Le mot de passe en clair
     * @return Le format stocké "salt:hash"
     */
    public static String hashPassword(String password) {
        String salt = generateSalt();
        String hash = hashPassword(password, salt);
        return salt + ":" + hash;
    }
    
    /**
     * Vérifie si un mot de passe correspond au hash stocké
     * 
     * @param password Le mot de passe à vérifier
     * @param storedHash Le hash stocké au format "salt:hash"
     * @return true si le mot de passe correspond
     */
    public static boolean verifyPassword(String password, String storedHash) {
        // Vérifier si c'est un ancien format (pas de sel)
        if (!storedHash.contains(":")) {
            // Ancien format : mot de passe en clair ou hash simple
            // Pour compatibilité, on compare directement
            return password.equals(storedHash);
        }
        
        // Nouveau format : salt:hash
        String[] parts = storedHash.split(":", 2);
        if (parts.length != 2) {
            return false;
        }
        
        String salt = parts[0];
        String hash = parts[1];
        
        // Recalculer le hash avec le même sel
        String computedHash = hashPassword(password, salt);
        
        return hash.equals(computedHash);
    }
    
    /**
     * Vérifie si un hash est au nouveau format sécurisé
     * 
     * @param storedHash Le hash stocké
     * @return true si c'est le nouveau format salt:hash
     */
    public static boolean isSecureFormat(String storedHash) {
        return storedHash != null && storedHash.contains(":") && storedHash.split(":").length == 2;
    }
}
