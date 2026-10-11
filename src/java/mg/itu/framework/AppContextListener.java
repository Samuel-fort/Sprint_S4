package mg.itu.framework;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import mg.itu.annotation.URLMapping;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Appelé par Tomcat au démarrage de l'application (pas au déploiement du .war).
 * Scanne les packages de contrôleurs, crée UNE instance par contrôleur et
 * construit la table des routes (URL + verbe) -> méthode.
 *
 * Package(s) à scanner : context-param "controller-package" dans web.xml
 * (plusieurs packages possibles, séparés par des virgules).
 */
public class AppContextListener implements ServletContextListener {

    public static final String PARAM_PACKAGE = "controller-package";
    public static final String DEFAULT_PACKAGE = "mg.itu.controllers";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        String param = context.getInitParameter(PARAM_PACKAGE);
        if (param == null || param.trim().isEmpty()) {
            param = DEFAULT_PACKAGE;
        }

        try {
            List<Class<?>> controllers = new ArrayList<>();
            for (String pkg : param.split(",")) {
                pkg = pkg.trim();
                if (pkg.isEmpty()) {
                    continue;
                }
                for (Class<?> c : ClassScanner.getControllerClasses(pkg)) {
                    if (!controllers.contains(c)) {
                        controllers.add(c);
                    }
                }
            }

            Map<Class<?>, Object> instances = new HashMap<>();
            Map<Mapping, Mapping> urlMappings = new LinkedHashMap<>();

            for (Class<?> controllerClass : controllers) {
                for (Method method : controllerClass.getDeclaredMethods()) {
                    URLMapping annotation = method.getAnnotation(URLMapping.class);
                    if (annotation == null) {
                        continue;
                    }

                    String url = annotation.value();
                    if (!url.startsWith("/")) {
                        url = "/" + url;
                    }

                    Mapping mapping = new Mapping(url, controllerClass.getName(),
                            method.getName(), annotation.method());

                    Mapping previous = urlMappings.get(mapping);
                    if (previous != null) {
                        throw new IllegalStateException("Route en double : " + annotation.method() + " " + url
                                + " déclarée par " + previous.getClassName() + "." + previous.getMethodName()
                                + " et " + controllerClass.getName() + "." + method.getName());
                    }

                    Object instance = instances.get(controllerClass);
                    if (instance == null) {
                        Constructor<?> constructor = controllerClass.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        instance = constructor.newInstance();
                        instances.put(controllerClass, instance);
                    }

                    method.setAccessible(true);
                    mapping.setInstance(instance);
                    mapping.setMethod(method);
                    urlMappings.put(mapping, mapping);
                }
            }

            context.setAttribute("controllers", controllers);
            context.setAttribute("urlMappings", urlMappings);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du scan des controllers : " + e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
