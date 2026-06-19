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
import java.util.ArrayList;
import java.util.List;


public class FrontServlet extends HttpServlet {

    private List<Mapping> mappings = new ArrayList<>();

    @Override
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

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String url = request.getRequestURI().substring(request.getContextPath().length());

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