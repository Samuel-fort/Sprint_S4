package mg.itu.controllers;

import mg.itu.annotation.Controller;
import mg.itu.annotation.URLMapping;
import mg.itu.framework.HttpMethod;
import mg.itu.framework.ModelView;
import mg.itu.models.Etudiant;
import mg.itu.repository.EtudiantRepository;

@Controller
public class EtudiantBeanController {

    @URLMapping("/etudiants/form-bean")
    public ModelView form() {
        return new ModelView("/WEB-INF/views/etudiant-form-bean.jsp");
    }

    // Le formulaire envoie etudiant.nom, etudiant.age, etudiant.email
    // -> le framework construit l'objet Etudiant (nom d'argument = préfixe)
    @URLMapping(value = "/etudiants/save-bean", method = HttpMethod.POST)
    public ModelView save(Etudiant etudiant) {
        EtudiantRepository.save(etudiant);
        return new ModelView("/WEB-INF/views/etudiants.jsp")
                .addAttribute("etudiants", EtudiantRepository.findAll());
    }
}
