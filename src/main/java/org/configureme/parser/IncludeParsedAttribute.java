package org.configureme.parser;

import org.configureme.Environment;
import org.configureme.repository.IncludeValue;
import org.configureme.util.StringUtils;

/**
 * Represents parsed value of include attribute within a certain environment.
 * Include attribute contains link attribute in the another config.
 *
 * @author ivanbatura
 * @version $Id: $Id
 */
public class IncludeParsedAttribute extends ParsedAttribute<IncludeValue> {
	/**
	 * Constructs new include parsed attribute value with specified name, environment and value.
	 *
	 * @param name        name of the attribute
	 * @param environment environment which the value is defined within
	 * @param value       list of child attribute values of the attribute within the environment
	 */
	public IncludeParsedAttribute(String name, Environment environment, String value) {
		super(name, environment, createIncludeValue(value));
	}

	/**
	 * Creates internal representation of the attribute value.
	 *
	 * @param value       name of the link attribute in the another config
	 * @return internal representation of the include attribute value
	 */
	private static IncludeValue createIncludeValue(final String value) {
		if (value.charAt(1) != '<')
			return new IncludeValue();
		//remove wrappers
		final String unwrapped = value.substring(2, value.length() - 1);
		return new IncludeValue(StringUtils.getStringBefore(unwrapped, "."), StringUtils.getStringAfter(unwrapped, "."));
	}
}
