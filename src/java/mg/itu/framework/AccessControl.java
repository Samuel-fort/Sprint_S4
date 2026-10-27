package mg.itu.framework;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import mg.itu.annotation.Authenticated;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Vérifie, avant d'exécuter une méthode de contrôleur, que l'utilisateur a le droit de l'appeler.
 *
 * Paramètres facultatifs (context-param dans web.xml) :
 *   auth-user-key : attribut de session qui prouve la connexion (défaut "user")
 *   auth-role-key : attribut de session qui contient le ou les rôles (défaut "role")
 *   login-url     : page vers laquelle rediriger un visiteur non connecté (ex. /login)
 */
public final class AccessControl {

    public static final String PARAM_USER_KEY = "auth-user-key";
    public static final String PARAM_ROLE_KEY = "auth-role-key";
    public static final String PARAM_LOGIN_URL = "login-url";
    public static final String DEFAULT_USER_KEY = "user";
    public static final String DEFAULT_ROLE_KEY = "role";

    private AccessControl() {
    }

    /** @throws AccessDeniedException si l'appel est refusé (401 non connecté, 403 rôle insuffisant) */
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

        String[] required = rule.roles();
        if (required.length == 0) {
            return;
        }

        Set<String> owned = rolesOf(http.getAttribute(param(context, PARAM_ROLE_KEY, DEFAULT_ROLE_KEY)));
        for (String role : required) {
            if (owned.contains(role)) {
                return;
            }
        }
        throw new AccessDeniedException(403,
                "Accès refusé : rôle requis : " + String.join(" ou ", required));
    }

    /** Le rôle peut être stocké en String, String[] ou collection de rôles. */
    private static Set<String> rolesOf(Object value) {
        Set<String> roles = new HashSet<>();
        if (value instanceof String) {
            roles.add((String) value);
        } else if (value instanceof String[]) {
            roles.addAll(Arrays.asList((String[]) value));
        } else if (value instanceof Collection) {
            for (Object role : (Collection<?>) value) {
                if (role != null) {
                    roles.add(role.toString());
                }
            }
        }
        return roles;
    }

    static String param(ServletContext context, String name, String defaultValue) {
        String value = context.getInitParameter(name);
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim();
    }
}
