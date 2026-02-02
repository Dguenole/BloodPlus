/*
 * Package DAO : Data Access Object
 */
package dao;

import model.ActionLog;
import model.Donneur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DonneurDAO : gère toutes les opérations sur les donneurs en base de données
 * 
 * 💡 EXPLICATION DU PATTERN DAO :
 * - Le DAO sépare la logique métier de l'accès aux données
 * - Chaque méthode correspond à une opération : CRUD
 *   C = Create (ajouter)
 *   R = Read (lire/chercher)
 *   U = Update (modifier)
 *   D = Delete (supprimer)
 * 
 * @author dteach
 */
public class DonneurDAO {
    
    // Connexion à la base de données
    private Connection connection;
    private ActionLogDAO actionLogDAO;
    
    /**
     * Constructeur : récupère la connexion
     */
    public DonneurDAO() {
        this.connection = DatabaseConnection.getConnection();
        this.actionLogDAO = new ActionLogDAO();
    }
    
    // ================================================================
    // CREATE : Ajouter un nouveau donneur
    // ================================================================
    
    /**
     * Ajoute un nouveau donneur dans la base de données
     * 
     * 💡 PreparedStatement : protège contre les injections SQL
     *    On utilise des ? comme placeholders, puis on remplit avec setString, setDate, etc.
     * 
     * @param donneur Le donneur à ajouter
     * @return true si l'ajout a réussi
     */
    public boolean ajouter(Donneur donneur) {
        String sql = "INSERT INTO donneurs (nom, prenom, date_naissance, sexe, groupe_sanguin, telephone, email, adresse, apte) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Remplir les ? avec les valeurs du donneur
            pstmt.setString(1, donneur.getNom());
            pstmt.setString(2, donneur.getPrenom());
            
            // Conversion java.util.Date vers java.sql.Date
            if (donneur.getDateNaissance() != null) {
                pstmt.setDate(3, new java.sql.Date(donneur.getDateNaissance().getTime()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }
            
            pstmt.setString(4, donneur.getSexe());
            pstmt.setString(5, donneur.getGroupeSanguin());
            pstmt.setString(6, donneur.getTelephone());
            pstmt.setString(7, donneur.getEmail());
            pstmt.setString(8, donneur.getAdresse());
            pstmt.setBoolean(9, donneur.isApte());
            
            // Exécuter la requête
            int rowsAffected = pstmt.executeUpdate();
            
            // Récupérer l'ID généré automatiquement
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    donneur.setId(rs.getInt(1));
                }
                System.out.println("✅ Donneur ajouté avec ID: " + donneur.getId());
                
                // Enregistrer l'action
                actionLogDAO.logAjout(ActionLog.ENTITE_DONNEUR, 
                    "Ajout du donneur: " + donneur.getNomComplet() + " (" + donneur.getGroupeSanguin() + ")");
                
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout du donneur: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // ================================================================
    // READ : Lire / Rechercher des donneurs
    // ================================================================
    
    /**
     * Récupère tous les donneurs de la base
     * 
     * @return Liste de tous les donneurs
     */
    public List<Donneur> listerTous() {
        List<Donneur> donneurs = new ArrayList<>();
        String sql = "SELECT * FROM donneurs ORDER BY nom, prenom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            // Parcourir les résultats
            while (rs.next()) {
                Donneur donneur = extraireDonneur(rs);
                donneurs.add(donneur);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la lecture des donneurs: " + e.getMessage());
            e.printStackTrace();
        }
        
        return donneurs;
    }
    
    /**
     * Recherche un donneur par son ID
     * 
     * @param id L'identifiant du donneur
     * @return Le donneur trouvé ou null
     */
    public Donneur trouverParId(int id) {
        String sql = "SELECT * FROM donneurs WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extraireDonneur(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche du donneur: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Recherche des donneurs par groupe sanguin
     * 
     * @param groupeSanguin Le groupe sanguin recherché (ex: "A+", "O-")
     * @return Liste des donneurs de ce groupe
     */
    public List<Donneur> trouverParGroupeSanguin(String groupeSanguin) {
        List<Donneur> donneurs = new ArrayList<>();
        String sql = "SELECT * FROM donneurs WHERE groupe_sanguin = ? AND apte = true";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, groupeSanguin);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                donneurs.add(extraireDonneur(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche par groupe: " + e.getMessage());
            e.printStackTrace();
        }
        
        return donneurs;
    }
    
    /**
     * Recherche des donneurs par nom ou prénom
     * 
     * @param recherche Le texte à rechercher
     * @return Liste des donneurs correspondants
     */
    public List<Donneur> rechercher(String recherche) {
        List<Donneur> donneurs = new ArrayList<>();
        String sql = "SELECT * FROM donneurs WHERE nom LIKE ? OR prenom LIKE ? ORDER BY nom, prenom";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String pattern = "%" + recherche + "%";  // % = n'importe quels caractères
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                donneurs.add(extraireDonneur(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche: " + e.getMessage());
            e.printStackTrace();
        }
        
        return donneurs;
    }
    
    // ================================================================
    // UPDATE : Modifier un donneur
    // ================================================================
    
    /**
     * Met à jour les informations d'un donneur
     * 
     * @param donneur Le donneur avec les nouvelles informations
     * @return true si la mise à jour a réussi
     */
    public boolean modifier(Donneur donneur) {
        String sql = "UPDATE donneurs SET nom=?, prenom=?, date_naissance=?, sexe=?, groupe_sanguin=?, telephone=?, email=?, adresse=?, apte=? WHERE id=?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setString(1, donneur.getNom());
            pstmt.setString(2, donneur.getPrenom());
            
            if (donneur.getDateNaissance() != null) {
                pstmt.setDate(3, new java.sql.Date(donneur.getDateNaissance().getTime()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }
            
            pstmt.setString(4, donneur.getSexe());
            pstmt.setString(5, donneur.getGroupeSanguin());
            pstmt.setString(6, donneur.getTelephone());
            pstmt.setString(7, donneur.getEmail());
            pstmt.setString(8, donneur.getAdresse());
            pstmt.setBoolean(9, donneur.isApte());
            pstmt.setInt(10, donneur.getId());  // WHERE id = ?
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Donneur modifié: " + donneur.getNomComplet());
                
                // Enregistrer l'action
                actionLogDAO.logModification(ActionLog.ENTITE_DONNEUR, 
                    "Modification du donneur: " + donneur.getNomComplet());
                
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // ================================================================
    // DELETE : Supprimer un donneur
    // ================================================================
    
    /**
     * Supprime un donneur de la base de données
     * 
     * @param id L'identifiant du donneur à supprimer
     * @return true si la suppression a réussi
     */
    public boolean supprimer(int id) {
        // Récupérer le donneur avant suppression pour le log
        Donneur donneur = trouverParId(id);
        String nomDonneur = donneur != null ? donneur.getNomComplet() : "ID:" + id;
        
        String sql = "DELETE FROM donneurs WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Donneur supprimé (ID: " + id + ")");
                
                // Enregistrer l'action
                actionLogDAO.logSuppression(ActionLog.ENTITE_DONNEUR, 
                    "Suppression du donneur: " + nomDonneur);
                
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // ================================================================
    // MÉTHODES UTILITAIRES
    // ================================================================
    
    /**
     * Compte le nombre total de donneurs
     */
    public int compter() {
        String sql = "SELECT COUNT(*) FROM donneurs";
        
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
     * Compte les donneurs par groupe sanguin
     */
    public int compterParGroupe(String groupeSanguin) {
        String sql = "SELECT COUNT(*) FROM donneurs WHERE groupe_sanguin = ? AND apte = true";
        
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
     * 💡 Méthode privée : extrait un Donneur d'un ResultSet
     * Évite de répéter le même code dans chaque méthode
     */
    private Donneur extraireDonneur(ResultSet rs) throws SQLException {
        Donneur donneur = new Donneur();
        donneur.setId(rs.getInt("id"));
        donneur.setNom(rs.getString("nom"));
        donneur.setPrenom(rs.getString("prenom"));
        donneur.setDateNaissance(rs.getDate("date_naissance"));
        donneur.setSexe(rs.getString("sexe"));
        donneur.setGroupeSanguin(rs.getString("groupe_sanguin"));
        donneur.setTelephone(rs.getString("telephone"));
        donneur.setEmail(rs.getString("email"));
        donneur.setAdresse(rs.getString("adresse"));
        donneur.setDateInscription(rs.getTimestamp("date_inscription"));
        donneur.setApte(rs.getBoolean("apte"));
        return donneur;
    }
}
