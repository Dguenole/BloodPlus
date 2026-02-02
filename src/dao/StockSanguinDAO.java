/*
 * Package DAO : Data Access Object
 */
package dao;

import model.ActionLog;
import model.StockSanguin;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * StockSanguinDAO : gère les opérations sur le stock de sang
 * 
 * 💡 C'est l'un des DAO les plus importants car il gère le cœur de l'application :
 *    le stock de sang disponible
 * 
 * @author dteach
 */
public class StockSanguinDAO {
    
    private Connection connection;
    private ActionLogDAO actionLogDAO;
    
    public StockSanguinDAO() {
        this.connection = DatabaseConnection.getConnection();
        this.actionLogDAO = new ActionLogDAO();
    }
    
    // ================================================================
    // CREATE
    // ================================================================
    
    public boolean ajouter(StockSanguin stock) {
        String sql = "INSERT INTO stocks_sanguins (groupe_sanguin, quantite, date_prelevement, date_peremption, don_id, statut) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, stock.getGroupeSanguin());
            pstmt.setInt(2, stock.getQuantite());
            pstmt.setDate(3, new java.sql.Date(stock.getDatePrelevement().getTime()));
            pstmt.setDate(4, new java.sql.Date(stock.getDatePeremption().getTime()));
            
            if (stock.getDonId() > 0) {
                pstmt.setInt(5, stock.getDonId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            
            pstmt.setString(6, stock.getStatut());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    stock.setId(rs.getInt(1));
                }
                
                // Enregistrer l'action
                actionLogDAO.logAjout(ActionLog.ENTITE_STOCK, 
                    "Ajout au stock: " + stock.getQuantite() + "ml de " + stock.getGroupeSanguin());
                
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
     * Récupère tout le stock disponible
     */
    public List<StockSanguin> listerDisponible() {
        List<StockSanguin> stocks = new ArrayList<>();
        String sql = "SELECT * FROM stocks_sanguins WHERE statut = 'DISPONIBLE' AND date_peremption >= CURDATE() ORDER BY date_peremption ASC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                stocks.add(extraireStock(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stocks;
    }
    
    /**
     * Récupère tout le stock (tous statuts)
     */
    public List<StockSanguin> listerTous() {
        List<StockSanguin> stocks = new ArrayList<>();
        String sql = "SELECT * FROM stocks_sanguins ORDER BY date_peremption ASC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                stocks.add(extraireStock(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stocks;
    }
    
    /**
     * Récupère le stock par groupe sanguin
     */
    public List<StockSanguin> trouverParGroupe(String groupeSanguin) {
        List<StockSanguin> stocks = new ArrayList<>();
        String sql = "SELECT * FROM stocks_sanguins WHERE groupe_sanguin = ? AND statut = 'DISPONIBLE' AND date_peremption >= CURDATE()";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, groupeSanguin);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                stocks.add(extraireStock(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stocks;
    }
    
    /**
     * 💡 IMPORTANT : Calcule la quantité totale disponible par groupe sanguin
     */
    public int getQuantiteTotaleParGroupe(String groupeSanguin) {
        String sql = "SELECT COALESCE(SUM(quantite), 0) FROM stocks_sanguins WHERE groupe_sanguin = ? AND statut = 'DISPONIBLE' AND date_peremption >= CURDATE()";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, groupeSanguin);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Récupère le résumé du stock par groupe
     * Retourne un tableau : [groupe, quantité totale]
     */
    public List<Object[]> getResumeStock() {
        List<Object[]> resume = new ArrayList<>();
        String sql = "SELECT groupe_sanguin, COALESCE(SUM(quantite), 0) as total " +
                     "FROM stocks_sanguins " +
                     "WHERE statut = 'DISPONIBLE' AND date_peremption >= CURDATE() " +
                     "GROUP BY groupe_sanguin " +
                     "ORDER BY groupe_sanguin";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] ligne = new Object[2];
                ligne[0] = rs.getString("groupe_sanguin");
                ligne[1] = rs.getInt("total");
                resume.add(ligne);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resume;
    }
    
    /**
     * Récupère les stocks qui vont bientôt périmer (dans les X jours)
     */
    public List<StockSanguin> getStocksProchesPeremption(int joursAvant) {
        List<StockSanguin> stocks = new ArrayList<>();
        String sql = "SELECT * FROM stocks_sanguins " +
                     "WHERE statut = 'DISPONIBLE' " +
                     "AND date_peremption BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
                     "ORDER BY date_peremption ASC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, joursAvant);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                stocks.add(extraireStock(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stocks;
    }
    
    // ================================================================
    // UPDATE
    // ================================================================
    
    public boolean modifier(StockSanguin stock) {
        String sql = "UPDATE stocks_sanguins SET groupe_sanguin=?, quantite=?, date_peremption=?, statut=? WHERE id=?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, stock.getGroupeSanguin());
            pstmt.setInt(2, stock.getQuantite());
            pstmt.setDate(3, new java.sql.Date(stock.getDatePeremption().getTime()));
            pstmt.setString(4, stock.getStatut());
            pstmt.setInt(5, stock.getId());
            
            if (pstmt.executeUpdate() > 0) {
                actionLogDAO.logModification(ActionLog.ENTITE_STOCK, 
                    "Modification du stock ID:" + stock.getId() + " (" + stock.getGroupeSanguin() + ")");
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Marquer un stock comme utilisé
     */
    public boolean marquerUtilise(int stockId) {
        String sql = "UPDATE stocks_sanguins SET statut = 'UTILISE' WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, stockId);
            if (pstmt.executeUpdate() > 0) {
                actionLogDAO.log(ActionLog.ACTION_UTILISER, ActionLog.ENTITE_STOCK, 
                    "Stock ID:" + stockId + " marqué comme utilisé");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Marquer les stocks périmés automatiquement
     */
    public int marquerPerimes() {
        String sql = "UPDATE stocks_sanguins SET statut = 'PERIME' WHERE date_peremption < CURDATE() AND statut = 'DISPONIBLE'";
        
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
        String sql = "DELETE FROM stocks_sanguins WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            if (pstmt.executeUpdate() > 0) {
                actionLogDAO.logSuppression(ActionLog.ENTITE_STOCK, 
                    "Suppression du stock ID:" + id);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // ================================================================
    // DISTRIBUTION - Diminuer le stock
    // ================================================================
    
    /**
     * Diminue le stock d'un groupe sanguin pour une distribution
     * @param groupeSanguin le groupe sanguin
     * @param quantite la quantité à soustraire en ml
     * @return true si réussi
     */
    public boolean diminuerStock(String groupeSanguin, int quantite) {
        // Trouver les poches disponibles de ce groupe, les plus anciennes d'abord
        String sqlSelect = "SELECT id, quantite FROM stocks_sanguins " +
                          "WHERE groupe_sanguin = ? AND statut = 'DISPONIBLE' " +
                          "ORDER BY date_peremption ASC";
        
        int resteADistribuer = quantite;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sqlSelect)) {
            pstmt.setString(1, groupeSanguin);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next() && resteADistribuer > 0) {
                int stockId = rs.getInt("id");
                int stockQuantite = rs.getInt("quantite");
                
                if (stockQuantite <= resteADistribuer) {
                    // Cette poche est entièrement utilisée
                    marquerUtilise(stockId);
                    resteADistribuer -= stockQuantite;
                } else {
                    // Diminuer partiellement cette poche
                    int nouvelleQuantite = stockQuantite - resteADistribuer;
                    String sqlUpdate = "UPDATE stocks_sanguins SET quantite = ? WHERE id = ?";
                    try (PreparedStatement pstmtUpdate = connection.prepareStatement(sqlUpdate)) {
                        pstmtUpdate.setInt(1, nouvelleQuantite);
                        pstmtUpdate.setInt(2, stockId);
                        pstmtUpdate.executeUpdate();
                    }
                    resteADistribuer = 0;
                }
            }
            
            if (resteADistribuer == 0) {
                actionLogDAO.log(ActionLog.ACTION_UTILISER, ActionLog.ENTITE_STOCK, 
                    "Distribution de " + quantite + "ml de " + groupeSanguin);
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // ================================================================
    // MÉTHODE UTILITAIRE
    // ================================================================
    
    private StockSanguin extraireStock(ResultSet rs) throws SQLException {
        StockSanguin stock = new StockSanguin();
        stock.setId(rs.getInt("id"));
        stock.setGroupeSanguin(rs.getString("groupe_sanguin"));
        stock.setQuantite(rs.getInt("quantite"));
        stock.setDatePrelevement(rs.getDate("date_prelevement"));
        stock.setDatePeremption(rs.getDate("date_peremption"));
        stock.setDonId(rs.getInt("don_id"));
        stock.setStatut(rs.getString("statut"));
        return stock;
    }
}
