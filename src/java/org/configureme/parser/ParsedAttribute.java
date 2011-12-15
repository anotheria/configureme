package org.configureme.parser;

import org.configureme.Environment;
import org.configureme.repository.Value;

/**
 * The combination of the name and the value of an attribute in an environment. Usually an attribute diffenent values in different environments. The parses collects all values of an attribute and submits it
 * in a large list to the configuration repository, which in turn creates hierarchical structures of the attributes and their values.
 * @author another
 */
public abstract class ParsedAttribute<T extends Value> {
	/**
	 * Name of the attribute.
	 */
	private final String name;
	/**
	 * Value of the attribute in this environment.
	 */
	private final T value;
	/**
	 * Environment of the attribute.
	 */
	private final Environment environment;

	/**
	 * Constructs new parsed attribute with the specified name, environment and value.
	 * @param name name of the attribute
	 * @param environment environment which the value is defined within
	 * @param value value of the attribute within the environment
	 */
	protected ParsedAttribute(String name, Environment environment, T value) {
		this.name = name;
		this.environment = environment;
		this.value = value;
	}

	/**
	 * Returns the name of the attribute.
	 * @return the name of the attribute
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the value of the attribute.
	 * @return the value of the attribute
	 */
	public Value getValue() {
		return value;
	}

	/**
	 * Returns the environemnt of this attribute value.
	 * @return the environemnt of this attribute value
	 */
	public Environment getEnvironment() {
		return environment;
	}

	@Override
	public String toString(){
		return name+" = "+value+" in "+environment;
	}
}

