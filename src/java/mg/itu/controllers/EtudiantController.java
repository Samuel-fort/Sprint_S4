package mg.itu.controllers;

import mg.itu.annotation.Controller;
import mg.itu.annotation.RestAPI;
import mg.itu.annotation.URLMapping;
import mg.itu.framework.HttpMethod;
import mg.itu.framework.ModelView;
import mg.itu.models.Etudiant;
import mg.itu.repository.EtudiantRepository;

import java.util.List;

@Controller
public class EtudiantController {

    @URLMapping("/etudiants/form")
    public ModelView form() {
        return new ModelView("/WEB-INF/views/etudiant-form.jsp");
    }

    // Les arguments sont des types de base, remplis depuis les champs du formulaire
    @URLMapping(value = "/etudiants/save", method = HttpMethod.POST)
    public ModelView save(String nom, int age, String email) {
        EtudiantRepository.save(new Etudiant(nom, age, email));
        return liste();
    }

    @URLMapping("/etudiants")
    public ModelView liste() {
        return new ModelView("/WEB-INF/views/etudiants.jsp")
                .addAttribute("etudiants", EtudiantRepository.findAll());
    }

    @RestAPI
    @URLMapping("/api/etudiants")
    public List<Etudiant> apiListe() {
        return EtudiantRepository.findAll();
    }
}
