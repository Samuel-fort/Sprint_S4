package mg.itu.models;

import mg.itu.framework.Mapfile;

public class Personne {

    private String nom;
    private Mapfile recto;
    private Mapfile verso;

    public Personne() {
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Mapfile getRecto() { return recto; }
    public void setRecto(Mapfile recto) { this.recto = recto; }

    public Mapfile getVerso() { return verso; }
    public void setVerso(Mapfile verso) { this.verso = verso; }
}
