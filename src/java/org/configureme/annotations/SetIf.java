package org.configureme.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method to be called with the name and value of each property whose
 * name matches configured condition. Default is 'matches' condition, which mean
 * property name should exactly match the regular expression compiled form
 * annotation's value parameter. The method must expect exactly two parameter of
 * type string:
 * 
 * <pre>
 * &#064;SetIf(condition = SetIfCondition.matches, value = &quot;name[\\d]+&quot;)
 * public void debug(String name, String value) {
 * 	log.debug(name + &quot;= &quot; + value);
 * }
 * </pre>
 * 
 * @author dzhmud
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SetIf {

	String value();

	SetIfCondition condition() default SetIfCondition.matches;

	/**
	 * Conditions to be checked by setif.
	 * @author dzhmud.
	 *
	 */
	public enum SetIfCondition {
		/**
		 * does the key start with given annotation value,
		 */
		startsWith, 
		/**
		 * does the key contain given annotation value,
		 */
		contains, 
		/**
		 * does the key match given annotation value,
		 */
		matches
	}

	public static final class ConditionChecker {
		private ConditionChecker() {
		}

		public static final boolean satisfyCondition(SetIf annotation, String attributeName) {
			switch (annotation.condition()) {
			case startsWith:
				return attributeName.startsWith(annotation.value());
			case contains:
				return attributeName.contains(annotation.value());
			case matches:
				return attributeName.matches(annotation.value());
			default:
				return false;
			}
		}
	}

}
