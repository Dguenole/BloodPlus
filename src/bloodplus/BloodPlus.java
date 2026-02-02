/*
 * BloodPlus - Application de Gestion de Banque de Sang
 * Projet scolaire - 2026
 */
package bloodplus;

import ui.LoginFrame;
import javax.swing.*;

/**
 * Classe principale de l'application BloodPlus
 * C'est le point d'entrée de ton application (là où tout commence!)
 * 
 * 💡 FLUX DE L'APPLICATION :
 * 1. L'utilisateur voit la page de connexion (LoginFrame)
 * 2. Après authentification, il accède à l'application principale (MainFrame)
 * 3. Les permissions sont appliquées selon son rôle (ADMIN, OPERATEUR, LECTEUR)
 * 
 * @author dteach
 */
public class BloodPlus {

    /**
     * Méthode main : c'est ici que l'application démarre
     * @param args les arguments de la ligne de commande (on ne les utilise pas ici)
     */
    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("   🩸 Bienvenue dans BloodPlus   ");
        System.out.println("   Banque de Sang Numérique      ");
        System.out.println("=================================");
        System.out.println("");
        System.out.println("🔐 Authentification requise...");
        System.out.println("");
        
        // Utiliser le look and feel du système (plus joli)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Impossible de charger le look and feel système");
        }
        
        // Lancer l'écran de connexion sur le thread EDT
        // 💡 Swing doit être exécuté sur l'Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // Afficher la page de connexion
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
