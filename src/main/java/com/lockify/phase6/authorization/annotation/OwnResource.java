package com.lockify.phase6.authorization.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Resource endpoints pe lagao - caller sirf apna resource access kar sake.
 * resourceIdParam me path variable ka naam do (default "id").
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OwnResource {

    String resourceIdParam() default "id";

    String action() default "READ";
}
