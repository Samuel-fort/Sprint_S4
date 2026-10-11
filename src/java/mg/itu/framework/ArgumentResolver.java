package mg.itu.framework;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Method;

/** Construit le tableau d'arguments à passer à la méthode du contrôleur. */
public final class ArgumentResolver {

    private ArgumentResolver() {
    }

    public static Object[] resolve(Method action, HttpServletRequest request, HttpServletResponse response) {
        if (action.getParameterCount() != 0) {
            throw new IllegalStateException("Paramètres de méthode non supportés avant le sprint 7 : "
                    + action.getDeclaringClass().getName() + "." + action.getName());
        }
        return new Object[0];
    }
}
