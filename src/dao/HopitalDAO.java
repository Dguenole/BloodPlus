/*
 * Package DAO : Data Access Object
 */
package dao;

import model.ActionLog;
import model.Hopital;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * HopitalDAO : gère les opérations sur les hôpitaux
 * 
 * @author dteach
 */
public class HopitalDAO {
    
    private Connection connection;
    private ActionLogDAO actionLogDAO;
    
    public HopitalDAO() {
        this.connection = DatabaseConnection.getConnection();
        this.actionLogDAO = new ActionLogDAO();
    }
    
    // ================================================================
    // CREATE
    // ================================================================
    
    public boolean ajouter(Hopital hopital) {
        String sql = "INSERT INTO hopitaux (nom, adresse, ville, telephone, email, responsable, actif) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, hopital.getNom());
            pstmt.setString(2, hopital.getAdresse());
            pstmt.setString(3, hopital.getVille());
            pstmt.setString(4, hopital.getTelephone());
            pstmt.setString(5, hopital.getEmail());
            pstmt.setString(6, hopital.getResponsable());
            pstmt.setBoolean(7, hopital.isActif());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    hopital.setId(rs.getInt(1));
                }
                System.out.println("✅ Hôpital ajouté: " + hopital.getNom());
                
                // Enregistrer l'action
                actionLogDAO.logAjout(ActionLog.ENTITE_HOPITAL, 
                    "Ajout de l'hôpital: " + hopital.getNom() + " (" + hopital.getVille() + ")");
                
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
    
    public List<Hopital> listerTous() {
        List<Hopital> hopitaux = new ArrayList<>();
        String sql = "SELECT * FROM hopitaux ORDER BY nom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                hopitaux.add(extraireHopital(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hopitaux;
    }
    
    public List<Hopital> listerActifs() {
        List<Hopital> hopitaux = new ArrayList<>();
        String sql = "SELECT * FROM hopitaux WHERE actif = true ORDER BY nom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                hopitaux.add(extraireHopital(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hopitaux;
    }
    
    public Hopital trouverParId(int id) {
        String sql = "SELECT * FROM hopitaux WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extraireHopital(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Hopital> rechercherParVille(String ville) {
        List<Hopital> hopitaux = new ArrayList<>();
        String sql = "SELECT * FROM hopitaux WHERE ville LIKE ? AND actif = true";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + ville + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                hopitaux.add(extraireHopital(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hopitaux;
    }
    
    // ================================================================
    // UPDATE
    // ================================================================
    
    public boolean modifier(Hopital hopital) {
        String sql = "UPDATE hopitaux SET nom=?, adresse=?, ville=?, telephone=?, email=?, responsable=?, actif=? WHERE id=?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, hopital.getNom());
            pstmt.setString(2, hopital.getAdresse());
            pstmt.setString(3, hopital.getVille());
            pstmt.setString(4, hopital.getTelephone());
            pstmt.setString(5, hopital.getEmail());
            pstmt.setString(6, hopital.getResponsable());
            pstmt.setBoolean(7, hopital.isActif());
            pstmt.setInt(8, hopital.getId());
            
            if (pstmt.executeUpdate() > 0) {
                actionLogDAO.logModification(ActionLog.ENTITE_HOPITAL, 
                    "Modification de l'hôpital: " + hopital.getNom());
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // ================================================================
    // DELETE
    // ================================================================
    
    public boolean supprimer(int id) {
        // Récupérer l'hôpital avant suppression pour le log
        Hopital hopital = trouverParId(id);
        String nomHopital = hopital != null ? hopital.getNom() : "ID:" + id;
        
        String sql = "DELETE FROM hopitaux WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            if (pstmt.executeUpdate() > 0) {
                actionLogDAO.logSuppression(ActionLog.ENTITE_HOPITAL, 
                    "Suppression de l'hôpital: " + nomHopital);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public int compter() {
        String sql = "SELECT COUNT(*) FROM hopitaux WHERE actif = true";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private Hopital extraireHopital(ResultSet rs) throws SQLException {
        Hopital hopital = new Hopital();
        hopital.setId(rs.getInt("id"));
        hopital.setNom(rs.getString("nom"));
        hopital.setAdresse(rs.getString("adresse"));
        hopital.setVille(rs.getString("ville"));
        hopital.setTelephone(rs.getString("telephone"));
        hopital.setEmail(rs.getString("email"));
        hopital.setResponsable(rs.getString("responsable"));
        hopital.setActif(rs.getBoolean("actif"));
        return hopital;
    }
}
