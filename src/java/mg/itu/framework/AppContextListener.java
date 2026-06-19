package mg.itu.framework;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import mg.itu.annotation.URLMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        try {
            List<Class<?>> controllers = ClassScanner.getControllerClasses("mg.itu.controllers");
            context.setAttribute("controllers", controllers);

            List<Mapping> urlMappings = new ArrayList<>();

            for (Class<?> controllerClass : controllers) {
                Method[] methods = controllerClass.getDeclaredMethods();

                for (Method method : methods) {
                    if (method.isAnnotationPresent(URLMapping.class)) {
                        URLMapping annotation = method.getAnnotation(URLMapping.class);
                        String url = annotation.value();

                        Mapping mapping = new Mapping(
                                url,
                                controllerClass.getName(),
                                method.getName()
                        );

                        urlMappings.add(mapping);
                    }
                }
            }

            context.setAttribute("urlMappings", urlMappings);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du scan des controllers", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        
    }
}
