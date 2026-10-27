package mg.itu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * L'utilisateur doit être authentifié pour exécuter la méthode
 * (ou toutes les méthodes de la classe si l'annotation est sur la classe).
 * Est authentifié celui dont la session contient l'attribut "user"
 * (nom modifiable : context-param "auth-user-key" dans web.xml).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Authenticated {
}
