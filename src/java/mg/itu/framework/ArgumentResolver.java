package mg.itu.framework;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mg.itu.annotation.Param;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Construit le tableau d'arguments de la méthode du contrôleur à partir de la requête.
 *  - HttpServletRequest / HttpServletResponse / HttpSession : injectés
 *  - Session (framework) : la session sous forme de Map, voir SessionManager
 *  - types simples et tableaux de types simples : paramètre de formulaire "nom"
 *  - objets (ex. Etudiant) : paramètres "nom.champ" (voir BeanBinder)
 *  - Mapfile : fichier envoyé dans le champ "nom" (formulaire multipart)
 *  - Map<String, Object> : tous les champs du formulaire + les fichiers (Mapfile)
 */
public final class ArgumentResolver {

    private ArgumentResolver() {
    }

    public static Object[] resolve(Method action, HttpServletRequest request, HttpServletResponse response) {
        Parameter[] parameters = action.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Class<?> type = parameters[i].getType();

            if (type == HttpServletRequest.class) {
                args[i] = request;
            } else if (type == HttpServletResponse.class) {
                args[i] = response;
            } else if (type == HttpSession.class) {
                args[i] = request.getSession();
            } else if (type == Session.class) {
                args[i] = SessionManager.obtain(request);
            } else if (type == Map.class) {
                args[i] = formData(request);
            } else {
                String name = nameOf(parameters[i], action);
                if (type == Mapfile.class) {
                    args[i] = FileUpload.read(request, name);
                } else if (TypeConverter.isSimple(type)) {
                    args[i] = TypeConverter.convert(request.getParameter(name), type, name);
                } else if (TypeConverter.isSimpleArray(type)) {
                    args[i] = TypeConverter.convertArray(request.getParameterValues(name), type, name);
                } else if (BeanBinder.isBean(type)) {
                    args[i] = BeanBinder.bind(type, name, request, true);
                } else {
                    throw new IllegalStateException("Type d'argument non supporté pour '" + name + "' : "
                            + type.getName() + " (" + action.getDeclaringClass().getSimpleName()
                            + "." + action.getName() + ")");
                }
            }
        }
        return args;
    }

    /** Option 1 : champs texte (String, ou String[] si plusieurs valeurs) + fichiers (Mapfile). */
    private static Map<String, Object> formData(HttpServletRequest request) {
        Map<String, Object> data = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String[] values = entry.getValue();
            data.put(entry.getKey(), values.length == 1 ? values[0] : values);
        }
        data.putAll(FileUpload.readAll(request));
        return data;
    }

    static String nameOf(Parameter parameter, Method action) {
        Param annotation = parameter.getAnnotation(Param.class);
        if (annotation != null && !annotation.value().isEmpty()) {
            return annotation.value();
        }
        if (parameter.isNamePresent()) {
            return parameter.getName();
        }
        throw new IllegalStateException("Nom d'argument introuvable pour "
                + action.getDeclaringClass().getName() + "." + action.getName()
                + " : compilez l'application avec 'javac -parameters' ou ajoutez @Param(\"nom\")");
    }
}
