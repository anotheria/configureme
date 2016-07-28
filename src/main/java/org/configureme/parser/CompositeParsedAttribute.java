package org.configureme.parser;

import org.configureme.Environment;
import org.configureme.repository.CompositeValue;
import org.configureme.repository.Value;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents parsed value of named composite attribute within a certain environment.
 * Composite attribute contains arbitrary set of child attributes distinguished by their names. Nested attributes have have arbitrary type.
 */
public class CompositeParsedAttribute extends ParsedAttribute<CompositeValue> {
	/**
	 * Constructs new composite parsed attribute value with specified name, environment and value.
	 * @param name name of the attribute
	 * @param environment environment which the value is defined within
	 * @param value list of child attribute values of the attribute within the environment
	 */
	public CompositeParsedAttribute(String name, Environment environment, List<? extends ParsedAttribute<?>> value) {
		super(name, environment, createCompositeValue(name, value));
	}

	/**
	 * Creates internal representation of the attribute value.
	 * @param name name of the attribute
	 * @param value list of child attribute values of the attribute within the environment
	 * @return internal representation of the composite attribute value
	 */
	private static CompositeValue createCompositeValue(String name, Collection<? extends ParsedAttribute<?>> value) {
    	Map<String, Value> map = new HashMap<>(value.size());
    	for (ParsedAttribute<?> attr : value) {
    		map.put(attr.getName(), attr.getValue());
    	}
		return new CompositeValue(name, map);
	}
}
