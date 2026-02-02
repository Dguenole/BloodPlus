/*
 * Package UI : Interfaces graphiques
 */
package ui;

import dao.ActionLogDAO;
import model.Utilisateur;
import service.Session;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * MainFrame : Fenêtre principale de l'application BloodPlus
 * 
 * 💡 EXPLICATION :
 * - JFrame = la fenêtre principale
 * - JMenuBar = la barre de menu en haut
 * - JPanel = conteneur pour organiser les composants
 * - CardLayout = permet de switcher entre différents écrans
 * 
 * @author dteach
 */
public class MainFrame extends JFrame {
    
    // Instance statique pour accès global (rafraîchissement dashboard)
    private static MainFrame instance;
    
    // ============ COMPOSANTS ============
    private JPanel mainPanel;           // Panel principal avec CardLayout
    private CardLayout cardLayout;      // Pour changer d'écran
    
    // Les différents écrans (panels)
    private DashboardPanel dashboardPanel;
    private DonneurPanel donneurPanel;
    private DonPanel donPanel;
    private StockPanel stockPanel;
    private HopitalPanel hopitalPanel;
    private DistributionPanel distributionPanel;
    private UtilisateurPanel utilisateurPanel;
    private HistoriquePanel historiquePanel;
    
    // Barre latérale
    private JPanel sidebarPanel;
    
    // Boutons admin (pour contrôle visibilité)
    private JButton btnUtilisateurs;
    private JButton btnHistorique;
    
    // Constantes pour les noms des cartes (écrans)
    private static final String DASHBOARD = "DASHBOARD";
    private static final String DONNEURS = "DONNEURS";
    private static final String DONS = "DONS";
    private static final String STOCK = "STOCK";
    private static final String HOPITAUX = "HOPITAUX";
    private static final String DISTRIBUTIONS = "DISTRIBUTIONS";
    private static final String UTILISATEURS = "UTILISATEURS";
    private static final String HISTORIQUE = "HISTORIQUE";

    /**
     * Constructeur : initialise la fenêtre
     */
    public MainFrame() {
        instance = this;  // Sauvegarder l'instance pour accès global
        initComponents();
        setupWindow();
    }
    
    /**
     * Retourne l'instance du MainFrame (pour rafraîchir le dashboard)
     */
    public static MainFrame getInstance() {
        return instance;
    }
    
    /**
     * Rafraîchit le dashboard (appelé après une modification de données)
     */
    public void refreshDashboard() {
        if (dashboardPanel != null) {
            dashboardPanel.refreshData();
        }
    }
    
