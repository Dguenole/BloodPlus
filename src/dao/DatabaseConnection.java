/*
 * Package DAO : Data Access Object (Accès aux données)
 * Ce package contient tout ce qui communique avec la base de données
 */
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe DatabaseConnection : gère la connexion à MySQL
 * 
 * 💡 EXPLICATION :
 * Cette classe utilise le pattern SINGLETON
 * = Une seule instance de connexion pour toute l'application
 * 
 * Pour se connecter à MySQL, on a besoin :
 * - L'URL : où se trouve la base (localhost = ton ordi)
 * - Le USER : nom d'utilisateur MySQL
 * - Le PASSWORD : mot de passe MySQL
 * 
 * @author dteach
 */
public class DatabaseConnection {
    
    // ============ CONFIGURATION DE LA BASE DE DONNÉES ============
    // 🔧 MODIFIE CES VALEURS SELON TA CONFIGURATION
    
    private static final String URL = "jdbc:mysql://localhost:3306/bloodplus";
    private static final String USER = "root";           // Ton utilisateur MySQL
    private static final String PASSWORD = "";           // Ton mot de passe MySQL
    
    // ============ SINGLETON ============
    private static Connection connection = null;

    /**
     * Constructeur privé (empêche de créer plusieurs instances)
     */
    private DatabaseConnection() {
    }

    /**
     * Obtenir la connexion à la base de données
     * 
     * 💡 Si la connexion n'existe pas, on la crée
     *    Si elle existe déjà, on la retourne
     * 
     * @return La connexion à MySQL
     */
    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Charger le driver MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Établir la connexion
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connexion à la base de données réussie !");
                
            } catch (ClassNotFoundException e) {
                System.err.println("❌ Driver MySQL non trouvé !");
                System.err.println("💡 Assure-toi d'avoir ajouté le JAR mysql-connector au projet");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("❌ Erreur de connexion à MySQL !");
                System.err.println("💡 Vérifie que MySQL est démarré et que la base 'bloodplus' existe");
                e.printStackTrace();
            }
        }
        return connection;
    }

    /**
     * Fermer la connexion proprement
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("👋 Connexion fermée");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Tester la connexion
     * @return true si la connexion fonctionne
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Test de connexion réussi !");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("❌ Test de connexion échoué !");
        return false;
    }
}
