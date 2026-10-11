package mg.itu.annotation;

import mg.itu.framework.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface URLMapping {
    String value();

    HttpMethod method() default HttpMethod.GET;
}
