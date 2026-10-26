package mg.itu.framework;

/** Accès refusé : 401 (pas authentifié) ou 403 (authentifié mais pas autorisé). */
public class AccessDeniedException extends RuntimeException {

    private final int status;

    public AccessDeniedException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
