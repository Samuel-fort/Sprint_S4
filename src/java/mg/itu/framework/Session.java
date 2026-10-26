package mg.itu.framework;

import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * La session vue par le développeur : une simple Map clé -> objet.
 * Il suffit de déclarer un argument "Session session" dans la méthode d'action.
 * Les changements sont recopiés dans la vraie HttpSession après l'action
 * (les pages JSP peuvent donc les lire avec session.getAttribute).
 */
public class Session {

    private final String id;
    private final Map<String, Object> data = new ConcurrentHashMap<>();
    private volatile boolean invalidated;

    public Session(String id) {
        this.id = id;
    }

    public String getId() { return id; }

    public Object get(String key) {
        return key == null ? null : data.get(key);
    }

    /** Valeur typée, ou null si la clé est absente ou d'un autre type. */
    public <T> T get(String key, Class<T> type) {
        Object value = get(key);
        return type.isInstance(value) ? type.cast(value) : null;
    }

    /** Ajoute ou remplace une valeur. Une valeur null supprime la clé. */
    public Session set(String key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("Clé de session null");
        }
        if (value == null) {
            data.remove(key);
        } else {
            data.put(key, value);
        }
        return this;
    }

    public void remove(String key) {
        if (key != null) {
            data.remove(key);
        }
    }

    public boolean contains(String key) {
        return key != null && data.containsKey(key);
    }

    public void clear() {
        data.clear();
    }

    /** Tout ce que contient la session (la Map elle-même, pas une copie). */
    public Map<String, Object> getData() { return data; }

    /** Déconnexion : la session HTTP sera invalidée à la fin de l'action. */
    public void invalidate() {
        invalidated = true;
        data.clear();
    }

    boolean isInvalidated() { return invalidated; }

    /** Recharge la Map depuis les attributs de la session HTTP. */
    void loadFrom(HttpSession http) {
        Map<String, Object> current = new HashMap<>();
        Enumeration<String> names = http.getAttributeNames();
        for (String name : Collections.list(names)) {
            Object value = http.getAttribute(name);
            if (value != null) {
                current.put(name, value);
            }
        }
        data.keySet().retainAll(current.keySet());
        data.putAll(current);
    }

    /** Recopie la Map dans la session HTTP (ajouts, modifications et suppressions). */
    void saveTo(HttpSession http) {
        for (String name : Collections.list(http.getAttributeNames())) {
            if (!data.containsKey(name)) {
                http.removeAttribute(name);
            }
        }
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (http.getAttribute(entry.getKey()) != entry.getValue()) {
                http.setAttribute(entry.getKey(), entry.getValue());
            }
        }
    }
}
