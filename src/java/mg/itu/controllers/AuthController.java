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

    // Comptes de DÉMONSTRATION (login -> {mot de passe, rôle}) : à remplacer par une vraie base de données.
    private static final Map<String, String[]> COMPTES = new HashMap<>();

    static {
        COMPTES.put("admin", new String[]{"admin", "ADMIN"});
        COMPTES.put("rakoto", new String[]{"1234", "USER"});
    }

    @URLMapping("/login")
    public ModelView loginForm() {
        return new ModelView("/WEB-INF/views/login.jsp");
    }

    @URLMapping(value = "/login", method = HttpMethod.POST)
    public ModelView login(String login, String motdepasse, Session session) {
        String[] compte = login == null ? null : COMPTES.get(login);
        if (compte == null || !compte[0].equals(motdepasse)) {
            return new ModelView("/WEB-INF/views/login.jsp")
                    .addAttribute("erreur", "Identifiant ou mot de passe incorrect");
        }
        session.set("user", login);
        session.set("role", compte[1]);
        return secretView(session);
    }

    @URLMapping("/logout")
    public ModelView logout(Session session) {
        session.invalidate();
        return new ModelView("/WEB-INF/views/login.jsp").addAttribute("info", "Vous êtes déconnecté");
    }

    // Réservé aux utilisateurs connectés, quel que soit leur rôle
    @Authenticated
    @URLMapping("/secret")
    public ModelView secret(Session session) {
        return secretView(session);
    }

    // Réservé aux utilisateurs connectés ayant le rôle ADMIN
    @Authenticated(roles = {"ADMIN"})
    @URLMapping("/admin")
    public ModelView admin(Session session) {
        return new ModelView("/WEB-INF/views/admin.jsp").addAttribute("user", session.get("user"));
    }

    private ModelView secretView(Session session) {
        return new ModelView("/WEB-INF/views/secret.jsp")
                .addAttribute("user", session.get("user"))
                .addAttribute("role", session.get("role"));
    }
}
