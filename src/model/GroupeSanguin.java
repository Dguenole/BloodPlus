/*
 * Package MODEL : contient les "objets métier"
 */
package model;

/**
 * Classe GroupeSanguin : représente un groupe sanguin
 * 
 * 💡 EXPLICATION :
 * Il existe 8 groupes sanguins : A+, A-, B+, B-, AB+, AB-, O+, O-
 * Chaque groupe peut donner à certains groupes et recevoir de certains autres
 * 
 * @author dteach
 */
public class GroupeSanguin {
    
    // ============ ATTRIBUTS ============
    private int id;
    private String code;        // Ex: "A+", "O-"
    private String description; // Ex: "A Positif"

    // ============ LES 8 GROUPES SANGUINS (constantes) ============
    public static final String A_POSITIF = "A+";
    public static final String A_NEGATIF = "A-";
    public static final String B_POSITIF = "B+";
    public static final String B_NEGATIF = "B-";
    public static final String AB_POSITIF = "AB+";
    public static final String AB_NEGATIF = "AB-";
    public static final String O_POSITIF = "O+";
    public static final String O_NEGATIF = "O-";
    
    // Tableau de tous les groupes (utile pour les listes déroulantes)
    public static final String[] TOUS_LES_GROUPES = {
        A_POSITIF, A_NEGATIF,
        B_POSITIF, B_NEGATIF,
        AB_POSITIF, AB_NEGATIF,
        O_POSITIF, O_NEGATIF
    };

    // ============ CONSTRUCTEURS ============
    
    public GroupeSanguin() {
    }

    public GroupeSanguin(String code) {
        this.code = code;
    }

    // ============ GETTERS & SETTERS ============
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // ============ MÉTHODES DE COMPATIBILITÉ ============
    
    /**
     * 💡 RÈGLE IMPORTANTE DE COMPATIBILITÉ SANGUINE
     * Vérifie si un groupe donneur peut donner à un groupe receveur
     * 
     * @param groupeDonneur Le groupe du donneur
     * @param groupeReceveur Le groupe du receveur
     * @return true si le don est compatible
     */
    public static boolean estCompatible(String groupeDonneur, String groupeReceveur) {
        // O- est donneur universel (peut donner à tout le monde)
        if (O_NEGATIF.equals(groupeDonneur)) {
            return true;
        }
        
        // AB+ est receveur universel (peut recevoir de tout le monde)
        if (AB_POSITIF.equals(groupeReceveur)) {
            return true;
        }
        
        // Même groupe = toujours compatible
        if (groupeDonneur.equals(groupeReceveur)) {
            return true;
        }
        
        // Règles spécifiques
        switch (groupeDonneur) {
            case O_POSITIF:
                return groupeReceveur.endsWith("+"); // O+ donne aux positifs
            case A_NEGATIF:
                return A_POSITIF.equals(groupeReceveur) || 
                       AB_NEGATIF.equals(groupeReceveur) || 
                       AB_POSITIF.equals(groupeReceveur);
            case A_POSITIF:
                return AB_POSITIF.equals(groupeReceveur);
            case B_NEGATIF:
                return B_POSITIF.equals(groupeReceveur) || 
                       AB_NEGATIF.equals(groupeReceveur) || 
                       AB_POSITIF.equals(groupeReceveur);
            case B_POSITIF:
                return AB_POSITIF.equals(groupeReceveur);
            case AB_NEGATIF:
                return AB_POSITIF.equals(groupeReceveur);
            default:
                return false;
        }
    }

    /**
     * Retourne la liste des groupes qui peuvent DONNER à ce groupe
     */
    public static String[] getDonneursCompatibles(String groupeReceveur) {
        switch (groupeReceveur) {
            case O_NEGATIF:
                return new String[]{O_NEGATIF};
            case O_POSITIF:
                return new String[]{O_NEGATIF, O_POSITIF};
            case A_NEGATIF:
                return new String[]{O_NEGATIF, A_NEGATIF};
            case A_POSITIF:
                return new String[]{O_NEGATIF, O_POSITIF, A_NEGATIF, A_POSITIF};
            case B_NEGATIF:
                return new String[]{O_NEGATIF, B_NEGATIF};
            case B_POSITIF:
                return new String[]{O_NEGATIF, O_POSITIF, B_NEGATIF, B_POSITIF};
            case AB_NEGATIF:
                return new String[]{O_NEGATIF, A_NEGATIF, B_NEGATIF, AB_NEGATIF};
            case AB_POSITIF:
                return TOUS_LES_GROUPES; // Receveur universel
            default:
                return new String[]{};
        }
    }

    @Override
    public String toString() {
        return code;
    }
}
