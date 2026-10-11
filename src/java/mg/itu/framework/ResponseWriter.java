package mg.itu.framework;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.annotation.RestAPI;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Transforme la valeur de retour d'une méthode de contrôleur en réponse HTTP :
 *  - @RestAPI            -> JSON (ou texte brut si String). Un ModelView est
 *                           envoyé sous forme de JSON de son modèle.
 *  - ModelView           -> attributs de la requête + forward vers la page
 *  - autre (String, ...) -> écrit tel quel dans la réponse
 */
public final class ResponseWriter {

    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(LocalDate.class,
                    (JsonSerializer<LocalDate>) (src, type, ctx) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonSerializer<LocalDateTime>) (src, type, ctx) -> new JsonPrimitive(src.toString()))
            .create();

    private ResponseWriter() {
    }

    public static void write(Method action, Object result,
                             HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        if (action.isAnnotationPresent(RestAPI.class)) {
            Object payload = (result instanceof ModelView) ? ((ModelView) result).getModel() : result;
            writeJson(action, payload, response);
            return;
        }

        if (result instanceof ModelView) {
            forward((ModelView) result, request, response);
            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        if (result != null) {
            response.getWriter().print(result);
        }
    }

    private static void forward(ModelView mv, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String view = mv.getUrl();
        if (view == null || view.trim().isEmpty()) {
            throw new IllegalStateException("ModelView sans url : indiquez la page à afficher");
        }
        if (!view.startsWith("/")) {
            view = "/" + view;
        }
        for (Map.Entry<String, Object> entry : mv.getModel().entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }
        request.getRequestDispatcher(view).forward(request, response);
    }

    private static void writeJson(Method action, Object payload, HttpServletResponse response)
            throws IOException {
        if (action.getReturnType() == void.class) {
            return;
        }
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        if (payload instanceof String) {
            // consigne : si le type de retour est String, on le renvoie tel quel
            response.setContentType("text/plain;charset=UTF-8");
            out.print(payload);
        } else {
            response.setContentType("application/json;charset=UTF-8");
            out.print(GSON.toJson(payload));
        }
    }
}
