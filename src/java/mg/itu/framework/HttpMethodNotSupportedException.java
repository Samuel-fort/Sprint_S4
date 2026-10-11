package mg.itu.framework;

import java.util.List;

/** L'URL existe mais pas avec ce verbe HTTP (-> 405). */
public class HttpMethodNotSupportedException extends Exception {

    private final List<HttpMethod> allowed;

    public HttpMethodNotSupportedException(String message, List<HttpMethod> allowed) {
        super(message);
        this.allowed = allowed;
    }

    public List<HttpMethod> getAllowed() {
        return allowed;
    }
}
