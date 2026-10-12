package mg.itu.models;

public class Etudiant {

    private String nom;
    private int age;
    private String email;

    public Etudiant() {
    }

    public Etudiant(String nom, int age, String email) {
        this.nom = nom;
        this.age = age;
        this.email = email;
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "Etudiant{nom='" + nom + "', age=" + age + ", email='" + email + "'}";
    }
}
