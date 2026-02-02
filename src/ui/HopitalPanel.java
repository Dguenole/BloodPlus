/*
 * Package UI : Interfaces graphiques
 */
package ui;

import dao.HopitalDAO;
import model.Hopital;
import utils.ValidationUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * HopitalPanel : Gestion des hôpitaux
 * 
 * @author dteach
 */
public class HopitalPanel extends JPanel {
    
    private JTable table;
    private DefaultTableModel tableModel;
    
    private HopitalDAO hopitalDAO;

    public HopitalPanel() {
        hopitalDAO = new HopitalDAO();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // ========== TITRE ==========
        JLabel titleLabel = new JLabel("🏥 Gestion des Hôpitaux");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        // ========== TABLEAU ==========
        String[] colonnes = {"ID", "Nom", "Ville", "Téléphone", "Responsable", "Actif"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setRowHeight(30);
        
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // ========== BOUTONS ==========
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
        
        refreshData();
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
        btn.setPreferredSize(new Dimension(150, 45));
        
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
    
    private void showFormulaire(Hopital hopital) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            hopital == null ? "Nouvel Hôpital" : "Modifier Hôpital", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        
        JTextField txtNom = new JTextField(20);
        JTextField txtAdresse = new JTextField(20);
        JTextField txtVille = new JTextField(20);
        JTextField txtTelephone = new JTextField(20);
        JTextField txtEmail = new JTextField(20);
        JTextField txtResponsable = new JTextField(20);
        JCheckBox chkActif = new JCheckBox("Actif");
        chkActif.setSelected(true);
        
        if (hopital != null) {
            txtNom.setText(hopital.getNom());
            txtAdresse.setText(hopital.getAdresse());
            txtVille.setText(hopital.getVille());
            txtTelephone.setText(hopital.getTelephone());
            txtEmail.setText(hopital.getEmail());
            txtResponsable.setText(hopital.getResponsable());
            chkActif.setSelected(hopital.isActif());
        }
        
        int row = 0;
        addField(formPanel, gbc, row++, "Nom *:", txtNom);
        addField(formPanel, gbc, row++, "Adresse:", txtAdresse);
        addField(formPanel, gbc, row++, "Ville:", txtVille);
        addField(formPanel, gbc, row++, "Téléphone:", txtTelephone);
        addField(formPanel, gbc, row++, "Email:", txtEmail);
        addField(formPanel, gbc, row++, "Responsable:", txtResponsable);
        
        gbc.gridx = 1; gbc.gridy = row++;
        formPanel.add(chkActif, gbc);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = createButton("💾 Enregistrer", new Color(40, 167, 69));
        JButton btnCancel = createButton("❌ Annuler", new Color(108, 117, 125));
        
        btnCancel.addActionListener(e -> dialog.dispose());
        
        btnSave.addActionListener(e -> {
            if (ValidationUtils.estVide(txtNom.getText())) {
                JOptionPane.showMessageDialog(dialog, "Le nom est obligatoire !");
                return;
            }
            
            Hopital h = hopital != null ? hopital : new Hopital();
            h.setNom(txtNom.getText().trim());
            h.setAdresse(txtAdresse.getText().trim());
            h.setVille(txtVille.getText().trim());
            h.setTelephone(txtTelephone.getText().trim());
            h.setEmail(txtEmail.getText().trim());
            h.setResponsable(txtResponsable.getText().trim());
            h.setActif(chkActif.isSelected());
            
            boolean success = hopital == null ? hopitalDAO.ajouter(h) : hopitalDAO.modifier(h);
            
            if (success) {
                JOptionPane.showMessageDialog(dialog, "✅ Hôpital enregistré !");
                dialog.dispose();
                refreshData();
            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Erreur");
            }
        });
        
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);
        
        dialog.add(formPanel);
        dialog.setVisible(true);
    }
    
    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }
    
    private void modifierSelection() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un hôpital");
            return;
        }
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        Hopital h = hopitalDAO.trouverParId(id);
        if (h != null) showFormulaire(h);
    }
    
    private void supprimerSelection() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un hôpital");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Supprimer cet hôpital ?", 
            "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            if (hopitalDAO.supprimer(id)) {
                JOptionPane.showMessageDialog(this, "✅ Supprimé");
                refreshData();
            }
        }
    }
    
    public void refreshData() {
        tableModel.setRowCount(0);
        List<Hopital> hopitaux = hopitalDAO.listerTous();
        
        for (Hopital h : hopitaux) {
            tableModel.addRow(new Object[]{
                h.getId(),
                h.getNom(),
                h.getVille(),
                h.getTelephone(),
                h.getResponsable(),
                h.isActif() ? "✅ Oui" : "❌ Non"
            });
        }
    }
}
