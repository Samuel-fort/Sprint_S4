package mg.itu.controllers;

import mg.itu.annotation.Controller;
import mg.itu.annotation.URLMapping;
import mg.itu.framework.HttpMethod;
import mg.itu.framework.ModelView;
import mg.itu.models.Personne;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class UploadController {

    @URLMapping("/upload/form")
    public ModelView form() {
        return new ModelView("/WEB-INF/views/upload-form.jsp");
    }

    // Option 1 : l'argument Map<String, Object> reçoit tous les champs du formulaire
    // (String, ou String[] s'il y a plusieurs valeurs) et chaque fichier sous forme de Mapfile.
    @URLMapping(value = "/upload/map", method = HttpMethod.POST)
    public ModelView uploadMap(Map<String, Object> donnees) {
        return new ModelView("/WEB-INF/views/upload-result.jsp")
                .addAttribute("titre", "Option 1 : Map<String, Object>")
                .addAttribute("donnees", donnees);
    }

    // Option 2 : la classe Personne a des attributs de type Mapfile (recto, verso).
    // Champs du formulaire : personne.nom, personne.recto, personne.verso
    @URLMapping(value = "/upload/bean", method = HttpMethod.POST)
    public ModelView uploadBean(Personne personne) {
        Map<String, Object> donnees = new LinkedHashMap<>();
        donnees.put("nom", personne.getNom());
        donnees.put("recto", personne.getRecto());
        donnees.put("verso", personne.getVerso());
        return new ModelView("/WEB-INF/views/upload-result.jsp")
                .addAttribute("titre", "Option 2 : objet avec attributs Mapfile")
                .addAttribute("donnees", donnees);
    }
}
