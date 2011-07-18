package org.configureme.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a field configurable.
 */
@Retention (RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Configure {

}
