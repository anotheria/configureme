package org.configureme.parser;

import org.configureme.Environment;
import org.configureme.repository.ArrayValue;
import org.configureme.repository.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents parsed value of named array attribute within a certain environment.
 * Array attribute contains a sequence of child attributes with the same name and type. The sequence can have arbitrary length.
 *
 * @author another
 * @version $Id: $Id
 */
public class ArrayParsedAttribute extends ParsedAttribute<ArrayValue> {
	/**
	 * Constructs new array parsed attribute value with specified name, environment and value.
	 *
	 * @param name name of the attribute
	 * @param environment environment which the value is defined within
	 * @param value list of child attribute values of the attribute within the environment
	 */
	public ArrayParsedAttribute(String name, Environment environment, List<? extends ParsedAttribute<?>> value) {
		super(name, environment, createArrayValue(value));
	}

	/**
	 * Creates internal representation of the attribute value.
	 * @param value list of child attribute values of the attribute within the environment
	 * @return internal representation of the composite attribute value
	 */
	private static ArrayValue createArrayValue(final Collection<? extends ParsedAttribute<?>> value) {
		final List<Value> list = new ArrayList<>(value.size());
		for (final ParsedAttribute<?> parsed : value)
			list.add(parsed.getValue());

		return new ArrayValue(list);
	}
}
