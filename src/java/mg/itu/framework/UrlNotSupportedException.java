package mg.itu.framework;

/** Aucune route ne correspond à l'URL demandée (-> 404). */
public class UrlNotSupportedException extends Exception {

    public UrlNotSupportedException(String message) {
        super(message);
    }
}
