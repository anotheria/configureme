package org.configureme.repository;

/**
 * The distinguishing interface for classes representing different types of attribute values such as plain, composite and array.
 *
 * @author another
 * @version $Id: $Id
 */
public interface Value {
	/**
	 * Gets raw representation of the value which is a String for all plain types, a Collection of Objects for arrays and a Map of Strings to Objects for composite types.
	 *
	 * @return raw representation of the value.
	 */
	Object getRaw();
}
