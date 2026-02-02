/*
 * Package UI : Interfaces graphiques
 */
package ui;

import dao.*;
import model.GroupeSanguin;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * DashboardPanel : Tableau de bord principal
 * 
 * 💡 EXPLICATION :
 * C'est l'écran d'accueil qui affiche :
 * - Les statistiques générales
 * - L'état du stock par groupe sanguin
 * - Les alertes en cours
 * 
 * @author dteach
 */
public class DashboardPanel extends JPanel {
    
    // Labels pour les statistiques
    private JLabel lblTotalDonneurs;
    private JLabel lblTotalDons;
    private JLabel lblTotalStock;
    private JLabel lblAlertes;
    
    // Panel pour les stocks par groupe
    private JPanel stockGridPanel;
    
    // Panel pour les alertes
    private JPanel alertesPanel;
    
    // DAOs pour récupérer les données
    private DonneurDAO donneurDAO;
    private DonDAO donDAO;
    private StockSanguinDAO stockDAO;
    private AlerteDAO alerteDAO;

    public DashboardPanel() {
        initDAOs();
        initComponents();
        refreshData();
    }
    
    private void initDAOs() {
        donneurDAO = new DonneurDAO();
        donDAO = new DonDAO();
        stockDAO = new StockSanguinDAO();
        alerteDAO = new AlerteDAO();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 245, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // ========== TITRE ==========
        JLabel titleLabel = new JLabel("📊 Tableau de bord");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        // ========== CONTENU PRINCIPAL ==========
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setOpaque(false);
        
        // ----- Cartes statistiques (en haut) -----
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        
        lblTotalDonneurs = new JLabel("0");
        lblTotalDons = new JLabel("0");
        lblTotalStock = new JLabel("0 ml");
        lblAlertes = new JLabel("0");
        
        statsPanel.add(createStatCard("👥 Donneurs", lblTotalDonneurs, new Color(52, 152, 219)));
        statsPanel.add(createStatCard("💉 Dons", lblTotalDons, new Color(46, 204, 113)));
        statsPanel.add(createStatCard("🩸 Stock Total", lblTotalStock, new Color(155, 89, 182)));
        statsPanel.add(createStatCard("⚠️ Alertes", lblAlertes, new Color(231, 76, 60)));
        
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        
        // ----- Zone centrale : Stock par groupe + Alertes -----
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        centerPanel.setOpaque(false);
        
        // Stock par groupe sanguin
        JPanel stockSection = createSection("🩸 Stock par groupe sanguin");
        stockGridPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        stockGridPanel.setOpaque(false);
        stockSection.add(stockGridPanel, BorderLayout.CENTER);
        
        // Alertes récentes
        JPanel alertesSection = createSection("⚠️ Alertes récentes");
        alertesPanel = new JPanel();
        alertesPanel.setLayout(new BoxLayout(alertesPanel, BoxLayout.Y_AXIS));
        alertesPanel.setBackground(Color.WHITE);
        JScrollPane alertesScroll = new JScrollPane(alertesPanel);
        alertesScroll.setBorder(null);
        alertesSection.add(alertesScroll, BorderLayout.CENTER);
        
        centerPanel.add(stockSection);
        centerPanel.add(alertesSection);
        
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Crée une carte de statistique
     */
    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Arrondir les coins (effet visuel)
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLbl.setForeground(Color.WHITE);
        
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(Color.WHITE);
        
        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Crée une section avec titre
     */
    private JPanel createSection(String title) {
        JPanel section = new JPanel(new BorderLayout(10, 10));
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Arial", Font.BOLD, 16));
        section.add(titleLbl, BorderLayout.NORTH);
        
        return section;
    }
    
    /**
     * Crée une carte pour un groupe sanguin
     */
    private JPanel createBloodGroupCard(String groupe, int quantite) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 53, 69)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Couleur selon le niveau de stock
        Color bgColor;
        if (quantite == 0) {
            bgColor = new Color(220, 53, 69);  // Rouge - rupture
        } else if (quantite < 1000) {
            bgColor = new Color(255, 193, 7);  // Jaune - stock bas
        } else {
            bgColor = new Color(40, 167, 69);  // Vert - OK
        }
        card.setBackground(bgColor);
        
