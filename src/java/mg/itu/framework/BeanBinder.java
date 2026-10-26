package mg.itu.framework;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;

/**
 * Remplit un objet à partir des paramètres "prefixe.champ" de la requête.
 * Exemple : etudiant.nom=Rakoto, etudiant.age=20  ->  Etudiant(nom, age).
 * Objets imbriqués supportés : etudiant.adresse.ville=Tana.
 * Un setter public est utilisé s'il existe, sinon le champ est écrit directement.
 * Un champ de type Mapfile reçoit le fichier envoyé dans "prefixe.champ" (formulaire multipart).
 */
public final class BeanBinder {

    private BeanBinder() {
    }

    /** Un type "objet" que l'on sait instancier (ni primitif, ni java.*, ni interface/abstrait). */
    public static boolean isBean(Class<?> type) {
        return !type.isPrimitive() && !type.isArray() && !type.isInterface() && !type.isEnum()
                && !Modifier.isAbstract(type.getModifiers()) && !type.getName().startsWith("java.");
    }

    /** @param top true pour l'argument de la méthode (toujours instancié), false pour un objet imbriqué. */
    public static Object bind(Class<?> type, String prefix, HttpServletRequest request, boolean top) {
        if (!top && !hasParameterWithPrefix(request, prefix + ".")
                && !FileUpload.hasFileWithPrefix(request, prefix + ".")) {
            return null;
        }

        Object bean = instantiate(type);

        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod) || field.isSynthetic()) {
                    continue;
                }

                String key = prefix + "." + field.getName();
                Class<?> fieldType = field.getType();
                Object value;

                if (fieldType == Mapfile.class) {
                    value = FileUpload.read(request, key);
                    if (value == null) {
                        continue;
                    }
                } else if (TypeConverter.isSimple(fieldType)) {
                    String raw = request.getParameter(key);
                    if (raw == null) {
                        continue;
                    }
                    value = TypeConverter.convert(raw, fieldType, key);
                } else if (TypeConverter.isSimpleArray(fieldType)) {
                    value = TypeConverter.convertArray(request.getParameterValues(key), fieldType, key);
                    if (value == null) {
                        continue;
                    }
                } else if (isBean(fieldType)) {
                    value = bind(fieldType, key, request, false);
                    if (value == null) {
                        continue;
                    }
                } else {
                    continue;
                }
                assign(bean, field, value);
            }
        }
        return bean;
    }

    private static Object instantiate(Class<?> type) {
        try {
            Constructor<?> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Impossible d'instancier " + type.getName()
                    + " : un constructeur sans argument est requis", e);
        }
    }

    private static void assign(Object bean, Field field, Object value) {
        try {
            String name = field.getName();
            String setterName = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
            for (Method method : bean.getClass().getMethods()) {
                if (method.getName().equals(setterName) && method.getParameterCount() == 1
                        && method.getParameterTypes()[0] == field.getType()) {
                    method.invoke(bean, value);
                    return;
                }
            }
            field.setAccessible(true);
            field.set(bean, value);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            throw new IllegalArgumentException("Valeur refusée pour '" + field.getName() + "' : " + cause.getMessage());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Accès impossible au champ " + field.getName(), e);
        }
    }

    private static boolean hasParameterWithPrefix(HttpServletRequest request, String prefix) {
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            if (names.nextElement().startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
