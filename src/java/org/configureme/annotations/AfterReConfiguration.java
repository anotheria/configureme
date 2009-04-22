package org.configureme.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Called immediately after re-configuration is finished.
 */
@Retention (RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterReConfiguration {

}
