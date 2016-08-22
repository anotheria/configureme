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
 * @version $Id: $Id
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SetIf {

	/**
	 * Value for the condition.
	 */
	String value();

	/**
	 * The condition type.
	 */
	SetIfCondition condition() default SetIfCondition.matches;

	/**
	 * Conditions to be checked by setif.
	 * @author dzhmud.
	 *
	 */
    enum SetIfCondition {
		/**
		 * Does the key start with given annotation value.
		 */
		startsWith, 
		/**
		 * Does the key contain given annotation value.
		 */
		contains, 
		/**
		 * Does the key match given annotation value.
		 */
		matches;
	}

	/**
	 * Utility for checking if condition is met.
	 * @author dzhmud.
	 *
	 */
	final class ConditionChecker {
		private ConditionChecker() {
		}

		/**
		 * Attribute name check for matching the condition.
		 * 
		 * @param annotation annotation
		 * @param attributeName attribute name to check
		 * @return true if attributeName matches condition, declared in the annotation, false - otherwise.
		 */
		public static boolean satisfyCondition(SetIf annotation, String attributeName) {
			switch (annotation.condition()) {
			case startsWith:
				return attributeName.startsWith(annotation.value());
			case contains:
				return attributeName.contains(annotation.value());
			case matches:
				return attributeName.matches(annotation.value());
			default:
				throw new AssertionError("Unsupported setif conndition "+annotation.condition());
			}
		}
	}

}
