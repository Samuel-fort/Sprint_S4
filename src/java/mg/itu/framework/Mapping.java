package mg.itu.framework;

public class Mapping {

    private String url;
    private String className;
    private String methodName;

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

    @Override
    public String toString() {
        return "Mapping{url='" + url + "', className='" + className
                + "', methodName='" + methodName + "'}";
    }
}