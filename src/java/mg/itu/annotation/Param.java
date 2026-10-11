package mg.itu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Nom du paramètre de formulaire lié à un argument de méthode.
 * Facultatif si l'application est compilée avec "javac -parameters"
 * (le nom de l'argument est alors utilisé).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {
    String value() default "";
}
