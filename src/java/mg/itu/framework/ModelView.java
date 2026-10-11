package mg.itu.framework;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Équivalent simplifié du ModelAndView de Spring :
 * url = page à afficher (forward), model = données passées à la page.
 */
public class ModelView {

    private String url;
    private final Map<String, Object> model = new LinkedHashMap<>();

    public ModelView() {
    }

    public ModelView(String url) {
        this.url = url;
    }

    /** Équivalent de model.addAttribute(key, value) de Spring. */
    public ModelView addAttribute(String key, Object value) {
        model.put(key, value);
        return this;
    }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Map<String, Object> getModel() { return model; }
}
