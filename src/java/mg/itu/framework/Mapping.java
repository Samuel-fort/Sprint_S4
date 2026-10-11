package mg.itu.framework;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Une route : (URL + verbe HTTP) -> méthode d'un contrôleur.
 * equals/hashCode ne portent que sur URL + verbe : deux routes identiques
 * sont donc détectées comme doublons au démarrage.
 */
public class Mapping {

    private String url;
    private String className;
    private String methodName;
    private HttpMethod httpMethod = HttpMethod.GET;
    private Object instance;
    private Method method;

    public Mapping() {
    }

    /** Route de l'ancien système XML (sprint 0). */
    public Mapping(String url, String className) {
        this.url = url;
        this.className = className;
    }

    public Mapping(String url, String className, String methodName, HttpMethod httpMethod) {
        this.url = url;
        this.className = className;
        this.methodName = methodName;
        this.httpMethod = httpMethod;
    }

    /** Clé de recherche dans la Map des routes. */
    public static Mapping key(String url, HttpMethod httpMethod) {
        Mapping k = new Mapping();
        k.url = url;
        k.httpMethod = httpMethod;
        return k;
    }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getMethodName() { return methodName; }
    public void setMethodName(String methodName) { this.methodName = methodName; }

    public HttpMethod getHttpMethod() { return httpMethod; }
    public void setHttpMethod(HttpMethod httpMethod) { this.httpMethod = httpMethod; }

    public Object getInstance() { return instance; }
    public void setInstance(Object instance) { this.instance = instance; }

    public Method getMethod() { return method; }
    public void setMethod(Method method) { this.method = method; }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Mapping)) return false;
        Mapping other = (Mapping) object;
        return Objects.equals(url, other.url) && httpMethod == other.httpMethod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, httpMethod);
    }

    @Override
    public String toString() {
        return "Mapping{" + httpMethod + " " + url + " -> " + className + "." + methodName + "}";
    }
}
