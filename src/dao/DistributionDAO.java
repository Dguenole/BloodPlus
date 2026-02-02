/*
 * Package DAO : Data Access Object
 */
package dao;

import model.ActionLog;
import model.Distribution;
import model.Hopital;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DistributionDAO : gère les distributions de sang aux hôpitaux
 * 
 * @author dteach
 */
public class DistributionDAO {
    
    private Connection connection;
    private ActionLogDAO actionLogDAO;
    private StockSanguinDAO stockDAO;
    
    public DistributionDAO() {
        this.connection = DatabaseConnection.getConnection();
        this.actionLogDAO = new ActionLogDAO();
        this.stockDAO = new StockSanguinDAO();
    }
    
    // ================================================================
    // CREATE
    // ================================================================
    
    public boolean ajouter(Distribution distribution) {
        String sql = "INSERT INTO distributions (hopital_id, groupe_sanguin, quantite, statut, motif) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, distribution.getHopitalId());
            pstmt.setString(2, distribution.getGroupeSanguin());
            pstmt.setInt(3, distribution.getQuantite());
            pstmt.setString(4, distribution.getStatut());
            pstmt.setString(5, distribution.getMotif());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    distribution.setId(rs.getInt(1));
                }
                
                // ⚡ DIMINUER LE STOCK AUTOMATIQUEMENT
                stockDAO.diminuerStock(distribution.getGroupeSanguin(), distribution.getQuantite());
                
                System.out.println("✅ Distribution enregistrée: " + distribution.getQuantite() + "ml de " + distribution.getGroupeSanguin());
                
                // Enregistrer l'action
                actionLogDAO.logAjout(ActionLog.ENTITE_DISTRIBUTION, 
                    "Distribution de " + distribution.getQuantite() + "ml de " + distribution.getGroupeSanguin());
                
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
    
    public List<Distribution> listerTous() {
        List<Distribution> distributions = new ArrayList<>();
        String sql = "SELECT d.*, h.nom as hopital_nom, h.ville as hopital_ville " +
                     "FROM distributions d " +
                     "JOIN hopitaux h ON d.hopital_id = h.id " +
                     "ORDER BY d.date_distribution DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Distribution dist = extraireDistribution(rs);
                
                // Ajouter les infos de l'hôpital
                Hopital hopital = new Hopital();
                hopital.setId(rs.getInt("hopital_id"));
                hopital.setNom(rs.getString("hopital_nom"));
                hopital.setVille(rs.getString("hopital_ville"));
                dist.setHopital(hopital);
                
                distributions.add(dist);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return distributions;
    }
    
    public Distribution trouverParId(int id) {
        String sql = "SELECT * FROM distributions WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extraireDistribution(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Distribution> trouverParHopital(int hopitalId) {
        List<Distribution> distributions = new ArrayList<>();
        String sql = "SELECT * FROM distributions WHERE hopital_id = ? ORDER BY date_distribution DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, hopitalId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                distributions.add(extraireDistribution(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return distributions;
    }
    
    public List<Distribution> trouverParStatut(String statut) {
        List<Distribution> distributions = new ArrayList<>();
        String sql = "SELECT d.*, h.nom as hopital_nom " +
                     "FROM distributions d " +
                     "JOIN hopitaux h ON d.hopital_id = h.id " +
                     "WHERE d.statut = ? ORDER BY d.date_distribution DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, statut);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Distribution dist = extraireDistribution(rs);
                Hopital hopital = new Hopital();
                hopital.setNom(rs.getString("hopital_nom"));
                dist.setHopital(hopital);
                distributions.add(dist);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return distributions;
    }
    
    // ================================================================
    // UPDATE
    // ================================================================
    
    public boolean modifier(Distribution distribution) {
        String sql = "UPDATE distributions SET hopital_id=?, groupe_sanguin=?, quantite=?, statut=?, motif=? WHERE id=?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, distribution.getHopitalId());
            pstmt.setString(2, distribution.getGroupeSanguin());
            pstmt.setInt(3, distribution.getQuantite());
            pstmt.setString(4, distribution.getStatut());
            pstmt.setString(5, distribution.getMotif());
            pstmt.setInt(6, distribution.getId());
            
            if (pstmt.executeUpdate() > 0) {
                actionLogDAO.logModification(ActionLog.ENTITE_DISTRIBUTION, 
                    "Modification de la distribution ID:" + distribution.getId());
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean marquerLivree(int distributionId) {
        String sql = "UPDATE distributions SET statut = 'LIVREE' WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, distributionId);
            if (pstmt.executeUpdate() > 0) {
                actionLogDAO.log(ActionLog.ACTION_LIVRER, ActionLog.ENTITE_DISTRIBUTION, 
                    "Distribution ID:" + distributionId + " marquée comme livrée");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean annuler(int distributionId) {
        String sql = "UPDATE distributions SET statut = 'ANNULEE' WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, distributionId);
            if (pstmt.executeUpdate() > 0) {
                actionLogDAO.log(ActionLog.ACTION_ANNULER, ActionLog.ENTITE_DISTRIBUTION, 
                    "Annulation de la distribution ID:" + distributionId);
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
        String sql = "DELETE FROM distributions WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            if (pstmt.executeUpdate() > 0) {
                actionLogDAO.logSuppression(ActionLog.ENTITE_DISTRIBUTION, 
                    "Suppression de la distribution ID:" + id);
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
    
    public int compter() {
        String sql = "SELECT COUNT(*) FROM distributions";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Total distribué par groupe sanguin
     */
    public int getTotalDistribueParGroupe(String groupeSanguin) {
        String sql = "SELECT COALESCE(SUM(quantite), 0) FROM distributions WHERE groupe_sanguin = ? AND statut = 'LIVREE'";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, groupeSanguin);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private Distribution extraireDistribution(ResultSet rs) throws SQLException {
        Distribution dist = new Distribution();
        dist.setId(rs.getInt("id"));
        dist.setHopitalId(rs.getInt("hopital_id"));
        dist.setGroupeSanguin(rs.getString("groupe_sanguin"));
        dist.setQuantite(rs.getInt("quantite"));
        dist.setDateDistribution(rs.getTimestamp("date_distribution"));
        dist.setStatut(rs.getString("statut"));
        dist.setMotif(rs.getString("motif"));
        return dist;
    }
}
