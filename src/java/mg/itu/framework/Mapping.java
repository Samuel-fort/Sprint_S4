package mg.itu.framework;

public class Mapping {

    private String url;
    private String className;
    private String methodName;
    private HttpMethod httpMethod = HttpMethod.GET;

    public Mapping() {
    }

    public Mapping(String url, String className) {
        this.url = url;
        this.className = className;
    }

    public Mapping(String url, String className, String methodName) {
        this.url = url;
        this.className = className;
        this.methodName = methodName;
    }

    public Mapping(String url, String className, String methodName, HttpMethod httpMethod) {
        this.url = url;
        this.className = className;
        this.methodName = methodName;
        this.httpMethod = httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    @Override
    public String toString() {
        return "Mapping{url='" + url + "', httpMethod=" + httpMethod + ", className='" + className
                + "', methodName='" + methodName + "'}";
    }
}