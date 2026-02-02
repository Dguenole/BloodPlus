/*
 * Package DAO : Data Access Object
 */
package dao;

import model.Alerte;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AlerteDAO : gère les alertes système
 * 
 * @author dteach
 */
public class AlerteDAO {
    
    private Connection connection;
    
    public AlerteDAO() {
        this.connection = DatabaseConnection.getConnection();
    }
    
    // ================================================================
    // CREATE
    // ================================================================
    
    public boolean ajouter(Alerte alerte) {
        String sql = "INSERT INTO alertes (type, message, groupe_sanguin, priorite, lue) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, alerte.getType());
            pstmt.setString(2, alerte.getMessage());
            pstmt.setString(3, alerte.getGroupeSanguin());
            pstmt.setString(4, alerte.getPriorite());
            pstmt.setBoolean(5, alerte.isLue());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    alerte.setId(rs.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // ================================================================
    // READ
    // ================================================================
    
    /**
     * Récupère toutes les alertes non lues
     */
    public List<Alerte> listerNonLues() {
        List<Alerte> alertes = new ArrayList<>();
        String sql = "SELECT * FROM alertes WHERE lue = false ORDER BY " +
                     "FIELD(priorite, 'CRITIQUE', 'HAUTE', 'MOYENNE', 'BASSE'), date_creation DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                alertes.add(extraireAlerte(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alertes;
    }
    
    /**
     * Récupère toutes les alertes
     */
    public List<Alerte> listerTous() {
        List<Alerte> alertes = new ArrayList<>();
        String sql = "SELECT * FROM alertes ORDER BY date_creation DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                alertes.add(extraireAlerte(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alertes;
    }
    
    /**
     * Récupère les alertes par priorité
     */
    public List<Alerte> listerParPriorite(String priorite) {
        List<Alerte> alertes = new ArrayList<>();
        String sql = "SELECT * FROM alertes WHERE priorite = ? AND lue = false ORDER BY date_creation DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, priorite);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                alertes.add(extraireAlerte(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alertes;
    }
    
    /**
     * Compte les alertes non lues
     */
    public int compterNonLues() {
        String sql = "SELECT COUNT(*) FROM alertes WHERE lue = false";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Compte les alertes critiques non lues
     */
    public int compterCritiques() {
        String sql = "SELECT COUNT(*) FROM alertes WHERE priorite = 'CRITIQUE' AND lue = false";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // ================================================================
    // UPDATE
    // ================================================================
    
    /**
     * Marquer une alerte comme lue
     */
    public boolean marquerCommeLue(int alerteId) {
        String sql = "UPDATE alertes SET lue = true WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, alerteId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Marquer toutes les alertes comme lues
     */
    public int marquerToutesCommeLues() {
        String sql = "UPDATE alertes SET lue = true WHERE lue = false";
        
        try (Statement stmt = connection.createStatement()) {
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // ================================================================
    // DELETE
    // ================================================================
    
    public boolean supprimer(int id) {
        String sql = "DELETE FROM alertes WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Supprimer les alertes lues de plus de X jours
     */
    public int supprimerAnciennes(int joursAvant) {
        String sql = "DELETE FROM alertes WHERE lue = true AND date_creation < DATE_SUB(NOW(), INTERVAL ? DAY)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, joursAvant);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private Alerte extraireAlerte(ResultSet rs) throws SQLException {
        Alerte alerte = new Alerte();
        alerte.setId(rs.getInt("id"));
        alerte.setType(rs.getString("type"));
        alerte.setMessage(rs.getString("message"));
        alerte.setGroupeSanguin(rs.getString("groupe_sanguin"));
        alerte.setDateCreation(rs.getTimestamp("date_creation"));
        alerte.setLue(rs.getBoolean("lue"));
        alerte.setPriorite(rs.getString("priorite"));
        return alerte;
    }
}
