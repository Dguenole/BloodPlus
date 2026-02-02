/*
 * Package UI : Interfaces graphiques
 */
package ui;

import dao.DistributionDAO;
import dao.HopitalDAO;
import dao.StockSanguinDAO;
import model.Distribution;
import model.GroupeSanguin;
import model.Hopital;
import utils.DateUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * DistributionPanel : Gestion des distributions de sang
 * 
 * @author dteach
 */
public class DistributionPanel extends JPanel {
    
    private JTable table;
    private DefaultTableModel tableModel;
    
    private DistributionDAO distributionDAO;
    private HopitalDAO hopitalDAO;
    private StockSanguinDAO stockDAO;

    public DistributionPanel() {
        distributionDAO = new DistributionDAO();
        hopitalDAO = new HopitalDAO();
        stockDAO = new StockSanguinDAO();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // ========== TITRE ==========
        JLabel titleLabel = new JLabel("📦 Gestion des Distributions");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        // ========== TABLEAU ==========
        String[] colonnes = {"ID", "Hôpital", "Groupe Sanguin", "Quantité (ml)", "Date", "Statut", "Motif"};
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
        
        JButton btnNouvelle = createButton("➕ Nouvelle Distribution", new Color(40, 167, 69));
        JButton btnLivrer = createButton("✅ Marquer Livrée", new Color(0, 123, 255));
        JButton btnAnnuler = createButton("❌ Annuler", new Color(220, 53, 69));
        JButton btnRefresh = createButton("🔄 Rafraîchir", new Color(108, 117, 125));
        
        btnNouvelle.addActionListener(e -> showFormulaire());
        btnLivrer.addActionListener(e -> marquerLivree());
        btnAnnuler.addActionListener(e -> annulerSelection());
        btnRefresh.addActionListener(e -> refreshData());
        
        buttonPanel.add(btnNouvelle);
        buttonPanel.add(btnLivrer);
        buttonPanel.add(btnAnnuler);
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
        btn.setPreferredSize(new Dimension(180, 45));
        
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
    
    private void showFormulaire() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Nouvelle Distribution", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        
        // Hôpitaux
        List<Hopital> hopitaux = hopitalDAO.listerActifs();
        JComboBox<Hopital> cmbHopital = new JComboBox<>();
        for (Hopital h : hopitaux) {
            cmbHopital.addItem(h);
        }
        cmbHopital.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Hopital) {
                    Hopital h = (Hopital) value;
                    setText(h.getNom() + " - " + h.getVille());
                }
                return this;
            }
        });
        
        JComboBox<String> cmbGroupe = new JComboBox<>(GroupeSanguin.TOUS_LES_GROUPES);
        JSpinner spnQuantite = new JSpinner(new SpinnerNumberModel(450, 100, 5000, 50));
        JTextField txtMotif = new JTextField(20);
        
        // Afficher le stock disponible
        JLabel lblStockDispo = new JLabel("Stock disponible: -");
        cmbGroupe.addActionListener(e -> {
            String groupe = (String) cmbGroupe.getSelectedItem();
            int stock = stockDAO.getQuantiteTotaleParGroupe(groupe);
            lblStockDispo.setText("Stock disponible: " + stock + " ml");
            lblStockDispo.setForeground(stock < 500 ? Color.RED : new Color(40, 167, 69));
        });
        
        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Hôpital *:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbHopital, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Groupe Sanguin *:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbGroupe, gbc);
        
        row++;
        gbc.gridx = 1; gbc.gridy = row;
        formPanel.add(lblStockDispo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Quantité (ml):"), gbc);
        gbc.gridx = 1;
        formPanel.add(spnQuantite, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Motif:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtMotif, gbc);
        
        // Boutons
        row++;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = createButton("💾 Enregistrer", new Color(40, 167, 69));
        JButton btnCancel = createButton("❌ Annuler", new Color(108, 117, 125));
        
        btnCancel.addActionListener(e -> dialog.dispose());
        
        btnSave.addActionListener(e -> {
            if (cmbHopital.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(dialog, "Sélectionnez un hôpital");
                return;
            }
            
            Hopital hopital = (Hopital) cmbHopital.getSelectedItem();
            String groupe = (String) cmbGroupe.getSelectedItem();
            int quantite = (int) spnQuantite.getValue();
            
            // Vérifier le stock
            int stockDispo = stockDAO.getQuantiteTotaleParGroupe(groupe);
            if (quantite > stockDispo) {
                JOptionPane.showMessageDialog(dialog, 
                    "⚠️ Stock insuffisant !\nDisponible: " + stockDispo + " ml",
                    "Erreur", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Distribution dist = new Distribution(hopital.getId(), groupe, quantite);
            dist.setMotif(txtMotif.getText().trim());
            
            if (distributionDAO.ajouter(dist)) {
                JOptionPane.showMessageDialog(dialog, "✅ Distribution enregistrée !");
                dialog.dispose();
                refreshData();
                // Rafraîchir le dashboard pour mettre à jour les stats
                if (MainFrame.getInstance() != null) {
                    MainFrame.getInstance().refreshDashboard();
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Erreur");
            }
        });
        
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);
        
        dialog.add(formPanel);
        
        // Déclencher l'affichage du stock initial
        cmbGroupe.setSelectedIndex(0);
        
        dialog.setVisible(true);
    }
    
    private void marquerLivree() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une distribution");
            return;
        }
        
        String statut = (String) tableModel.getValueAt(selectedRow, 5);
        if (!"EN_COURS".equals(statut)) {
            JOptionPane.showMessageDialog(this, "Cette distribution a déjà été traitée");
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        if (distributionDAO.marquerLivree(id)) {
            JOptionPane.showMessageDialog(this, "✅ Distribution marquée comme livrée !");
            refreshData();
            // Rafraîchir le dashboard
            if (MainFrame.getInstance() != null) {
                MainFrame.getInstance().refreshDashboard();
            }
        }
    }
    
    private void annulerSelection() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une distribution");
            return;
        }
        
        String statut = (String) tableModel.getValueAt(selectedRow, 5);
        if (!"EN_COURS".equals(statut)) {
            JOptionPane.showMessageDialog(this, "Cette distribution a déjà été traitée");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Annuler cette distribution ?", 
            "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            if (distributionDAO.annuler(id)) {
                JOptionPane.showMessageDialog(this, "Distribution annulée");
                refreshData();
                // Rafraîchir le dashboard
                if (MainFrame.getInstance() != null) {
                    MainFrame.getInstance().refreshDashboard();
                }
            }
        }
    }
    
    public void refreshData() {
        tableModel.setRowCount(0);
        List<Distribution> distributions = distributionDAO.listerTous();
        
        for (Distribution d : distributions) {
            String nomHopital = d.getHopital() != null ? d.getHopital().getNom() : "Inconnu";
            
            tableModel.addRow(new Object[]{
                d.getId(),
                nomHopital,
                d.getGroupeSanguin(),
                d.getQuantite(),
                DateUtils.dateHeureToString(d.getDateDistribution()),
                d.getStatut(),
                d.getMotif()
            });
        }
    }
}
