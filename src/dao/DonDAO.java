/*
 * Package DAO : Data Access Object
 */
package dao;

import model.ActionLog;
import model.Don;
import model.Donneur;
import model.StockSanguin;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * DonDAO : gère les opérations sur les dons de sang
 * 
 * @author dteach
 */
public class DonDAO {
    
    private Connection connection;
    private ActionLogDAO actionLogDAO;
    
    public DonDAO() {
        this.connection = DatabaseConnection.getConnection();
        this.actionLogDAO = new ActionLogDAO();
    }
    
    // ================================================================
    // CREATE : Ajouter un don
    // ================================================================
    
    public boolean ajouter(Don don) {
        String sql = "INSERT INTO dons (donneur_id, date_don, quantite, statut, notes) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, don.getDonneurId());
            pstmt.setTimestamp(2, new Timestamp(don.getDateDon().getTime()));
            pstmt.setInt(3, don.getQuantite());
            pstmt.setString(4, don.getStatut());
            pstmt.setString(5, don.getNotes());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    don.setId(rs.getInt(1));
                }
                System.out.println("✅ Don enregistré avec ID: " + don.getId());
                
                // Enregistrer l'action
                actionLogDAO.logAjout(ActionLog.ENTITE_DON, 
                    "Nouveau don de " + don.getQuantite() + "ml (ID:" + don.getId() + ")");
                
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout du don: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // ================================================================
    // READ : Lire les dons
    // ================================================================
    
    /**
     * Récupère tous les dons avec les infos du donneur
     */
    public List<Don> listerTous() {
        List<Don> dons = new ArrayList<>();
        // JOIN pour récupérer aussi les infos du donneur
        String sql = "SELECT d.*, dn.nom, dn.prenom, dn.groupe_sanguin " +
                     "FROM dons d " +
                     "JOIN donneurs dn ON d.donneur_id = dn.id " +
                     "ORDER BY d.date_don DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Don don = extraireDon(rs);
                
                // Créer un objet Donneur simplifié
                Donneur donneur = new Donneur();
                donneur.setId(rs.getInt("donneur_id"));
                donneur.setNom(rs.getString("nom"));
                donneur.setPrenom(rs.getString("prenom"));
                donneur.setGroupeSanguin(rs.getString("groupe_sanguin"));
                don.setDonneur(donneur);
                
                dons.add(don);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la lecture des dons: " + e.getMessage());
            e.printStackTrace();
        }
        
