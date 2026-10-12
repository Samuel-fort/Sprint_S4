package mg.itu.controllers;

import mg.itu.annotation.Controller;
import mg.itu.annotation.RestAPI;
import mg.itu.annotation.URLMapping;
import mg.itu.framework.ModelView;

import java.util.Arrays;

@Controller
public class DemoController {

    @URLMapping("/demo/view")
    public ModelView view() {
        ModelView mv = new ModelView("/WEB-INF/views/demo.jsp");
        mv.addAttribute("titre", "Premier ModelView");
        mv.addAttribute("items", Arrays.asList("un", "deux", "trois"));
        return mv;
    }

    // @RestAPI + ModelView : le modèle est renvoyé en JSON, aucune page n'est affichée
    @RestAPI
    @URLMapping("/api/demo")
    public ModelView apiView() {
        return new ModelView("/WEB-INF/views/demo.jsp")
                .addAttribute("titre", "Premier ModelView")
                .addAttribute("items", Arrays.asList("un", "deux", "trois"));
    }
}
