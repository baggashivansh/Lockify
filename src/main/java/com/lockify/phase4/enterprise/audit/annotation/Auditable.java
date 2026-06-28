package com.lockify.phase4.enterprise.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods pe lagao jinka audit trail chahiye.
 * AuditAspect automatically log karega - passwords/tokens skip hote hain.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    String action();

    String resource() default "";
}