        return dons;
    }
    
    /**
     * Recherche un don par ID
     */
    public Don trouverParId(int id) {
        String sql = "SELECT * FROM dons WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extraireDon(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Récupère les dons d'un donneur
     */
    public List<Don> trouverParDonneur(int donneurId) {
        List<Don> dons = new ArrayList<>();
        String sql = "SELECT * FROM dons WHERE donneur_id = ? ORDER BY date_don DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, donneurId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                dons.add(extraireDon(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return dons;
    }
    
    /**
     * Récupère les dons par statut
     */
    public List<Don> trouverParStatut(String statut) {
        List<Don> dons = new ArrayList<>();
        String sql = "SELECT d.*, dn.nom, dn.prenom, dn.groupe_sanguin " +
                     "FROM dons d " +
                     "JOIN donneurs dn ON d.donneur_id = dn.id " +
                     "WHERE d.statut = ? " +
                     "ORDER BY d.date_don DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, statut);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Don don = extraireDon(rs);
                
                Donneur donneur = new Donneur();
                donneur.setId(rs.getInt("donneur_id"));
                donneur.setNom(rs.getString("nom"));
                donneur.setPrenom(rs.getString("prenom"));
                donneur.setGroupeSanguin(rs.getString("groupe_sanguin"));
                don.setDonneur(donneur);
                
                dons.add(don);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return dons;
    }
    
    // ================================================================
    // UPDATE : Modifier un don
    // ================================================================
    
    public boolean modifier(Don don) {
        String sql = "UPDATE dons SET donneur_id=?, date_don=?, quantite=?, statut=?, notes=? WHERE id=?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, don.getDonneurId());
            pstmt.setTimestamp(2, new Timestamp(don.getDateDon().getTime()));
            pstmt.setInt(3, don.getQuantite());
            pstmt.setString(4, don.getStatut());
            pstmt.setString(5, don.getNotes());
            pstmt.setInt(6, don.getId());
            
            if (pstmt.executeUpdate() > 0) {
                actionLogDAO.logModification(ActionLog.ENTITE_DON, 
                    "Modification du don ID:" + don.getId());
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Valider un don (changer le statut à VALIDE)
     * 💡 IMPORTANT : Cette méthode ajoute aussi le sang au stock !
     */
    public boolean valider(int donId) {
        String sql = "UPDATE dons SET statut = 'VALIDE' WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, donId);
            if (pstmt.executeUpdate() > 0) {
                // Récupérer les infos du don pour créer le stock
                Don don = trouverParId(donId);
                if (don != null) {
                    // Récupérer le groupe sanguin du donneur
                    DonneurDAO donneurDAO = new DonneurDAO();
                    Donneur donneur = donneurDAO.trouverParId(don.getDonneurId());
                    
                    if (donneur != null) {
                        // Créer le stock sanguin
                        StockSanguin stock = new StockSanguin();
                        stock.setGroupeSanguin(donneur.getGroupeSanguin());
                        stock.setQuantite(don.getQuantite());
                        stock.setDatePrelevement(don.getDateDon());
                        
                        // Date de péremption = date du don + 42 jours
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(don.getDateDon());
                        cal.add(Calendar.DAY_OF_MONTH, 42);
                        stock.setDatePeremption(cal.getTime());
                        
                        stock.setDonId(donId);
                        stock.setStatut("DISPONIBLE");
                        
                        // Ajouter au stock
                        StockSanguinDAO stockDAO = new StockSanguinDAO();
                        stockDAO.ajouter(stock);
                        
                        System.out.println("✅ Stock ajouté: " + don.getQuantite() + "ml de " + donneur.getGroupeSanguin());
                    }
                }
                
                actionLogDAO.log(ActionLog.ACTION_VALIDER, ActionLog.ENTITE_DON, 
                    "Validation du don ID:" + donId + " - Stock créé");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Rejeter un don
     */
    public boolean rejeter(int donId, String raison) {
        String sql = "UPDATE dons SET statut = 'REJETE', notes = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, raison);
            pstmt.setInt(2, donId);
            if (pstmt.executeUpdate() > 0) {
                actionLogDAO.log(ActionLog.ACTION_REJETER, ActionLog.ENTITE_DON, 
                    "Rejet du don ID:" + donId + " - Raison: " + raison);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // ================================================================
    // DELETE : Supprimer un don
    // ================================================================
    
    public boolean supprimer(int id) {
        String sql = "DELETE FROM dons WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            if (pstmt.executeUpdate() > 0) {
                actionLogDAO.logSuppression(ActionLog.ENTITE_DON, 
                    "Suppression du don ID:" + id);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // ================================================================
    // STATISTIQUES
    // ================================================================
    
    /**
     * Compte le total des dons
     */
    public int compter() {
        String sql = "SELECT COUNT(*) FROM dons";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Compte les dons par statut
     */
    public int compterParStatut(String statut) {
        String sql = "SELECT COUNT(*) FROM dons WHERE statut = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, statut);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // ================================================================
    // MÉTHODE UTILITAIRE
    // ================================================================
    
    private Don extraireDon(ResultSet rs) throws SQLException {
        Don don = new Don();
        don.setId(rs.getInt("id"));
        don.setDonneurId(rs.getInt("donneur_id"));
        don.setDateDon(rs.getTimestamp("date_don"));
        don.setQuantite(rs.getInt("quantite"));
        don.setStatut(rs.getString("statut"));
        don.setNotes(rs.getString("notes"));
        return don;
    }
}
