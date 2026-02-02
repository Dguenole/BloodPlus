/*
 * Package UTILS : fonctions utilitaires réutilisables
 */
package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe DateUtils : utilitaires pour gérer les dates
 * 
 * 💡 EXPLICATION :
 * En Java, manipuler les dates peut être compliqué
 * Cette classe regroupe les fonctions utiles pour :
 * - Formater une date en texte
 * - Convertir un texte en date
 * - Calculer des différences de dates
 * 
 * @author dteach
 */
public class DateUtils {
    
    // Format de date standard (jour/mois/année)
    public static final String FORMAT_DATE = "dd/MM/yyyy";
    public static final String FORMAT_DATE_HEURE = "dd/MM/yyyy HH:mm";
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE);
    private static final SimpleDateFormat sdfHeure = new SimpleDateFormat(FORMAT_DATE_HEURE);

    /**
     * Convertir une Date en String
     * Ex: Date -> "20/01/2026"
     */
    public static String dateToString(Date date) {
        if (date == null) return "";
        return sdf.format(date);
    }

    /**
     * Convertir une Date en String avec l'heure
     * Ex: Date -> "20/01/2026 14:30"
     */
    public static String dateHeureToString(Date date) {
        if (date == null) return "";
        return sdfHeure.format(date);
    }

    /**
     * Convertir un String en Date
     * Ex: "20/01/2026" -> Date
     */
    public static Date stringToDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            System.err.println("❌ Format de date invalide : " + dateStr);
            return null;
        }
    }

    /**
     * Calculer le nombre de jours entre deux dates
     */
    public static int joursEntre(Date date1, Date date2) {
        long diff = Math.abs(date2.getTime() - date1.getTime());
        return (int) (diff / (24 * 60 * 60 * 1000));
    }

    /**
     * Ajouter des jours à une date
     */
    public static Date ajouterJours(Date date, int jours) {
        return new Date(date.getTime() + (jours * 24L * 60L * 60L * 1000L));
    }

    /**
     * Vérifier si une date est dans le passé
     */
    public static boolean estPassee(Date date) {
        return date.before(new Date());
    }

    /**
     * Vérifier si une date est aujourd'hui
     */
    public static boolean estAujourdhui(Date date) {
        String aujourdhui = dateToString(new Date());
        String dateTest = dateToString(date);
        return aujourdhui.equals(dateTest);
    }
}
