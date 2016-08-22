package org.configureme.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method to be set with the value of the property in brackets. The method must expect exactly one parameter of the desired java primary type (+string).
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
@Retention (RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Set {
	/**
	 * The value of the property.
	 */
	String value();
}
