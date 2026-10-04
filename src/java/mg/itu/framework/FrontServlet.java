package mg.itu.framework;

import com.google.gson.Gson;
import mg.itu.annotation.RestAPI;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class FrontServlet extends HttpServlet {

    private List<Mapping> mappings = new ArrayList<>();
    private List<Class<?>> controllers = new ArrayList<>();
    private List<Mapping> urlMappings = new ArrayList<>();

    @Override
    @SuppressWarnings("unchecked")
    public void init() throws ServletException {
        try {
            InputStream xmlFile = getServletContext().getResourceAsStream("/WEB-INF/app.xml");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("controller-url");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);

                String url = element.getElementsByTagName("url")
                        .item(0).getTextContent().trim();
                String className = element.getElementsByTagName("className")
                        .item(0).getTextContent().trim();

                mappings.add(new Mapping(url, className));
            }

            controllers = (List<Class<?>>) getServletContext().getAttribute("controllers");
            if (controllers == null) {
                controllers = new ArrayList<>();
            }

            urlMappings = (List<Mapping>) getServletContext().getAttribute("urlMappings");
            if (urlMappings == null) {
                urlMappings = new ArrayList<>();
            }

        } catch (Exception e) {
            throw new ServletException("Erreur lors du chargement de app.xml", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private Mapping trouverMapping(String url) throws UrlNotSupportedException {
        for (Mapping mapping : urlMappings) {
            if (mapping.getUrl().equals(url)) {
                return mapping;
            }
        }
        throw new UrlNotSupportedException("URL not supported : " + url);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String url = request.getRequestURI().substring(request.getContextPath().length());

        if (url.equals("/controllers")) {
            out.println("<h3>Classes annotées @Controller trouvées :</h3>");
            out.println("<ul>");
            for (Class<?> controller : controllers) {
                out.println("<li>" + controller.getName() + "</li>");
            }
            out.println("</ul>");
            return;
        }

        if (url.startsWith("/mvc")) {
            String realUrl = url.substring("/mvc".length());

            try {
                Mapping mapping = trouverMapping(realUrl);

                try {
                    Class<?> cls = Class.forName(mapping.getClassName());
                    Object controllerInstance = cls.getDeclaredConstructor().newInstance();

                    Method action = null;
                    for (Method m : cls.getDeclaredMethods()) {
                        if (m.getName().equals(mapping.getMethodName())) {
                            action = m;
                            break;
                        }
                    }
                    if (action == null) {
                        throw new NoSuchMethodException(mapping.getMethodName());
                    }

                    Object result = action.invoke(controllerInstance);

                    if (action.isAnnotationPresent(RestAPI.class)) {
                        // Méthode annotée @RestAPI : pas de vue, on renvoie du JSON
                        response.setContentType("application/json;charset=UTF-8");
                        out.print(new Gson().toJson(result));
                    } else {
                        out.println("<h3>URL supportée : " + realUrl + "</h3>");
                        out.println("<p>Classe : " + cls.getName() + "</p>");
                        out.println("<p>Méthode : " + action.getName() + "</p>");
                        out.println("<p>Résultat : " + result + "</p>");
                    }

                } catch (InvocationTargetException e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.println("<h1>Erreur serveur</h1>");
                    out.println("<pre>Erreur d'invocation : " + cause.getClass().getSimpleName()
                            + " - " + cause.getMessage() + "</pre>");
                } catch (ReflectiveOperationException e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.println("<h1>Erreur serveur</h1>");
                    out.println("<pre>Erreur d'invocation : " + e.getClass().getSimpleName()
                            + " - " + e.getMessage() + "</pre>");
                }

            } catch (UrlNotSupportedException e) {
                out.println("<h3>" + e.getMessage() + "</h3>");
                out.println("<p>Voici les URL que je connais :</p>");
                out.println("<ul>");
                for (Mapping mapping : urlMappings) {
                    out.println("<li>" + mapping.getUrl() + "</li>");
                }
                out.println("</ul>");
            }
            return;
        }

        Mapping mappingTrouve = null;
        for (Mapping mapping : mappings) {
            if (mapping.getUrl().equals(url)) {
                mappingTrouve = mapping;
                break;
            }
        }

        if (mappingTrouve == null) {
            out.println("Aucune classe associée à l'URL : " + url);
            return;
        }

        try {
            Class<?> classe = Class.forName(mappingTrouve.getClassName());
            out.println("Classe associée à " + url + " : " + classe.getName());
        } catch (ClassNotFoundException e) {
            out.println("Erreur : classe non trouvée -> " + mappingTrouve.getClassName());
        }
    }
}