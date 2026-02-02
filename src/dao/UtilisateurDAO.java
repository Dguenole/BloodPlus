/*
 * Package DAO
 */
package dao;

import model.ActionLog;
import model.Utilisateur;
import utils.PasswordUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UtilisateurDAO : gère les opérations sur les utilisateurs
 * 
 * @author dteach
 */
public class UtilisateurDAO {
    
    private Connection connection;
    private ActionLogDAO actionLogDAO;
    
    public UtilisateurDAO() {
        this.connection = DatabaseConnection.getConnection();
        this.actionLogDAO = new ActionLogDAO();
    }
    
    // ================================================================
    // AUTHENTIFICATION
    // ================================================================
    
    /**
     * 💡 Méthode principale de connexion
     * Vérifie le username et le mot de passe
     * 
     * @param username Le nom d'utilisateur
     * @param password Le mot de passe
     * @return L'utilisateur si authentifié, null sinon
     */
    public Utilisateur authentifier(String username, String password) {
        // 🔐 Nouvelle méthode avec hachage sécurisé
        String sql = "SELECT * FROM utilisateurs WHERE username = ? AND actif = true";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                
                // Vérifier le mot de passe avec le hash
                if (PasswordUtils.verifyPassword(password, storedPassword)) {
                    Utilisateur user = extraireUtilisateur(rs);
                    
                    // Si ancien format, migrer vers le nouveau format hashé
                    if (!PasswordUtils.isSecureFormat(storedPassword)) {
                        migrerMotDePasse(user.getId(), password);
                        System.out.println("🔐 Mot de passe migré vers format sécurisé pour: " + username);
                    }
                    
                    // Mettre à jour la date de dernière connexion
                    mettreAJourDerniereConnexion(user.getId());
                    
                    System.out.println("✅ Connexion réussie pour: " + user.getNomComplet());
                    return user;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur d'authentification: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("❌ Échec de connexion pour: " + username);
        return null;
    }
    
    /**
     * 🔐 Migre un mot de passe en clair vers le format hashé
     */
    private void migrerMotDePasse(int userId, String password) {
        String hashedPassword = PasswordUtils.hashPassword(password);
        String sql = "UPDATE utilisateurs SET password = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur migration mot de passe: " + e.getMessage());
        }
    }
    
    /**
     * Met à jour la date de dernière connexion
     */
    private void mettreAJourDerniereConnexion(int userId) {
        String sql = "UPDATE utilisateurs SET derniere_connexion = NOW() WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // ================================================================
    // CRUD
    // ================================================================
    
    public boolean ajouter(Utilisateur user) {
        String sql = "INSERT INTO utilisateurs (username, password, nom_complet, role, actif) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            // 🔐 Hasher le mot de passe avant stockage
            pstmt.setString(2, PasswordUtils.hashPassword(user.getPassword()));
            pstmt.setString(3, user.getNomComplet());
            pstmt.setString(4, user.getRole());
            pstmt.setBoolean(5, user.isActif());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
                System.out.println("✅ Utilisateur créé: " + user.getUsername());
                
                // Enregistrer l'action
                actionLogDAO.logAjout(ActionLog.ENTITE_UTILISATEUR, 
                    "Création de l'utilisateur: " + user.getUsername() + " (" + user.getRole() + ")");
                
                return true;
            }
            
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate")) {
                System.err.println("❌ Ce nom d'utilisateur existe déjà !");
            } else {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    public List<Utilisateur> listerTous() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs ORDER BY role, nom_complet";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                utilisateurs.add(extraireUtilisateur(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utilisateurs;
    }
    
    public Utilisateur trouverParId(int id) {
        String sql = "SELECT * FROM utilisateurs WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extraireUtilisateur(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Utilisateur trouverParUsername(String username) {
        String sql = "SELECT * FROM utilisateurs WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extraireUtilisateur(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean modifier(Utilisateur user) {
        String sql = "UPDATE utilisateurs SET username=?, nom_complet=?, role=?, actif=? WHERE id=?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getNomComplet());
            pstmt.setString(3, user.getRole());
            pstmt.setBoolean(4, user.isActif());
            pstmt.setInt(5, user.getId());
            
            if (pstmt.executeUpdate() > 0) {
                actionLogDAO.logModification(ActionLog.ENTITE_UTILISATEUR, 
                    "Modification de l'utilisateur: " + user.getUsername());
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Change le mot de passe d'un utilisateur
     */
    public boolean changerMotDePasse(int userId, String nouveauMotDePasse) {
        String sql = "UPDATE utilisateurs SET password = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // 🔐 Hasher le nouveau mot de passe
            pstmt.setString(1, PasswordUtils.hashPassword(nouveauMotDePasse));
            pstmt.setInt(2, userId);
            
            System.out.println("🔐 Mot de passe changé et hashé pour l'utilisateur ID: " + userId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean supprimer(int id) {
        // Récupérer l'utilisateur avant suppression pour le log
        Utilisateur user = trouverParId(id);
        String nomUser = user != null ? user.getUsername() : "ID:" + id;
        
        String sql = "DELETE FROM utilisateurs WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            if (pstmt.executeUpdate() > 0) {
                actionLogDAO.logSuppression(ActionLog.ENTITE_UTILISATEUR, 
                    "Suppression de l'utilisateur: " + nomUser);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Vérifie si un username existe déjà
     */
    public boolean usernameExiste(String username) {
        String sql = "SELECT COUNT(*) FROM utilisateurs WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public int compter() {
        String sql = "SELECT COUNT(*) FROM utilisateurs WHERE actif = true";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private Utilisateur extraireUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur user = new Utilisateur();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setNomComplet(rs.getString("nom_complet"));
        user.setRole(rs.getString("role"));
        user.setActif(rs.getBoolean("actif"));
        user.setDateCreation(rs.getTimestamp("date_creation"));
        user.setDerniereConnexion(rs.getTimestamp("derniere_connexion"));
        return user;
    }
}
