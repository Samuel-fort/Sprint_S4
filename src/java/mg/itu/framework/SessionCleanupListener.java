package mg.itu.framework;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/** Retire la Session du ServletContext quand la session HTTP expire ou est invalidée. */
public class SessionCleanupListener implements HttpSessionListener {

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        SessionManager.forget(event.getSession().getServletContext(), event.getSession().getId());
    }
}
