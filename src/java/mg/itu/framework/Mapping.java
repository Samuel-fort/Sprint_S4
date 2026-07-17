package mg.itu.framework;

import java.util.Objects;

public class Mapping {

    private String url;
    private String httpMethod;
    private String className;
    private String methodName;
    private Object instance;

    public Mapping(String url, String className) {
        this.url = url;
        this.className = className;
    }

    public Mapping(String url, String className, String methodName, String httpMethod) {
        this.url = url;
        this.className = className;
        this.methodName = methodName;
        this.httpMethod = httpMethod;
    }

    public String getUrl() { return url; }
    public String getHttpMethod() { return httpMethod; }
    public String getClassName() { return className; }
    public String getMethodName() { return methodName; }
    public Object getInstance() { return instance; }

    public void setInstance(Object instance) { this.instance = instance; }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Mapping)) return false;
        Mapping other = (Mapping) object;
        return Objects.equals(url, other.url)
                && Objects.equals(httpMethod, other.httpMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, httpMethod);
    }

    @Override
    public String toString() {
        return "Mapping{url='" + url + "', httpMethod='" + httpMethod
                + "', className='" + className + "', methodName='" + methodName + "'}";
    }
}
