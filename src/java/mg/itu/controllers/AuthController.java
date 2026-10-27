package mg.itu.controllers;

import mg.itu.annotation.Authenticated;
import mg.itu.annotation.Controller;
import mg.itu.annotation.URLMapping;
import mg.itu.framework.HttpMethod;
import mg.itu.framework.ModelView;
import mg.itu.framework.Session;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {

    // Comptes de DÉMONSTRATION (login -> mot de passe) : à remplacer par une vraie base de données.
    private static final Map<String, String> COMPTES = new HashMap<>();

    static {
        COMPTES.put("admin", "admin");
        COMPTES.put("rakoto", "1234");
    }

    @URLMapping("/login")
    public ModelView loginForm() {
        return new ModelView("/WEB-INF/views/login.jsp");
    }

    @URLMapping(value = "/login", method = HttpMethod.POST)
    public ModelView login(String login, String motdepasse, Session session) {
        String attendu = login == null ? null : COMPTES.get(login);
        if (attendu == null || !attendu.equals(motdepasse)) {
            return new ModelView("/WEB-INF/views/login.jsp")
                    .addAttribute("erreur", "Identifiant ou mot de passe incorrect");
        }
        session.set("user", login);
        return new ModelView("/WEB-INF/views/secret.jsp").addAttribute("user", login);
    }

    @URLMapping("/logout")
    public ModelView logout(Session session) {
        session.invalidate();
        return new ModelView("/WEB-INF/views/login.jsp").addAttribute("info", "Vous êtes déconnecté");
    }

    // Réservé aux utilisateurs connectés
    @Authenticated
    @URLMapping("/secret")
    public ModelView secret(Session session) {
        return new ModelView("/WEB-INF/views/secret.jsp").addAttribute("user", session.get("user"));
    }
}
