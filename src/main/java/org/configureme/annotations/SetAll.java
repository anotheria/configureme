package org.configureme.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method to be set with the value of each property in the configuration snapshot.
 * The method must expect exactly two parameter of type string:
 * <pre>
 * &#064;SetAll
 * public void debug(String name, String value){
 * 		log.debug(name+"= "+value);
 * }
 * </pre>
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
@Retention (RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SetAll {

}
