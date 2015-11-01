package org.configureme.parser;

import org.configureme.Environment;
import org.configureme.repository.PlainValue;

/**
 * Represents parsed value of named plain attribute within a certain environment.
 * Plain attribute contains single value of a trivial type such as string, boolean and all kind of numbers in a string representation.
 */
public class PlainParsedAttribute extends ParsedAttribute<PlainValue> {
	/**
	 * Constructs new plain parsed attribute value with specified name, environment and value string.
	 * @param name name of the attribute
	 * @param environment environment which the value is defined within
	 * @param value value of the attribute within the environment
	 */
	public PlainParsedAttribute(String name, Environment environment, String value) {
		super(name, environment, new PlainValue(value));
	}
}
