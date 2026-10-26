package mg.itu.framework;

/**
 * Un fichier envoyé par un formulaire (enctype="multipart/form-data").
 *  - nom       : nom du fichier SANS l'extension   (photo.png -> "photo")
 *  - extension : extension SANS le point           (photo.png -> "png")
 *  - contenu   : tout le contenu du fichier, en octets
 */
public class Mapfile {

    private String nom;
    private String extension;
    private byte[] contenu;

    public Mapfile() {
    }

    public Mapfile(String nom, String extension, byte[] contenu) {
        this.nom = nom;
        this.extension = extension;
        this.contenu = contenu;
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }

    public byte[] getContenu() { return contenu; }
    public void setContenu(byte[] contenu) { this.contenu = contenu; }

    /** Taille du fichier en octets. */
    public int getTaille() {
        return contenu == null ? 0 : contenu.length;
    }

    /** Nom + extension : "photo.png". */
    public String getNomComplet() {
        if (extension == null || extension.isEmpty()) {
            return nom;
        }
        return nom + "." + extension;
    }

    @Override
    public String toString() {
        return "Mapfile{nom='" + nom + "', extension='" + extension + "', taille=" + getTaille() + "}";
    }
}
