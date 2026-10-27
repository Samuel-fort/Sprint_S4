package mg.itu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * L'utilisateur doit être authentifié pour exécuter la méthode
 * (ou toutes les méthodes de la classe si l'annotation est sur la classe).
 *
 *   @Authenticated                      -> tout utilisateur connecté
 *   @Authenticated(roles = {"ADMIN"})   -> seulement les utilisateurs ayant le rôle ADMIN
 *
 * Est authentifié celui dont la session contient l'attribut "user" ; ses rôles sont lus
 * dans l'attribut "role" (noms modifiables : context-param "auth-user-key" / "auth-role-key").
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Authenticated {
    String[] roles() default {};
}
