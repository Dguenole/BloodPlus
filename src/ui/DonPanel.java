/*
 * Package UI : Interfaces graphiques
 */
package ui;

import dao.DonDAO;
import dao.DonneurDAO;
import model.Don;
import model.Donneur;
import utils.DateUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;

/**
 * DonPanel : Gestion des dons de sang
 * 
 * @author dteach
 */
public class DonPanel extends JPanel {
    
    private JTable table;
    private DefaultTableModel tableModel;
    
    private DonDAO donDAO;
    private DonneurDAO donneurDAO;

    public DonPanel() {
        donDAO = new DonDAO();
        donneurDAO = new DonneurDAO();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // ========== TITRE ==========
        JLabel titleLabel = new JLabel("💉 Gestion des Dons");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        // ========== TABLEAU ==========
        String[] colonnes = {"ID", "Donneur", "Groupe", "Date", "Quantité (ml)", "Statut"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        
        // Cacher ID
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
        
        JButton btnNouveau = createButton("➕ Nouveau Don", new Color(40, 167, 69));
        JButton btnValider = createButton("✅ Valider", new Color(0, 123, 255));
        JButton btnRejeter = createButton("❌ Rejeter", new Color(220, 53, 69));
        JButton btnRefresh = createButton("🔄 Rafraîchir", new Color(108, 117, 125));
        
        btnNouveau.addActionListener(e -> showFormulaire());
        btnValider.addActionListener(e -> validerSelection());
        btnRejeter.addActionListener(e -> rejeterSelection());
        btnRefresh.addActionListener(e -> refreshData());
        
        buttonPanel.add(btnNouveau);
        buttonPanel.add(btnValider);
        buttonPanel.add(btnRejeter);
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
        btn.setPreferredSize(new Dimension(160, 45));
        
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
     * Affiche le formulaire pour enregistrer un nouveau don
     */
    private void showFormulaire() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Enregistrer un Don", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        
        // Liste des donneurs aptes
        List<Donneur> donneurs = donneurDAO.listerTous();
        JComboBox<Donneur> cmbDonneur = new JComboBox<>();
        for (Donneur d : donneurs) {
            if (d.isApte()) {
                cmbDonneur.addItem(d);
            }
        }
        
        // Afficher le nom complet dans la combobox
        cmbDonneur.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Donneur) {
                    Donneur d = (Donneur) value;
                    setText(d.getNomComplet() + " (" + d.getGroupeSanguin() + ")");
                }
                return this;
            }
        });
        
        JSpinner spnQuantite = new JSpinner(new SpinnerNumberModel(450, 200, 500, 10));
        JTextArea txtNotes = new JTextArea(3, 20);
        txtNotes.setLineWrap(true);
        
        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Donneur *:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbDonneur, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Quantité (ml):"), gbc);
        gbc.gridx = 1;
        formPanel.add(spnQuantite, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(txtNotes), gbc);
        
        // Boutons
        row++;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = createButton("💾 Enregistrer", new Color(40, 167, 69));
        JButton btnCancel = createButton("❌ Annuler", new Color(108, 117, 125));
        
        btnCancel.addActionListener(e -> dialog.dispose());
        
        btnSave.addActionListener(e -> {
            if (cmbDonneur.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(dialog, "Veuillez sélectionner un donneur");
                return;
            }
            
            Donneur donneur = (Donneur) cmbDonneur.getSelectedItem();
            int quantite = (int) spnQuantite.getValue();
            
            Don don = new Don(donneur.getId(), new Date(), quantite);
            don.setNotes(txtNotes.getText().trim());
            
            if (donDAO.ajouter(don)) {
                JOptionPane.showMessageDialog(dialog, "✅ Don enregistré avec succès !");
                dialog.dispose();
                refreshData();
            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Erreur lors de l'enregistrement");
            }
        });
        
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);
        
        dialog.add(formPanel);
        dialog.setVisible(true);
    }
    
    private void validerSelection() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un don");
            return;
        }
        
        String statut = (String) tableModel.getValueAt(selectedRow, 5);
        if (!"EN_ATTENTE".equals(statut)) {
            JOptionPane.showMessageDialog(this, "Ce don a déjà été traité");
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        if (donDAO.valider(id)) {
            JOptionPane.showMessageDialog(this, "✅ Don validé !");
            refreshData();
        }
    }
    
    private void rejeterSelection() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un don");
            return;
        }
        
        String statut = (String) tableModel.getValueAt(selectedRow, 5);
        if (!"EN_ATTENTE".equals(statut)) {
            JOptionPane.showMessageDialog(this, "Ce don a déjà été traité");
            return;
        }
        
        String raison = JOptionPane.showInputDialog(this, "Raison du rejet :");
        if (raison != null) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            if (donDAO.rejeter(id, raison)) {
                JOptionPane.showMessageDialog(this, "Don rejeté");
                refreshData();
            }
        }
    }
    
    public void refreshData() {
        tableModel.setRowCount(0);
        List<Don> dons = donDAO.listerTous();
        
        for (Don d : dons) {
            String nomDonneur = d.getDonneur() != null ? 
                d.getDonneur().getNomComplet() : "Inconnu";
            String groupe = d.getDonneur() != null ? 
                d.getDonneur().getGroupeSanguin() : "-";
            
            tableModel.addRow(new Object[]{
                d.getId(),
                nomDonneur,
                groupe,
                DateUtils.dateHeureToString(d.getDateDon()),
                d.getQuantite(),
                d.getStatut()
            });
        }
    }
}