        JLabel lblGroupe = new JLabel(groupe, SwingConstants.CENTER);
        lblGroupe.setFont(new Font("Arial", Font.BOLD, 18));
        lblGroupe.setForeground(Color.WHITE);
        
        JLabel lblQuantite = new JLabel(quantite + " ml", SwingConstants.CENTER);
        lblQuantite.setFont(new Font("Arial", Font.PLAIN, 12));
        lblQuantite.setForeground(Color.WHITE);
        
        card.add(lblGroupe, BorderLayout.CENTER);
        card.add(lblQuantite, BorderLayout.SOUTH);
        
        return card;
    }
    
    /**
     * 💡 Rafraîchit toutes les données du dashboard
     * Appelé quand on navigue vers cet écran
     */
    public void refreshData() {
        try {
            // Statistiques générales
            int totalDonneurs = donneurDAO.compter();
            int totalDons = donDAO.compter();
            int alertesNonLues = alerteDAO.compterNonLues();
            
            lblTotalDonneurs.setText(String.valueOf(totalDonneurs));
            lblTotalDons.setText(String.valueOf(totalDons));
            lblAlertes.setText(String.valueOf(alertesNonLues));
            
            // Stock par groupe sanguin
            stockGridPanel.removeAll();
            int totalStock = 0;
            
            for (String groupe : GroupeSanguin.TOUS_LES_GROUPES) {
                int quantite = stockDAO.getQuantiteTotaleParGroupe(groupe);
                totalStock += quantite;
                stockGridPanel.add(createBloodGroupCard(groupe, quantite));
            }
            
            lblTotalStock.setText(totalStock + " ml");
            
            // Alertes récentes
            alertesPanel.removeAll();
            List<model.Alerte> alertes = alerteDAO.listerNonLues();
            
            if (alertes.isEmpty()) {
                JLabel noAlerte = new JLabel("✅ Aucune alerte");
                noAlerte.setFont(new Font("Arial", Font.ITALIC, 14));
                noAlerte.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                alertesPanel.add(noAlerte);
            } else {
                for (model.Alerte alerte : alertes) {
                    alertesPanel.add(createAlerteItem(alerte));
                }
            }
            
            // Rafraîchir l'affichage
            stockGridPanel.revalidate();
            stockGridPanel.repaint();
            alertesPanel.revalidate();
            alertesPanel.repaint();
            
        } catch (Exception e) {
            System.err.println("Erreur lors du rafraîchissement du dashboard: " + e.getMessage());
        }
    }
    
    /**
     * Crée un élément d'alerte
     */
    private JPanel createAlerteItem(model.Alerte alerte) {
        JPanel item = new JPanel(new BorderLayout(10, 5));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        // Couleur selon priorité
        Color prioriteColor;
        switch (alerte.getPriorite()) {
            case "CRITIQUE":
                prioriteColor = new Color(220, 53, 69);
                break;
            case "HAUTE":
                prioriteColor = new Color(255, 193, 7);
                break;
            default:
                prioriteColor = new Color(108, 117, 125);
        }
        item.setBackground(Color.WHITE);
        
        JLabel lblMessage = new JLabel(alerte.getMessage());
        lblMessage.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel lblPriorite = new JLabel("● " + alerte.getPriorite());
        lblPriorite.setFont(new Font("Arial", Font.BOLD, 10));
        lblPriorite.setForeground(prioriteColor);
        
        item.add(lblMessage, BorderLayout.CENTER);
        item.add(lblPriorite, BorderLayout.EAST);
        
        return item;
    }
}
