/*
 * Package UI
 */
package ui;

import dao.UtilisateurDAO;
import model.Utilisateur;
import service.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * UtilisateurPanel : Gestion des utilisateurs (réservé ADMIN)
 * 
 * @author dteach
 */
public class UtilisateurPanel extends JPanel {
    
    private JTable table;
    private DefaultTableModel tableModel;
    private UtilisateurDAO utilisateurDAO;
    
    private JButton btnAjouter, btnModifier, btnSupprimer, btnChangerMdp;

    public UtilisateurPanel() {
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
        
        JLabel lblTitre = new JLabel("👥 Gestion des Utilisateurs");
        lblTitre.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel lblInfo = new JLabel("Seul l'administrateur peut gérer les utilisateurs");
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 12));
        lblInfo.setForeground(Color.GRAY);
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(lblTitre);
        titlePanel.add(lblInfo);
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // ========== TABLE ==========
        String[] colonnes = {"ID", "Username", "Nom Complet", "Rôle", "Actif", "Dernière Connexion"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(52, 58, 64));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(220, 53, 69, 50));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(200, 200, 200));
        
        // Colorer les rôles
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(CENTER);
                String role = value.toString();
                if (role.equals("ADMIN")) {
                    setBackground(isSelected ? new Color(220, 53, 69, 80) : new Color(220, 53, 69, 40));
                    setForeground(new Color(220, 53, 69));
                } else if (role.equals("OPERATEUR")) {
                    setBackground(isSelected ? new Color(255, 193, 7, 80) : new Color(255, 193, 7, 40));
                    setForeground(new Color(200, 150, 0));
                } else {
                    setBackground(isSelected ? new Color(40, 167, 69, 80) : new Color(40, 167, 69, 40));
                    setForeground(new Color(40, 167, 69));
                }
                return this;
            }
        });
        
        // Masquer la colonne ID
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(scrollPane, BorderLayout.CENTER);
        
        // ========== BOUTONS ==========
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        buttonPanel.setBackground(new Color(240, 240, 245));
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        btnAjouter = createButton("➕ Nouvel utilisateur", new Color(40, 167, 69));
        btnModifier = createButton("✏️ Modifier", new Color(0, 123, 255));
        btnChangerMdp = createButton("🔑 Changer mot de passe", new Color(255, 193, 7));
        btnSupprimer = createButton("🗑️ Supprimer", new Color(220, 53, 69));
        
        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnChangerMdp);
        buttonPanel.add(btnSupprimer);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // ========== ÉVÉNEMENTS ==========
        btnAjouter.addActionListener(e -> ajouterUtilisateur());
        btnModifier.addActionListener(e -> modifierUtilisateur());
        btnChangerMdp.addActionListener(e -> changerMotDePasse());
        btnSupprimer.addActionListener(e -> supprimerUtilisateur());
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
        btn.setPreferredSize(new Dimension(200, 45));
        
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
        
        List<Utilisateur> utilisateurs = utilisateurDAO.listerTous();
        
        for (Utilisateur u : utilisateurs) {
            Object[] row = {
                u.getId(),
                u.getUsername(),
                u.getNomComplet(),
                u.getRole(),
                u.isActif() ? "✅ Oui" : "❌ Non",
                u.getDerniereConnexion() != null ? 
                    new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(u.getDerniereConnexion()) : 
                    "Jamais"
            };
            tableModel.addRow(row);
        }
    }
    
    private void ajouterUtilisateur() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                      "Nouvel utilisateur", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        JTextField txtUsername = new JTextField(20);
        JTextField txtNomComplet = new JTextField(20);
        JPasswordField txtPassword = new JPasswordField(20);
        JPasswordField txtConfirmPassword = new JPasswordField(20);
        JComboBox<String> cmbRole = new JComboBox<>(Utilisateur.TOUS_LES_ROLES);
        JCheckBox chkActif = new JCheckBox("Compte actif", true);
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nom d'utilisateur* :"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtUsername, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Nom complet* :"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtNomComplet, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Mot de passe* :"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPassword, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Confirmer mot de passe* :"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtConfirmPassword, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Rôle* :"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbRole, gbc);
        
        gbc.gridx = 1; gbc.gridy = 5;
        formPanel.add(chkActif, gbc);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        // Boutons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSauvegarder = new JButton("Sauvegarder");
        btnSauvegarder.setBackground(new Color(40, 167, 69));
        btnSauvegarder.setForeground(Color.WHITE);
        JButton btnAnnuler = new JButton("Annuler");
        
        btnPanel.add(btnSauvegarder);
        btnPanel.add(btnAnnuler);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        
        btnAnnuler.addActionListener(e -> dialog.dispose());
        btnSauvegarder.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String nomComplet = txtNomComplet.getText().trim();
            String password = new String(txtPassword.getPassword());
            String confirmPassword = new String(txtConfirmPassword.getPassword());
            String role = (String) cmbRole.getSelectedItem();
            
            // Validation
            if (username.isEmpty() || nomComplet.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Tous les champs sont obligatoires !", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog, "Les mots de passe ne correspondent pas !", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (password.length() < 4) {
                JOptionPane.showMessageDialog(dialog, "Le mot de passe doit contenir au moins 4 caractères !", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (utilisateurDAO.usernameExiste(username)) {
                JOptionPane.showMessageDialog(dialog, "Ce nom d'utilisateur existe déjà !", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Créer l'utilisateur
            Utilisateur user = new Utilisateur(username, password, nomComplet, role);
            user.setActif(chkActif.isSelected());
            
            if (utilisateurDAO.ajouter(user)) {
                JOptionPane.showMessageDialog(dialog, "Utilisateur créé avec succès !", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                chargerDonnees();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Erreur lors de la création !", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.setVisible(true);
    }
    
    private void modifierUtilisateur() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur à modifier.",
                "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        Utilisateur user = utilisateurDAO.trouverParId(userId);
        
        if (user == null) return;
        
        // Empêcher la modification de son propre compte (sauf Admin)
        if (user.getId() == Session.getInstance().getUtilisateurConnecte().getId()) {
            JOptionPane.showMessageDialog(this, "Vous ne pouvez pas modifier votre propre compte ici.\nUtilisez 'Changer mot de passe'.",
                "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                      "Modifier utilisateur", true);
        dialog.setSize(400, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        JTextField txtUsername = new JTextField(user.getUsername(), 20);
        JTextField txtNomComplet = new JTextField(user.getNomComplet(), 20);
        JComboBox<String> cmbRole = new JComboBox<>(Utilisateur.TOUS_LES_ROLES);
        cmbRole.setSelectedItem(user.getRole());
        JCheckBox chkActif = new JCheckBox("Compte actif", user.isActif());
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nom d'utilisateur :"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtUsername, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Nom complet :"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtNomComplet, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Rôle :"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbRole, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(chkActif, gbc);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSauvegarder = new JButton("Sauvegarder");
        btnSauvegarder.setBackground(new Color(40, 167, 69));
        btnSauvegarder.setForeground(Color.WHITE);
        JButton btnAnnuler = new JButton("Annuler");
        
        btnPanel.add(btnSauvegarder);
        btnPanel.add(btnAnnuler);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        
        btnAnnuler.addActionListener(e -> dialog.dispose());
        btnSauvegarder.addActionListener(e -> {
            user.setUsername(txtUsername.getText().trim());
            user.setNomComplet(txtNomComplet.getText().trim());
            user.setRole((String) cmbRole.getSelectedItem());
            user.setActif(chkActif.isSelected());
            
            if (utilisateurDAO.modifier(user)) {
                JOptionPane.showMessageDialog(dialog, "Utilisateur modifié avec succès !");
                chargerDonnees();
                dialog.dispose();
            }
        });
        
        dialog.setVisible(true);
    }
    
    private void changerMotDePasse() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur.",
                "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 10));
        JPasswordField txtNewPassword = new JPasswordField();
        JPasswordField txtConfirmPassword = new JPasswordField();
        
        panel.add(new JLabel("Nouveau mot de passe :"));
        panel.add(txtNewPassword);
        panel.add(new JLabel("Confirmer :"));
        panel.add(txtConfirmPassword);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Changer mot de passe de " + username, JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String newPassword = new String(txtNewPassword.getPassword());
            String confirmPassword = new String(txtConfirmPassword.getPassword());
            
            if (newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Le mot de passe ne peut pas être vide !");
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Les mots de passe ne correspondent pas !");
                return;
            }
            
            if (utilisateurDAO.changerMotDePasse(userId, newPassword)) {
                JOptionPane.showMessageDialog(this, "Mot de passe changé avec succès !");
            }
        }
    }
    
    private void supprimerUtilisateur() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur à supprimer.",
                "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Empêcher la suppression de son propre compte
        if (userId == Session.getInstance().getUtilisateurConnecte().getId()) {
            JOptionPane.showMessageDialog(this, "Vous ne pouvez pas supprimer votre propre compte !",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Voulez-vous vraiment supprimer l'utilisateur '" + username + "' ?",
            "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (utilisateurDAO.supprimer(userId)) {
                JOptionPane.showMessageDialog(this, "Utilisateur supprimé avec succès !");
                chargerDonnees();
            }
        }
    }
}
