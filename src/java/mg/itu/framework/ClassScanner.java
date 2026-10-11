package mg.itu.framework;

import mg.itu.annotation.Controller;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/** Retrouve, dans un package (sous-packages compris), les classes annotées @Controller. */
public class ClassScanner {

    public static List<Class<?>> getControllerClasses(String packageName) throws Exception {
        List<Class<?>> controllers = new ArrayList<>();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = ClassScanner.class.getClassLoader();
        }

        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            if ("file".equals(url.getProtocol())) {
                scanDirectory(new File(url.toURI()), packageName, classLoader, controllers);
            } else if ("jar".equals(url.getProtocol())) {
                scanJar(url, path, classLoader, controllers);
            }
        }
        return controllers;
    }

    private static void scanDirectory(File directory, String packageName, ClassLoader cl,
                                      List<Class<?>> controllers) throws Exception {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), cl, controllers);
            } else if (file.getName().endsWith(".class")) {
                String simpleName = file.getName().substring(0, file.getName().length() - ".class".length());
                addIfController(packageName + "." + simpleName, cl, controllers);
            }
        }
    }

    private static void scanJar(URL url, String path, ClassLoader cl,
                                List<Class<?>> controllers) throws Exception {
        JarURLConnection connection = (JarURLConnection) url.openConnection();
        connection.setUseCaches(false);
        try (JarFile jar = connection.getJarFile()) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith(path + "/") && name.endsWith(".class")) {
                    String className = name.substring(0, name.length() - ".class".length()).replace('/', '.');
                    addIfController(className, cl, controllers);
                }
            }
        }
    }

    private static void addIfController(String className, ClassLoader cl,
                                        List<Class<?>> controllers) throws Exception {
        if (className.endsWith("package-info") || className.endsWith("module-info")) {
            return;
        }
        Class<?> clazz = Class.forName(className, false, cl);
        if (clazz.isAnnotationPresent(Controller.class) && !controllers.contains(clazz)) {
            controllers.add(clazz);
        }
    }
}
