package mg.itu.framework;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import mg.itu.annotation.Authenticated;

import java.lang.reflect.Method;

/**
 * Vérifie, avant d'exécuter une méthode de contrôleur, que l'utilisateur a le droit de l'appeler.
 *
 * Paramètres facultatifs (context-param dans web.xml) :
 *   auth-user-key : attribut de session qui prouve la connexion (défaut "user")
 *   login-url     : page vers laquelle rediriger un visiteur non connecté (ex. /login)
 */
public final class AccessControl {

    public static final String PARAM_USER_KEY = "auth-user-key";
    public static final String PARAM_LOGIN_URL = "login-url";
    public static final String DEFAULT_USER_KEY = "user";

    private AccessControl() {
    }

    /** @throws AccessDeniedException si l'appel est refusé */
    public static void check(Method action, HttpServletRequest request) {
        Authenticated rule = action.getAnnotation(Authenticated.class);
        if (rule == null) {
            rule = action.getDeclaringClass().getAnnotation(Authenticated.class);
        }
        if (rule == null) {
            return;
        }

        ServletContext context = request.getServletContext();
        HttpSession http = request.getSession(false);
        if (http == null || http.getAttribute(param(context, PARAM_USER_KEY, DEFAULT_USER_KEY)) == null) {
            throw new AccessDeniedException(401, "Vous devez être connecté pour accéder à cette page");
        }
    }

    static String param(ServletContext context, String name, String defaultValue) {
        String value = context.getInitParameter(name);
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim();
    }
}
