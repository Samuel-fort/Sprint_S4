package mg.itu.framework;

import mg.itu.annotation.Controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ClassScanner {

    public static List<Class<?>> getControllerClasses(String packageName) throws Exception {
        List<Class<?>> controllers = new ArrayList<>();

        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);

        if (resource == null) {
            return controllers;
        }

        File directory = new File(resource.toURI());

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".class")) {
                        String simpleName = file.getName().replace(".class", "");
                        String className = packageName + "." + simpleName;

                        Class<?> clazz = Class.forName(className);

                        if (clazz.isAnnotationPresent(Controller.class)) {
                            controllers.add(clazz);
                        }
                    }
                }
            }
        }

        return controllers;
    }
}
