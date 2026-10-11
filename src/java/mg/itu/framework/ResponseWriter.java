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

/** Transforme la valeur de retour d'une méthode de contrôleur en réponse HTTP. */
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
            writeJson(action, result, response);
            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        if (result != null) {
            response.getWriter().print(result);
        }
    }

    private static void writeJson(Method action, Object result, HttpServletResponse response)
            throws IOException {
        if (action.getReturnType() == void.class) {
            return;
        }
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        if (result instanceof String) {
            // consigne : si le type de retour est String, on le renvoie tel quel
            response.setContentType("text/plain;charset=UTF-8");
            out.print(result);
        } else {
            response.setContentType("application/json;charset=UTF-8");
            out.print(GSON.toJson(result));
        }
    }
}
