/*
 * Package UI
 */
package ui;

import dao.ActionLogDAO;
import dao.UtilisateurDAO;
import model.ActionLog;
import model.Utilisateur;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * HistoriquePanel : Affiche l'historique des actions des utilisateurs
 * (Réservé ADMIN)
 * 
 * @author dteach
 */
public class HistoriquePanel extends JPanel {
    
    private JTable table;
    private DefaultTableModel tableModel;
    private ActionLogDAO actionLogDAO;
    private UtilisateurDAO utilisateurDAO;
    
    private JComboBox<String> cmbFiltre;
    private JComboBox<UtilisateurItem> cmbUtilisateur;
    private JTextField txtRecherche;
    private JSpinner spnLimite;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public HistoriquePanel() {
        actionLogDAO = new ActionLogDAO();
        utilisateurDAO = new UtilisateurDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(248, 249, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initComponents();
        chargerDonnees();
    }
    
    private void initComponents() {
        // ========== HEADER ==========
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel lblTitre = new JLabel("📋 Historique des Actions");
        lblTitre.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel lblInfo = new JLabel("Visualisez toutes les actions effectuées par les utilisateurs");
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 12));
        lblInfo.setForeground(Color.GRAY);
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(lblTitre);
        titlePanel.add(lblInfo);
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        // Stats rapides
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statsPanel.setOpaque(false);
        
        int totalActions = actionLogDAO.compterTotal();
        int actionsAujourdhui = actionLogDAO.compterAujourdhui();
        
        JLabel lblStats = new JLabel("📊 Total: " + totalActions + " | Aujourd'hui: " + actionsAujourdhui);
        lblStats.setFont(new Font("Arial", Font.BOLD, 12));
        lblStats.setForeground(new Color(0, 123, 255));
        statsPanel.add(lblStats);
        
        headerPanel.add(statsPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // ========== FILTRES ==========
        JPanel filtrePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filtrePanel.setBackground(new Color(233, 236, 239));
        filtrePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Filtre par type
        filtrePanel.add(new JLabel("Filtre:"));
        cmbFiltre = new JComboBox<>(new String[]{
            "Toutes les actions",
            "Aujourd'hui",
            "Connexions",
            "Ajouts",
            "Modifications",
            "Suppressions",
            "Donneurs",
            "Dons",
            "Stock",
            "Distributions"
        });
        cmbFiltre.setPreferredSize(new Dimension(150, 30));
        cmbFiltre.addActionListener(e -> appliquerFiltre());
        filtrePanel.add(cmbFiltre);
        
        // Filtre par utilisateur
        filtrePanel.add(Box.createHorizontalStrut(20));
        filtrePanel.add(new JLabel("Utilisateur:"));
        cmbUtilisateur = new JComboBox<>();
        cmbUtilisateur.addItem(new UtilisateurItem(0, "Tous"));
        for (Utilisateur u : utilisateurDAO.listerTous()) {
            cmbUtilisateur.addItem(new UtilisateurItem(u.getId(), u.getNomComplet()));
        }
        cmbUtilisateur.setPreferredSize(new Dimension(150, 30));
        cmbUtilisateur.addActionListener(e -> appliquerFiltre());
        filtrePanel.add(cmbUtilisateur);
        
        // Recherche
        filtrePanel.add(Box.createHorizontalStrut(20));
        filtrePanel.add(new JLabel("🔍"));
        txtRecherche = new JTextField(15);
        txtRecherche.setPreferredSize(new Dimension(150, 30));
        txtRecherche.addActionListener(e -> appliquerFiltre());
        filtrePanel.add(txtRecherche);
        
        // Limite
        filtrePanel.add(Box.createHorizontalStrut(20));
        filtrePanel.add(new JLabel("Limite:"));
        spnLimite = new JSpinner(new SpinnerNumberModel(100, 10, 1000, 50));
        spnLimite.setPreferredSize(new Dimension(70, 30));
        filtrePanel.add(spnLimite);
        
        // Bouton rechercher
        JButton btnRechercher = new JButton("Rechercher");
        btnRechercher.setBackground(new Color(0, 123, 255));
        btnRechercher.setForeground(Color.WHITE);
        btnRechercher.addActionListener(e -> appliquerFiltre());
        filtrePanel.add(btnRechercher);
        
        // Panel central avec filtres + table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(filtrePanel, BorderLayout.NORTH);
        
        // ========== TABLE ==========
        String[] colonnes = {"ID", "Date/Heure", "Utilisateur", "Action", "Entité", "Description"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(52, 58, 64));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(0, 123, 255, 50));
        table.setGridColor(new Color(220, 220, 220));
        
        // Masquer ID
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        
        // Largeurs des colonnes
        table.getColumnModel().getColumn(1).setPreferredWidth(130);  // Date
        table.getColumnModel().getColumn(2).setPreferredWidth(120);  // Utilisateur
        table.getColumnModel().getColumn(3).setPreferredWidth(100);  // Action
        table.getColumnModel().getColumn(4).setPreferredWidth(100);  // Entité
        table.getColumnModel().getColumn(5).setPreferredWidth(300);  // Description
        
        // Colorer les actions
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(CENTER);
                String action = value.toString();
                