    /**
     * Configure les propriétés de la fenêtre
     */
    private void setupWindow() {
        setTitle("🩸 BloodPlus - Gestion de Banque de Sang");
        setSize(1200, 700);
        setMinimumSize(new Dimension(1000, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    /**
     * Initialise tous les composants de l'interface
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // 1. Créer la barre de menu
        setJMenuBar(createMenuBar());
        
        // 2. Créer la barre latérale (navigation)
        sidebarPanel = createSidebar();
        add(sidebarPanel, BorderLayout.WEST);
        
        // 3. Créer le panel principal avec CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color(245, 245, 250));
        
        // 4. Créer les différents écrans
        dashboardPanel = new DashboardPanel();
        donneurPanel = new DonneurPanel();
        donPanel = new DonPanel();
        stockPanel = new StockPanel();
        hopitalPanel = new HopitalPanel();
        distributionPanel = new DistributionPanel();
        utilisateurPanel = new UtilisateurPanel();
        historiquePanel = new HistoriquePanel();
        
        // 5. Ajouter les écrans au CardLayout
        mainPanel.add(dashboardPanel, DASHBOARD);
        mainPanel.add(donneurPanel, DONNEURS);
        mainPanel.add(donPanel, DONS);
        mainPanel.add(stockPanel, STOCK);
        mainPanel.add(hopitalPanel, HOPITAUX);
        mainPanel.add(distributionPanel, DISTRIBUTIONS);
        mainPanel.add(utilisateurPanel, UTILISATEURS);
        mainPanel.add(historiquePanel, HISTORIQUE);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Afficher le dashboard par défaut
        cardLayout.show(mainPanel, DASHBOARD);
        
        // 6. Appliquer les permissions
        appliquerPermissions();
    }
    
    /**
     * Applique les permissions selon le rôle
     */
    private void appliquerPermissions() {
        Session session = Session.getInstance();
        if (btnUtilisateurs != null) {
            btnUtilisateurs.setVisible(session.estAdmin());
        }
        if (btnHistorique != null) {
            btnHistorique.setVisible(session.estAdmin());
        }
    }
    
    /**
     * Crée la barre de menu
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(220, 53, 69));
        
        // Menu Fichier
        JMenu menuFichier = new JMenu("Fichier");
        JMenuItem itemQuitter = new JMenuItem("Quitter");
        itemQuitter.addActionListener(e -> System.exit(0));
        menuFichier.add(itemQuitter);
        
        // Menu Aide
        JMenu menuAide = new JMenu("Aide");
        JMenuItem itemAPropos = new JMenuItem("À propos");
        itemAPropos.addActionListener(e -> showAbout());
        menuAide.add(itemAPropos);
        
        menuBar.add(menuFichier);
        menuBar.add(menuAide);
        
        return menuBar;
    }
    
    /**
     * Crée la barre latérale de navigation
     */
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(52, 58, 64));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Logo / Titre
        JLabel titleLabel = new JLabel("🩸 BloodPlus");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(titleLabel);
        sidebar.add(Box.createVerticalStrut(10));
        
        // Infos utilisateur connecté
        Session session = Session.getInstance();
        Utilisateur user = session.getUtilisateurConnecte();
        
        if (user != null) {
            JPanel userInfoPanel = new JPanel();
            userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
            userInfoPanel.setBackground(new Color(73, 80, 87));
            userInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            userInfoPanel.setMaximumSize(new Dimension(180, 60));
            userInfoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel lblNom = new JLabel(user.getNomComplet());
            lblNom.setFont(new Font("Arial", Font.BOLD, 12));
            lblNom.setForeground(Color.WHITE);
            lblNom.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel lblRole = new JLabel(user.getRoleEmoji() + " " + user.getRole());
            lblRole.setFont(new Font("Arial", Font.PLAIN, 11));
            lblRole.setForeground(new Color(200, 200, 200));
            lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            userInfoPanel.add(lblNom);
            userInfoPanel.add(Box.createVerticalStrut(3));
            userInfoPanel.add(lblRole);
            
            sidebar.add(userInfoPanel);
        }
        
        sidebar.add(Box.createVerticalStrut(20));
        
        // Boutons de navigation
        sidebar.add(createNavButton("📊 Tableau de bord", DASHBOARD));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createNavButton("👥 Donneurs", DONNEURS));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createNavButton("💉 Dons", DONS));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createNavButton("🩸 Stock Sanguin", STOCK));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createNavButton("🏥 Hôpitaux", HOPITAUX));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createNavButton("📦 Distributions", DISTRIBUTIONS));
        
        // Bouton Utilisateurs (visible seulement pour ADMIN)
        sidebar.add(Box.createVerticalStrut(5));
        btnUtilisateurs = createNavButton("⚙️ Utilisateurs", UTILISATEURS);
        sidebar.add(btnUtilisateurs);
        
        // Bouton Historique (visible seulement pour ADMIN)
        sidebar.add(Box.createVerticalStrut(5));
        btnHistorique = createNavButton("📝 Historique", HISTORIQUE);
        sidebar.add(btnHistorique);
        
        // Espace flexible
        sidebar.add(Box.createVerticalGlue());
        
        // Bouton déconnexion en bas
        JButton btnLogout = createNavButton("🚪 Déconnexion", null);
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Voulez-vous vraiment vous déconnecter ?", "Confirmation", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Enregistrer la déconnexion avant de fermer la session
                new ActionLogDAO().logDeconnexion();
                Session.getInstance().deconnecter();
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        sidebar.add(btnLogout);
        
        return sidebar;
    }
    
    /**
     * Crée un bouton de navigation stylisé
     */
    private JButton createNavButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(180, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(73, 80, 87));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effet hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(220, 53, 69));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(73, 80, 87));
            }
        });
        
        // Action : changer d'écran
        if (cardName != null) {
            button.addActionListener(e -> {
                cardLayout.show(mainPanel, cardName);
                refreshPanel(cardName);
            });
        }
        
        return button;
    }
    
    /**
     * Rafraîchit les données du panel affiché
     */
    private void refreshPanel(String panelName) {
        switch (panelName) {
            case DASHBOARD:
                dashboardPanel.refreshData();
                break;
            case DONNEURS:
                donneurPanel.refreshData();
                break;
            case DONS:
                donPanel.refreshData();
                break;
            case STOCK:
                stockPanel.refreshData();
                break;
            case HOPITAUX:
                hopitalPanel.refreshData();
                break;
            case DISTRIBUTIONS:
                distributionPanel.refreshData();
                break;
            case UTILISATEURS:
                utilisateurPanel.chargerDonnees();
                break;
            case HISTORIQUE:
                historiquePanel.chargerDonnees();
                break;
        }
    }
    
    /**
     * Affiche la boîte "À propos"
     */
    private void showAbout() {
        JOptionPane.showMessageDialog(this,
            "🩸 BloodPlus v1.0\n\n" +
            "Application de Gestion de Banque de Sang\n" +
            "Projet scolaire - 2026\n\n" +
            "Développé avec ❤️ en Java",
            "À propos",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Point d'entrée pour tester MainFrame directement
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
