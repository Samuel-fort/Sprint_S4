package mg.itu.controllers;

import mg.itu.annotation.Controller;
import mg.itu.annotation.URLMapping;
import mg.itu.framework.ModelView;
import mg.itu.framework.Session;

@Controller
public class SessionController {

    // L'argument "Session" est rempli par le framework
    @URLMapping("/session/compteur")
    public ModelView compteur(Session session) {
        Integer n = session.get("compteur", Integer.class);
        session.set("compteur", (n == null ? 0 : n) + 1);
        return vue(session);
    }

    @URLMapping("/session/vider")
    public ModelView vider(Session session) {
        session.clear();
        return vue(session);
    }

    private ModelView vue(Session session) {
        return new ModelView("/WEB-INF/views/session.jsp").addAttribute("maSession", session);
    }
}
