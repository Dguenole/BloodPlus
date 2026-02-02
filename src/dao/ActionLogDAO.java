/*
 * Package DAO
 */
package dao;

import model.ActionLog;
import service.Session;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ActionLogDAO : Gère l'enregistrement et la récupération des actions
 * 
 * 💡 EXPLICATION :
 * C'est le DAO qui s'occupe de sauvegarder toutes les actions dans la base
 * Les méthodes log() sont appelées depuis les autres DAO quand une action est effectuée
 * 
 * @author dteach
 */
public class ActionLogDAO {
    
    private Connection connection;
    
    public ActionLogDAO() {
        this.connection = DatabaseConnection.getConnection();
    }
    
    // ================================================================
    // MÉTHODES D'ENREGISTREMENT (LOGGING)
    // ================================================================
    
    /**
     * Enregistre une action dans le journal
     */
    public boolean enregistrer(ActionLog log) {
        String sql = "INSERT INTO actions_log (utilisateur_id, utilisateur_nom, action, entite, description) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, log.getUtilisateurId());
            pstmt.setString(2, log.getUtilisateurNom());
            pstmt.setString(3, log.getAction());
            pstmt.setString(4, log.getEntite());
            pstmt.setString(5, log.getDescription());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    log.setId(rs.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur enregistrement action: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Méthode raccourcie pour enregistrer une action
     * Utilise automatiquement l'utilisateur connecté
     */
    public void log(String action, String entite, String description) {
        Session session = Session.getInstance();
        if (session.estConnecte()) {
            ActionLog log = new ActionLog(
                session.getUtilisateurConnecte().getId(),
                session.getUtilisateurConnecte().getNomComplet(),
                action,
                entite,
                description
            );
            enregistrer(log);
            System.out.println("📝 LOG: " + log);
        }
    }
    
    /**
     * Raccourcis pour les actions courantes
     */
    public void logAjout(String entite, String description) {
        log(ActionLog.ACTION_AJOUTER, entite, description);
    }
    
    public void logModification(String entite, String description) {
        log(ActionLog.ACTION_MODIFIER, entite, description);
    }
    
    public void logSuppression(String entite, String description) {
        log(ActionLog.ACTION_SUPPRIMER, entite, description);
    }
    
    public void logConnexion() {
        log(ActionLog.ACTION_CONNEXION, ActionLog.ENTITE_SYSTEME, "Connexion à l'application");
    }
    
    public void logDeconnexion() {
        log(ActionLog.ACTION_DECONNEXION, ActionLog.ENTITE_SYSTEME, "Déconnexion de l'application");
    }
    
    // ================================================================
    // MÉTHODES DE RÉCUPÉRATION
    // ================================================================
    
    /**
     * Liste toutes les actions (les plus récentes en premier)
     */
    public List<ActionLog> listerTout(int limite) {
        List<ActionLog> actions = new ArrayList<>();
        String sql = "SELECT * FROM actions_log ORDER BY date_action DESC LIMIT ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limite);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                actions.add(extraireActionLog(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actions;
    }
    
    /**
     * Liste les actions d'un utilisateur spécifique
     */
    public List<ActionLog> listerParUtilisateur(int utilisateurId, int limite) {
        List<ActionLog> actions = new ArrayList<>();
        String sql = "SELECT * FROM actions_log WHERE utilisateur_id = ? ORDER BY date_action DESC LIMIT ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, utilisateurId);
            pstmt.setInt(2, limite);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                actions.add(extraireActionLog(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actions;
    }
    
    /**
     * Liste les actions d'aujourd'hui
     */
    public List<ActionLog> listerAujourdhui() {
        List<ActionLog> actions = new ArrayList<>();
        String sql = "SELECT * FROM actions_log WHERE DATE(date_action) = CURDATE() ORDER BY date_action DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                actions.add(extraireActionLog(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actions;
    }
    
    /**
     * Liste les actions par type d'entité
     */
    public List<ActionLog> listerParEntite(String entite, int limite) {
        List<ActionLog> actions = new ArrayList<>();
        String sql = "SELECT * FROM actions_log WHERE entite = ? ORDER BY date_action DESC LIMIT ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, entite);
            pstmt.setInt(2, limite);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                actions.add(extraireActionLog(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actions;
    }
    
    /**
     * Recherche dans les actions
     */
    public List<ActionLog> rechercher(String terme, int limite) {
        List<ActionLog> actions = new ArrayList<>();
        String sql = "SELECT * FROM actions_log WHERE " +
                     "utilisateur_nom LIKE ? OR action LIKE ? OR entite LIKE ? OR description LIKE ? " +
                     "ORDER BY date_action DESC LIMIT ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String pattern = "%" + terme + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);
            pstmt.setString(4, pattern);
            pstmt.setInt(5, limite);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                actions.add(extraireActionLog(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actions;
    }
    
    /**
     * Compte le nombre total d'actions
     */
    public int compterTotal() {
        String sql = "SELECT COUNT(*) FROM actions_log";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Compte les actions d'aujourd'hui
     */
    public int compterAujourdhui() {
        String sql = "SELECT COUNT(*) FROM actions_log WHERE DATE(date_action) = CURDATE()";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Supprime les anciennes actions (nettoyage)
     */
    public int supprimerAnciennes(int joursConservation) {
        String sql = "DELETE FROM actions_log WHERE date_action < DATE_SUB(NOW(), INTERVAL ? DAY)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, joursConservation);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private ActionLog extraireActionLog(ResultSet rs) throws SQLException {
        ActionLog log = new ActionLog();
        log.setId(rs.getInt("id"));
        log.setUtilisateurId(rs.getInt("utilisateur_id"));
        log.setUtilisateurNom(rs.getString("utilisateur_nom"));
        log.setAction(rs.getString("action"));
        log.setEntite(rs.getString("entite"));
        log.setDescription(rs.getString("description"));
        log.setDateAction(rs.getTimestamp("date_action"));
        return log;
    }
}
