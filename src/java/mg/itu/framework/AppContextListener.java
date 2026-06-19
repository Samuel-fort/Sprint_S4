package mg.itu.framework;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.util.List;

/**
 * Listener exécuté au démarrage de l'application.
 * Scanne le package des contrôleurs et stocke la liste des classes
 * annotées @Controller dans le ServletContext, pour que FrontServlet
 * puisse y accéder.
 */
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        try {
            List<Class<?>> controllers = ClassScanner.getControllerClasses("mg.itu.controllers");
            context.setAttribute("controllers", controllers);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du scan des controllers", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // rien à faire pour l'instant
    }
}
