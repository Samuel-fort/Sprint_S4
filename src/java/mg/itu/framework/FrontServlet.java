package mg.itu.framework;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
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

    private Mapping trouverMapping(String url, String httpMethod) throws UrlNotSupportedException {
        for (Mapping mapping : urlMappings) {
            if (mapping.getUrl().equals(url) && mapping.getHttpMethod().equals(httpMethod)) {
                return mapping;
            }
        }
        throw new UrlNotSupportedException("Aucun mapping pour l'URL " + url
                + " avec la méthode HTTP " + httpMethod);
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
            String httpMethod = request.getMethod();

            try {
                Mapping mapping = trouverMapping(realUrl, httpMethod);

                out.println("<h3>URL supportée : " + realUrl + "</h3>");
                out.println("<p>Méthode HTTP : " + httpMethod + "</p>");
                out.println("<p>Classe : " + mapping.getClassName() + "</p>");
                out.println("<p>Méthode : " + mapping.getMethodName() + "</p>");
                Class<?> controllerClass = mapping.getInstance().getClass();
                Object controller = mapping.getInstance(); // on réutilise l'instance déjà créée au démarrage
                Method controllerMethod = controllerClass.getDeclaredMethod(mapping.getMethodName());
                Object result = controllerMethod.invoke(controller);

                out.println("<p>Résultat : " + result + "</p>");

            } catch (UrlNotSupportedException e) {
                out.println("<h3>" + e.getMessage() + "</h3>");
                out.println("<p>Voici les mappings que je connais :</p>");
                out.println("<ul>");
                for (Mapping mapping : urlMappings) {
                    out.println("<li>" + mapping.getHttpMethod() + " " + mapping.getUrl() + "</li>");
                }
                out.println("</ul>");
            } catch (ReflectiveOperationException e) {
                out.println("<h3>Erreur lors de l'exécution du contrôleur</h3>");
                out.println("<p>" + e.getMessage() + "</p>");
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
