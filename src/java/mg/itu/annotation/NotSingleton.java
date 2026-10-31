package mg.itu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sprint 11 : par défaut, un contrôleur est un singleton (une seule instance, créée au démarrage,
 * partagée par toutes les requêtes). Avec @NotSingleton, une NOUVELLE instance est créée à chaque
 * requête : ses attributs repartent donc à zéro. Ne jamais y garder de données partagées.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NotSingleton {
}
