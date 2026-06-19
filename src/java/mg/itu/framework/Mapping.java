package mg.itu.framework;

public class Mapping {

    private String url;
    private String className;

    public Mapping() {
    }

    public Mapping(String url, String className) {
        this.url = url;
        this.className = className;
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

    @Override
    public String toString() {
        return "Mapping{url='" + url + "', className='" + className + "'}";
    }
}