/*
 * Package UI : Interfaces graphiques
 */
package ui;

import dao.DonneurDAO;
import model.Donneur;
import model.GroupeSanguin;
import utils.ValidationUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * DonneurPanel : Gestion des donneurs
 * 
 * 💡 EXPLICATION :
 * Cet écran permet de :
 * - Voir la liste des donneurs (JTable)
 * - Ajouter un nouveau donneur
 * - Modifier un donneur existant
 * - Supprimer un donneur
 * - Rechercher des donneurs
 * 
 * @author dteach
 */
public class DonneurPanel extends JPanel {
    
    // Composants
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtRecherche;
    
    // DAO
    private DonneurDAO donneurDAO;

    public DonneurPanel() {
        donneurDAO = new DonneurDAO();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // ========== EN-TÊTE ==========
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("👥 Gestion des Donneurs");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Barre de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        txtRecherche = new JTextField(20);
        txtRecherche.putClientProperty("JTextField.placeholderText", "Rechercher...");
        JButton btnRecherche = new JButton("🔍");
        btnRecherche.addActionListener(e -> rechercher());
        txtRecherche.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    rechercher();
                }
            }
        });
        searchPanel.add(txtRecherche);
        searchPanel.add(btnRecherche);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // ========== TABLEAU ==========
        String[] colonnes = {"ID", "Nom", "Prénom", "Groupe Sanguin", "Téléphone", "Email", "Apte"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Empêcher l'édition directe
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Cacher la colonne ID (mais garder la donnée)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        add(scrollPane, BorderLayout.CENTER);
        
        // ========== BOUTONS D'ACTION ==========
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        buttonPanel.setBackground(new Color(240, 240, 245));
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JButton btnAjouter = createButton("➕ Ajouter", new Color(40, 167, 69));
        JButton btnModifier = createButton("✏️ Modifier", new Color(0, 123, 255));
        JButton btnSupprimer = createButton("🗑️ Supprimer", new Color(220, 53, 69));
        JButton btnRefresh = createButton("🔄 Rafraîchir", new Color(108, 117, 125));
        
        btnAjouter.addActionListener(e -> showFormulaire(null));
        btnModifier.addActionListener(e -> modifierSelection());
        btnSupprimer.addActionListener(e -> supprimerSelection());
        btnRefresh.addActionListener(e -> refreshData());
        
        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(btnRefresh);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Charger les données
        refreshData();
    }
    
    /**
     * Crée un bouton stylisé avec meilleure visibilité
     */
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
        btn.setPreferredSize(new Dimension(150, 45));
        
        // Effet hover
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
    
    /**
     * Affiche le formulaire pour ajouter/modifier un donneur
     */
    private void showFormulaire(Donneur donneur) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            donneur == null ? "Nouveau Donneur" : "Modifier Donneur", true);
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Champs du formulaire
        JTextField txtNom = new JTextField(20);
        JTextField txtPrenom = new JTextField(20);
        JComboBox<String> cmbGroupe = new JComboBox<>(GroupeSanguin.TOUS_LES_GROUPES);
        JComboBox<String> cmbSexe = new JComboBox<>(new String[]{"M", "F"});
        JTextField txtTelephone = new JTextField(20);
        JTextField txtEmail = new JTextField(20);
        JTextField txtAdresse = new JTextField(20);
        JCheckBox chkApte = new JCheckBox("Apte à donner");
        chkApte.setSelected(true);
        
        // Pré-remplir si modification
        if (donneur != null) {
            txtNom.setText(donneur.getNom());
            txtPrenom.setText(donneur.getPrenom());
            cmbGroupe.setSelectedItem(donneur.getGroupeSanguin());
            cmbSexe.setSelectedItem(donneur.getSexe());
            txtTelephone.setText(donneur.getTelephone());
            txtEmail.setText(donneur.getEmail());
            txtAdresse.setText(donneur.getAdresse());
            chkApte.setSelected(donneur.isApte());
        }
        
        // Ajouter les composants
        int row = 0;
        addFormField(formPanel, gbc, row++, "Nom *:", txtNom);
        addFormField(formPanel, gbc, row++, "Prénom *:", txtPrenom);
        addFormField(formPanel, gbc, row++, "Groupe Sanguin *:", cmbGroupe);
        addFormField(formPanel, gbc, row++, "Sexe:", cmbSexe);
        addFormField(formPanel, gbc, row++, "Téléphone:", txtTelephone);
        addFormField(formPanel, gbc, row++, "Email:", txtEmail);
        addFormField(formPanel, gbc, row++, "Adresse:", txtAdresse);
        
        gbc.gridx = 1; gbc.gridy = row++;
        formPanel.add(chkApte, gbc);
        
        // Boutons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = createButton("💾 Enregistrer", new Color(40, 167, 69));
        JButton btnCancel = createButton("❌ Annuler", new Color(108, 117, 125));
        
        btnCancel.addActionListener(e -> dialog.dispose());
        
        btnSave.addActionListener(e -> {
            // Validation
            if (ValidationUtils.estVide(txtNom.getText()) || 
                ValidationUtils.estVide(txtPrenom.getText())) {
                JOptionPane.showMessageDialog(dialog, 
                    "Les champs Nom et Prénom sont obligatoires !", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Créer ou mettre à jour le donneur
            Donneur d = donneur != null ? donneur : new Donneur();
            d.setNom(txtNom.getText().trim());
            d.setPrenom(txtPrenom.getText().trim());
            d.setGroupeSanguin((String) cmbGroupe.getSelectedItem());
            d.setSexe((String) cmbSexe.getSelectedItem());
            d.setTelephone(txtTelephone.getText().trim());
            d.setEmail(txtEmail.getText().trim());
            d.setAdresse(txtAdresse.getText().trim());
            d.setApte(chkApte.isSelected());
            
            boolean success;
            if (donneur == null) {
                success = donneurDAO.ajouter(d);
            } else {
                success = donneurDAO.modifier(d);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(dialog, 
                    "✅ Donneur enregistré avec succès !", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshData();
            } else {
                JOptionPane.showMessageDialog(dialog, 
                    "❌ Erreur lors de l'enregistrement", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);
        
        dialog.add(formPanel);
        dialog.setVisible(true);
    }
    
    /**
     * Ajoute un champ au formulaire
     */
    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }
    
    /**
     * Modifie le donneur sélectionné
     */
    private void modifierSelection() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un donneur à modifier", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        Donneur donneur = donneurDAO.trouverParId(id);
        if (donneur != null) {
            showFormulaire(donneur);
        }
    }
    
    /**
     * Supprime le donneur sélectionné
     */
    private void supprimerSelection() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un donneur à supprimer", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir supprimer ce donneur ?", 
            "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            if (donneurDAO.supprimer(id)) {
                JOptionPane.showMessageDialog(this, "✅ Donneur supprimé");
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Erreur lors de la suppression", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Recherche des donneurs
     */
    private void rechercher() {
        String recherche = txtRecherche.getText().trim();
        if (recherche.isEmpty()) {
            refreshData();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Donneur> donneurs = donneurDAO.rechercher(recherche);
        
        for (Donneur d : donneurs) {
            tableModel.addRow(new Object[]{
                d.getId(),
                d.getNom(),
                d.getPrenom(),
                d.getGroupeSanguin(),
                d.getTelephone(),
                d.getEmail(),
                d.isApte() ? "✅ Oui" : "❌ Non"
            });
        }
    }
    
    /**
     * Rafraîchit les données du tableau
     */
    public void refreshData() {
        tableModel.setRowCount(0);
        List<Donneur> donneurs = donneurDAO.listerTous();
        
        for (Donneur d : donneurs) {
            tableModel.addRow(new Object[]{
                d.getId(),
                d.getNom(),
                d.getPrenom(),
                d.getGroupeSanguin(),
                d.getTelephone(),
                d.getEmail(),
                d.isApte() ? "✅ Oui" : "❌ Non"
            });
        }
    }
}
