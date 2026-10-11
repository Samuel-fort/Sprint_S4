package mg.itu.framework;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Front Controller : toutes les requêtes passent ici (url-pattern "/").
 * 1. cherche la route (URL + verbe) construite par AppContextListener
 * 2. résout les arguments, invoque la méthode, écrit la réponse
 * 3. sinon : ancien mapping XML (sprint 0), /controllers (sprint 1),
 *    fichiers statiques, 405 ou 404.
 */
public class FrontServlet extends HttpServlet {

    private final List<Mapping> xmlMappings = new ArrayList<>();
    private List<Class<?>> controllers = new ArrayList<>();
    private Map<Mapping, Mapping> urlMappings = new LinkedHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public void init() throws ServletException {
        try {
            loadXmlMappings();

            Object c = getServletContext().getAttribute("controllers");
            if (c != null) {
                controllers = (List<Class<?>>) c;
            }
            Object m = getServletContext().getAttribute("urlMappings");
            if (m != null) {
                urlMappings = (Map<Mapping, Mapping>) m;
            }
        } catch (Exception e) {
            throw new ServletException("Erreur lors de l'initialisation du FrontServlet", e);
        }
    }

    private void loadXmlMappings() throws Exception {
        try (InputStream xmlFile = getServletContext().getResourceAsStream("/WEB-INF/app.xml")) {
            if (xmlFile == null) {
                return;
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("controller-url");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                String url = element.getElementsByTagName("url").item(0).getTextContent().trim();
                String className = element.getElementsByTagName("className").item(0).getTextContent().trim();
                xmlMappings.add(new Mapping(url, className));
            }
        }
    }

    // service() plutôt que doGet/doPost : tous les verbes passent par le routeur
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String path = pathOf(request);

        try {
            Mapping mapping = findMapping(path, parseVerb(request.getMethod()), request.getMethod());
            execute(mapping, request, response);

        } catch (HttpMethodNotSupportedException e) {
            response.setHeader("Allow", join(e.getAllowed()));
            sendError(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Méthode non autorisée", e.getMessage());

        } catch (UrlNotSupportedException e) {
            handleUnknown(path, e, request, response);
        }
    }

    // ------------------------------------------------------------------ routage

    private Mapping findMapping(String path, HttpMethod verb, String rawVerb)
            throws UrlNotSupportedException, HttpMethodNotSupportedException {

        if (verb != null) {
            Mapping m = urlMappings.get(Mapping.key(path, verb));
            if (m == null && verb == HttpMethod.HEAD) {
                m = urlMappings.get(Mapping.key(path, HttpMethod.GET));
            }
            if (m != null) {
                return m;
            }
        }

        List<HttpMethod> allowed = new ArrayList<>();
        for (Mapping m : urlMappings.values()) {
            if (m.getUrl().equals(path)) {
                allowed.add(m.getHttpMethod());
            }
        }
        if (!allowed.isEmpty()) {
            throw new HttpMethodNotSupportedException(
                    "La méthode " + rawVerb + " n'est pas supportée pour " + path
                            + " (autorisées : " + join(allowed) + ")", allowed);
        }
        throw new UrlNotSupportedException("URL not supported : " + path);
    }

    private void execute(Mapping mapping, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Object[] args = ArgumentResolver.resolve(mapping.getMethod(), request, response);
            Object result = mapping.getMethod().invoke(mapping.getInstance(), args);
            ResponseWriter.write(mapping.getMethod(), result, request, response);

        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            log("Erreur dans " + mapping.getClassName() + "." + mapping.getMethodName(), cause);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur",
                    cause.getClass().getSimpleName() + " - " + cause.getMessage());

        } catch (IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Requête invalide", e.getMessage());

        } catch (IllegalAccessException e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur", e.getMessage());
        }
    }

    private void handleUnknown(String path, UrlNotSupportedException e,
                               HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String verb = request.getMethod();
        if ("GET".equals(verb) || "HEAD".equals(verb)) {

            if ("/controllers".equals(path)) {
                showControllers(response);
                return;
            }

            Mapping xml = findXmlMapping(path);
            if (xml != null) {
                showXmlMapping(xml, response);
                return;
            }

            if (looksLikeStaticFile(path)) {
                RequestDispatcher dispatcher = getServletContext().getNamedDispatcher("default");
                if (dispatcher != null) {
                    dispatcher.forward(request, response);
                    return;
                }
            }
        }

        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<h3>" + esc(e.getMessage()) + "</h3>");
        out.println("<p>Voici les routes que je connais :</p>");
        out.println("<ul>");
        for (Mapping m : urlMappings.values()) {
            out.println("<li>" + m.getHttpMethod() + " " + esc(m.getUrl()) + "</li>");
        }
        out.println("</ul>");
    }

    // ------------------------------------------------- pages "historiques" (sprint 0/1)

    private Mapping findXmlMapping(String path) {
        for (Mapping m : xmlMappings) {
            if (m.getUrl().equals(path)) {
                return m;
            }
        }
        return null;
    }

    private void showXmlMapping(Mapping mapping, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<h3>URL : " + esc(mapping.getUrl()) + "</h3>");
        try {
            Class<?> clazz = Class.forName(mapping.getClassName());
            out.println("<p>Classe : " + esc(clazz.getName()) + "</p>");
        } catch (ClassNotFoundException e) {
            out.println("Erreur : classe non trouvée -> " + esc(mapping.getClassName()));
        }
    }

    private void showControllers(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<h3>Contrôleurs détectés</h3>");
        out.println("<ul>");
        for (Class<?> c : controllers) {
            out.println("<li>" + esc(c.getName()) + "</li>");
        }
        out.println("</ul>");
    }

    // ------------------------------------------------------------------- utilitaires

    private static String pathOf(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        int semicolon = path.indexOf(';');
        if (semicolon >= 0) {
            path = path.substring(0, semicolon);
        }
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path.isEmpty() ? "/" : path;
    }

    private static HttpMethod parseVerb(String verb) {
        try {
            return HttpMethod.valueOf(verb.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static boolean looksLikeStaticFile(String path) {
        String last = path.substring(path.lastIndexOf('/') + 1);
        return last.indexOf('.') > 0;
    }

    private static String join(List<HttpMethod> verbs) {
        StringBuilder sb = new StringBuilder();
        for (HttpMethod v : verbs) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(v.name());
        }
        return sb.toString();
    }

    private static void sendError(HttpServletResponse response, int status, String title, String message)
            throws IOException {
        if (response.isCommitted()) {
            return;
        }
        response.resetBuffer();
        response.setStatus(status);
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<h1>" + esc(title) + "</h1>");
        out.println("<pre>" + esc(message) + "</pre>");
    }

    static String esc(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
