package mg.itu.framework;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Garde dans le ServletContext une Map  id de session HTTP -> Session.
 * Si l'id n'y est pas encore, on y range la session courante.
 */
public final class SessionManager {

    public static final String CONTEXT_ATTRIBUTE = "framework.sessions";

    private SessionManager() {
    }

    @SuppressWarnings("unchecked")
    static Map<String, Session> registry(ServletContext context) {
        synchronized (SessionManager.class) {
            Object registry = context.getAttribute(CONTEXT_ATTRIBUTE);
            if (registry == null) {
                registry = new ConcurrentHashMap<String, Session>();
                context.setAttribute(CONTEXT_ATTRIBUTE, registry);
            }
            return (Map<String, Session>) registry;
        }
    }

    /** La Session du framework correspondant à la session HTTP courante (créée si besoin). */
    public static Session obtain(HttpServletRequest request) {
        HttpSession http = request.getSession();
        Session session = registry(request.getServletContext()).computeIfAbsent(http.getId(), Session::new);
        session.loadFrom(http);
        return session;
    }

    /** À appeler après l'action : recopie la Session dans la session HTTP (ou l'invalide). */
    public static void save(Session session, HttpServletRequest request) {
        HttpSession http = request.getSession();
        if (session.isInvalidated()) {
            registry(request.getServletContext()).remove(session.getId());
            http.invalidate();
            return;
        }
        session.saveTo(http);
    }

    /** Oublie la session (session HTTP expirée ou invalidée). */
    public static void forget(ServletContext context, String sessionId) {
        Object registry = context.getAttribute(CONTEXT_ATTRIBUTE);
        if (registry instanceof Map) {
            ((Map<?, ?>) registry).remove(sessionId);
        }
    }
}
