package org.configureme.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a field not configureable. Use it if you annotate the class as @ConfigureMe(allfields=true) but want to explicitely exclude some fields.
 *
 * @author another
 * @version $Id: $Id
 */
@Retention (RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DontConfigure {

}
