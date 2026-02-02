/*
 * Package UI
 */
package ui;

import dao.ActionLogDAO;
import dao.UtilisateurDAO;
import model.Utilisateur;
import service.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * LoginFrame : Fenêtre de connexion
 * 
 * 💡 EXPLICATION :
 * C'est la première fenêtre qui s'affiche au lancement
 * L'utilisateur doit s'authentifier pour accéder à l'application
 * 
 * @author dteach
 */
public class LoginFrame extends JFrame {
    
    // Composants du formulaire
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnConnexion;
    private JLabel lblMessage;
    
    // DAO pour l'authentification
    private UtilisateurDAO utilisateurDAO;
    private ActionLogDAO actionLogDAO;

    public LoginFrame() {
        utilisateurDAO = new UtilisateurDAO();
        actionLogDAO = new ActionLogDAO();
        initComponents();
        setupWindow();
    }
    
    private void setupWindow() {
        setTitle("🩸 BloodPlus - Connexion");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Centrer à l'écran
        setResizable(false);
    }
    
    private void initComponents() {
        // Panel principal avec fond
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(52, 58, 64));
        
        // ========== HEADER (Logo) ==========
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(220, 53, 69));  // Rouge sang
        headerPanel.setPreferredSize(new Dimension(400, 120));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        JLabel lblLogo = new JLabel("🩸");
        lblLogo.setFont(new Font("Arial", Font.PLAIN, 50));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblTitle = new JLabel("BloodPlus");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSubtitle = new JLabel("Banque de Sang Numérique");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(255, 255, 255, 200));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(Box.createVerticalStrut(15));
        headerPanel.add(lblLogo);
        headerPanel.add(lblTitle);
        headerPanel.add(lblSubtitle);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // ========== FORMULAIRE ==========
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(52, 58, 64));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        
        // Label "Connexion"
        JLabel lblConnexion = new JLabel("Connexion", SwingConstants.CENTER);
        lblConnexion.setFont(new Font("Arial", Font.BOLD, 20));
        lblConnexion.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(lblConnexion, gbc);
        
        // Espace
        gbc.gridy = 1;
        formPanel.add(Box.createVerticalStrut(20), gbc);
        
        // Label Username
        JLabel lblUsername = new JLabel("👤 Nom d'utilisateur");
        lblUsername.setForeground(Color.WHITE);
        lblUsername.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridy = 2;
        formPanel.add(lblUsername, gbc);
        
        // Champ Username
        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUsername.setPreferredSize(new Dimension(200, 35));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridy = 3;
        formPanel.add(txtUsername, gbc);
        
        // Label Password
        JLabel lblPassword = new JLabel("🔒 Mot de passe");
        lblPassword.setForeground(Color.WHITE);
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridy = 4;
        formPanel.add(lblPassword, gbc);
        
        // Champ Password
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(200, 35));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridy = 5;
        formPanel.add(txtPassword, gbc);
        
        // Espace
        gbc.gridy = 6;
        formPanel.add(Box.createVerticalStrut(15), gbc);
        
        // Bouton Connexion
        btnConnexion = new JButton("Se connecter");
        btnConnexion.setFont(new Font("Arial", Font.BOLD, 14));
        btnConnexion.setBackground(new Color(220, 53, 69));
        btnConnexion.setForeground(Color.WHITE);
        btnConnexion.setFocusPainted(false);
        btnConnexion.setBorderPainted(false);
        btnConnexion.setPreferredSize(new Dimension(200, 40));
        btnConnexion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 7;
        formPanel.add(btnConnexion, gbc);
        
        // Message d'erreur
        lblMessage = new JLabel(" ", SwingConstants.CENTER);
        lblMessage.setFont(new Font("Arial", Font.ITALIC, 12));
        lblMessage.setForeground(new Color(255, 100, 100));
        gbc.gridy = 8;
        formPanel.add(lblMessage, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // ========== FOOTER ==========
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(52, 58, 64));
        JLabel lblFooter = new JLabel("© 2026 BloodPlus - DAS");
        lblFooter.setForeground(new Color(150, 150, 150));
        lblFooter.setFont(new Font("Arial", Font.PLAIN, 10));
        footerPanel.add(lblFooter);
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // ========== ÉVÉNEMENTS ==========
        
        // Connexion au clic sur le bouton
        btnConnexion.addActionListener(e -> tentativeConnexion());
        
        // Connexion avec la touche Entrée
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    tentativeConnexion();
                }
            }
        });
        
        txtUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtPassword.requestFocus();
                }
            }
        });
        
        // Effet hover sur le bouton
        btnConnexion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnConnexion.setBackground(new Color(200, 35, 51));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btnConnexion.setBackground(new Color(220, 53, 69));
            }
        });
    }
    
    /**
     * Tente de connecter l'utilisateur
     */
    private void tentativeConnexion() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }
        
        // Désactiver le bouton pendant la vérification
        btnConnexion.setEnabled(false);
        btnConnexion.setText("Connexion...");
        
        // Tentative d'authentification
        SwingWorker<Utilisateur, Void> worker = new SwingWorker<Utilisateur, Void>() {
            @Override
            protected Utilisateur doInBackground() {
                return utilisateurDAO.authentifier(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    Utilisateur user = get();
                    
                    if (user != null) {
                        // Connexion réussie !
                        Session.getInstance().connecter(user);
                        
                        // Enregistrer l'action de connexion
                        actionLogDAO.logConnexion();
                        
                        // Ouvrir la fenêtre principale
                        MainFrame mainFrame = new MainFrame();
                        mainFrame.setVisible(true);
                        
                        // Fermer la fenêtre de login
                        dispose();
                        
                    } else {
                        // Échec de connexion
                        showError("Nom d'utilisateur ou mot de passe incorrect");
                        txtPassword.setText("");
                        txtPassword.requestFocus();
                    }
                    
                } catch (Exception e) {
                    showError("Erreur de connexion à la base de données");
                    e.printStackTrace();
                } finally {
                    btnConnexion.setEnabled(true);
                    btnConnexion.setText("Se connecter");
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Affiche un message d'erreur
     */
    private void showError(String message) {
        lblMessage.setText("❌ " + message);
        
        // Effet de shake (animation simple)
        Timer timer = new Timer(50, null);
        final int[] count = {0};
        final int originalX = getX();
        
        timer.addActionListener(e -> {
            count[0]++;
            if (count[0] <= 6) {
                setLocation(originalX + (count[0] % 2 == 0 ? 5 : -5), getY());
            } else {
                setLocation(originalX, getY());
                timer.stop();
            }
        });
        timer.start();
    }
    
    /**
     * Point d'entrée pour tester LoginFrame
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
