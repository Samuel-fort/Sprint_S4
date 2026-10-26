package mg.itu.framework;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Lecture des fichiers d'une requête multipart/form-data.
 * Nécessite &lt;multipart-config&gt; sur le FrontServlet dans web.xml.
 * Un champ "file" laissé vide dans le formulaire donne null (pas de fichier).
 */
public final class FileUpload {

    private FileUpload() {
    }

    public static boolean isMultipart(HttpServletRequest request) {
        String type = request.getContentType();
        return type != null && type.toLowerCase().startsWith("multipart/");
    }

    /** Le fichier envoyé dans le champ {@code name}, ou null s'il n'y en a pas. */
    public static Mapfile read(HttpServletRequest request, String name) {
        if (!isMultipart(request)) {
            return null;
        }
        try {
            return toMapfile(request.getPart(name));
        } catch (IOException | ServletException | IllegalStateException e) {
            throw failure(name, e);
        }
    }

    /** Tous les fichiers de la requête, par nom de champ. */
    public static Map<String, Mapfile> readAll(HttpServletRequest request) {
        Map<String, Mapfile> files = new LinkedHashMap<>();
        if (!isMultipart(request)) {
            return files;
        }
        try {
            for (Part part : request.getParts()) {
                Mapfile file = toMapfile(part);
                if (file != null) {
                    files.put(part.getName(), file);
                }
            }
        } catch (IOException | ServletException | IllegalStateException e) {
            throw failure("*", e);
        }
        return files;
    }

    /** Vrai si un fichier a été envoyé dans un champ dont le nom commence par prefix. */
    public static boolean hasFileWithPrefix(HttpServletRequest request, String prefix) {
        if (!isMultipart(request)) {
            return false;
        }
        try {
            for (Part part : request.getParts()) {
                if (part.getName() != null && part.getName().startsWith(prefix) && hasFile(part)) {
                    return true;
                }
            }
        } catch (IOException | ServletException | IllegalStateException e) {
            throw failure(prefix, e);
        }
        return false;
    }

    private static boolean hasFile(Part part) {
        String submitted = part.getSubmittedFileName();
        return submitted != null && !submitted.trim().isEmpty();
    }

    private static Mapfile toMapfile(Part part) throws IOException {
        if (part == null || !hasFile(part)) {
            return null;
        }
        // certains navigateurs envoient le chemin complet : on ne garde que le nom
        String submitted = part.getSubmittedFileName();
        String fileName = submitted.substring(Math.max(submitted.lastIndexOf('/'), submitted.lastIndexOf('\\')) + 1);

        int dot = fileName.lastIndexOf('.');
        String nom = dot > 0 ? fileName.substring(0, dot) : fileName;
        String extension = dot > 0 ? fileName.substring(dot + 1) : "";

        byte[] contenu;
        try (InputStream in = part.getInputStream()) {
            contenu = in.readAllBytes();
        }
        return new Mapfile(nom, extension, contenu);
    }

    private static IllegalArgumentException failure(String name, Exception e) {
        return new IllegalArgumentException("Lecture du fichier '" + name + "' impossible : " + e.getMessage()
                + " (vérifiez <multipart-config> dans web.xml et la taille maximale autorisée)");
    }
}