                if (action.contains("AJOUTER")) {
                    setBackground(new Color(40, 167, 69, 50));
                    setForeground(new Color(40, 167, 69));
                } else if (action.contains("MODIFIER")) {
                    setBackground(new Color(255, 193, 7, 50));
                    setForeground(new Color(180, 140, 0));
                } else if (action.contains("SUPPRIMER")) {
                    setBackground(new Color(220, 53, 69, 50));
                    setForeground(new Color(220, 53, 69));
                } else if (action.contains("CONNEXION")) {
                    setBackground(new Color(0, 123, 255, 50));
                    setForeground(new Color(0, 123, 255));
                } else {
                    setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                    setForeground(Color.BLACK);
                }
                return this;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // ========== BOUTONS ==========
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        buttonPanel.setBackground(new Color(240, 240, 245));
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JButton btnRefresh = createButton("🔄 Rafraîchir", new Color(0, 123, 255));
        JButton btnExporter = createButton("📥 Exporter", new Color(40, 167, 69));
        JButton btnNettoyer = createButton("🧹 Nettoyer ancien", new Color(255, 193, 7));
        
        btnRefresh.addActionListener(e -> chargerDonnees());
        btnExporter.addActionListener(e -> exporterHistorique());
        btnNettoyer.addActionListener(e -> nettoyerAncien());
        
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnExporter);
        buttonPanel.add(btnNettoyer);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(170, 45));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        
        return btn;
    }
    
    public void chargerDonnees() {
        tableModel.setRowCount(0);
        int limite = (int) spnLimite.getValue();
        
        List<ActionLog> actions = actionLogDAO.listerTout(limite);
        
        for (ActionLog a : actions) {
            Object[] row = {
                a.getId(),
                dateFormat.format(a.getDateAction()),
                a.getUtilisateurNom(),
                a.getActionEmoji() + " " + a.getAction(),
                a.getEntiteEmoji() + " " + a.getEntite(),
                a.getDescription()
            };
            tableModel.addRow(row);
        }
    }
    
    private void appliquerFiltre() {
        tableModel.setRowCount(0);
        int limite = (int) spnLimite.getValue();
        
        String filtre = (String) cmbFiltre.getSelectedItem();
        UtilisateurItem userItem = (UtilisateurItem) cmbUtilisateur.getSelectedItem();
        String recherche = txtRecherche.getText().trim();
        
        List<ActionLog> actions;
        
        // Si recherche textuelle
        if (!recherche.isEmpty()) {
            actions = actionLogDAO.rechercher(recherche, limite);
        }
        // Si filtre par utilisateur
        else if (userItem != null && userItem.id > 0) {
            actions = actionLogDAO.listerParUtilisateur(userItem.id, limite);
        }
        // Autres filtres
        else {
            switch (filtre) {
                case "Aujourd'hui":
                    actions = actionLogDAO.listerAujourdhui();
                    break;
                case "Connexions":
                    actions = actionLogDAO.rechercher("CONNEXION", limite);
                    break;
                case "Ajouts":
                    actions = actionLogDAO.rechercher("AJOUTER", limite);
                    break;
                case "Modifications":
                    actions = actionLogDAO.rechercher("MODIFIER", limite);
                    break;
                case "Suppressions":
                    actions = actionLogDAO.rechercher("SUPPRIMER", limite);
                    break;
                case "Donneurs":
                    actions = actionLogDAO.listerParEntite(ActionLog.ENTITE_DONNEUR, limite);
                    break;
                case "Dons":
                    actions = actionLogDAO.listerParEntite(ActionLog.ENTITE_DON, limite);
                    break;
                case "Stock":
                    actions = actionLogDAO.listerParEntite(ActionLog.ENTITE_STOCK, limite);
                    break;
                case "Distributions":
                    actions = actionLogDAO.listerParEntite(ActionLog.ENTITE_DISTRIBUTION, limite);
                    break;
                default:
                    actions = actionLogDAO.listerTout(limite);
            }
        }
        
        for (ActionLog a : actions) {
            Object[] row = {
                a.getId(),
                dateFormat.format(a.getDateAction()),
                a.getUtilisateurNom(),
                a.getActionEmoji() + " " + a.getAction(),
                a.getEntiteEmoji() + " " + a.getEntite(),
                a.getDescription()
            };
            tableModel.addRow(row);
        }
    }
    
    private void exporterHistorique() {
        JOptionPane.showMessageDialog(this, 
            "Fonctionnalité d'export à implémenter!\n" +
            "Tu peux exporter vers CSV, PDF, etc.",
            "Export", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void nettoyerAncien() {
        String[] options = {"30 jours", "60 jours", "90 jours", "Annuler"};
        int choix = JOptionPane.showOptionDialog(this,
            "Supprimer les actions plus anciennes que :",
            "Nettoyage",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null, options, options[2]);
        
        if (choix >= 0 && choix < 3) {
            int jours = choix == 0 ? 30 : (choix == 1 ? 60 : 90);
            int supprimees = actionLogDAO.supprimerAnciennes(jours);
            JOptionPane.showMessageDialog(this, 
                supprimees + " action(s) ancienne(s) supprimée(s)");
            chargerDonnees();
        }
    }
    
    /**
     * Classe interne pour le ComboBox des utilisateurs
     */
    private static class UtilisateurItem {
        int id;
        String nom;
        
        UtilisateurItem(int id, String nom) {
            this.id = id;
            this.nom = nom;
        }
        
        @Override
        public String toString() {
            return nom;
        }
    }
}
