package mg.itu.repository;

import mg.itu.models.Etudiant;

import java.util.ArrayList;
import java.util.List;

/**
 * Stockage EN MÉMOIRE (perdu au redémarrage). Pour écrire en base, remplacer
 * le corps de save()/findAll() par du JDBC en gardant les mêmes signatures.
 */
public final class EtudiantRepository {

    private static final List<Etudiant> DATA = new ArrayList<>();

    private EtudiantRepository() {
    }

    public static synchronized void save(Etudiant etudiant) {
        DATA.add(etudiant);
    }

    public static synchronized List<Etudiant> findAll() {
        return new ArrayList<>(DATA);
    }
}
