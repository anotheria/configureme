package org.configureme.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Called immediately after configuration is finished.
 *
 * @author another
 * @version $Id: $Id
 */
@Retention (RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterConfiguration {

}
