/*
 * Package UI : Interfaces graphiques
 */
package ui;

import dao.StockSanguinDAO;
import model.GroupeSanguin;
import model.StockSanguin;
import utils.DateUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * StockPanel : Gestion du stock sanguin
 * 
 * @author dteach
 */
public class StockPanel extends JPanel {
    
    private JTable table;
    private DefaultTableModel tableModel;
    private JPanel resumePanel;
    
    private StockSanguinDAO stockDAO;

    public StockPanel() {
        stockDAO = new StockSanguinDAO();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // ========== TITRE ==========
        JLabel titleLabel = new JLabel("🩸 Gestion du Stock Sanguin");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        // ========== PANNEAU CENTRAL ==========
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        
        // Résumé par groupe (en haut)
        resumePanel = new JPanel(new GridLayout(1, 8, 10, 0));
        resumePanel.setOpaque(false);
        resumePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        centerPanel.add(resumePanel, BorderLayout.NORTH);
        
        // Tableau des stocks
        String[] colonnes = {"ID", "Groupe", "Quantité (ml)", "Date Prélèvement", "Date Péremption", "Jours Restants", "Statut"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setRowHeight(30);
        
        // Cacher ID
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // ========== BOUTONS ==========
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        buttonPanel.setBackground(new Color(240, 240, 245));
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JButton btnMarquerPerimes = createButton("⚠️ Marquer périmés", new Color(255, 193, 7));
        JButton btnProchesPeremption = createButton("⏰ Proches péremption", new Color(0, 123, 255));
        JButton btnRefresh = createButton("🔄 Rafraîchir", new Color(108, 117, 125));
        
        btnMarquerPerimes.addActionListener(e -> {
            int nb = stockDAO.marquerPerimes();
            JOptionPane.showMessageDialog(this, nb + " stock(s) marqué(s) comme périmé(s)");
            refreshData();
        });
        
        btnProchesPeremption.addActionListener(e -> afficherProchesPeremption());
        btnRefresh.addActionListener(e -> refreshData());
        
        buttonPanel.add(btnMarquerPerimes);
        buttonPanel.add(btnProchesPeremption);
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
    
    /**
     * Crée une carte résumé pour un groupe sanguin
     */
    private JPanel createResumeCard(String groupe, int quantite) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 53, 69), 2));
        card.setPreferredSize(new Dimension(80, 70));
        
        // Couleur selon niveau
        Color bgColor;
        if (quantite == 0) {
            bgColor = new Color(220, 53, 69);  // Rouge
        } else if (quantite < 1000) {
            bgColor = new Color(255, 193, 7);  // Jaune
        } else {
            bgColor = new Color(40, 167, 69);  // Vert
        }
        card.setBackground(bgColor);
        
        JLabel lblGroupe = new JLabel(groupe, SwingConstants.CENTER);
        lblGroupe.setFont(new Font("Arial", Font.BOLD, 16));
        lblGroupe.setForeground(Color.WHITE);
        
        JLabel lblQte = new JLabel(quantite + " ml", SwingConstants.CENTER);
        lblQte.setFont(new Font("Arial", Font.PLAIN, 11));
        lblQte.setForeground(Color.WHITE);
        
        card.add(lblGroupe, BorderLayout.CENTER);
        card.add(lblQte, BorderLayout.SOUTH);
        
        return card;
    }
    
    /**
     * Affiche les stocks proches de la péremption
     */
    private void afficherProchesPeremption() {
        List<StockSanguin> stocks = stockDAO.getStocksProchesPeremption(7);
        
        if (stocks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "✅ Aucun stock proche de la péremption (7 jours)");
        } else {
            StringBuilder sb = new StringBuilder("⚠️ Stocks expirant dans les 7 prochains jours :\n\n");
            for (StockSanguin s : stocks) {
                sb.append("• ").append(s.getGroupeSanguin())
                  .append(" - ").append(s.getQuantite()).append(" ml")
                  .append(" (expire le ").append(DateUtils.dateToString(s.getDatePeremption()))
                  .append(")\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Alerte Péremption", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    public void refreshData() {
        // Rafraîchir le résumé
        resumePanel.removeAll();
        for (String groupe : GroupeSanguin.TOUS_LES_GROUPES) {
            int qte = stockDAO.getQuantiteTotaleParGroupe(groupe);
            resumePanel.add(createResumeCard(groupe, qte));
        }
        resumePanel.revalidate();
        resumePanel.repaint();
        
        // Rafraîchir le tableau
        tableModel.setRowCount(0);
        List<StockSanguin> stocks = stockDAO.listerTous();
        
        for (StockSanguin s : stocks) {
            int joursRestants = s.joursAvantPeremption();
            String joursStr = joursRestants < 0 ? "PÉRIMÉ" : joursRestants + " jours";
            
            tableModel.addRow(new Object[]{
                s.getId(),
                s.getGroupeSanguin(),
                s.getQuantite(),
                DateUtils.dateToString(s.getDatePrelevement()),
                DateUtils.dateToString(s.getDatePeremption()),
                joursStr,
                s.getStatut()
            });
        }
    }
}
