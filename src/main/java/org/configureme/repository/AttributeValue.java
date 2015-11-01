package org.configureme.repository;

import org.configureme.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The internal representation of the attribute value(s).
 * @author lrosenberg
 */
public class AttributeValue {

	/**
	 * The logger.
	 */
	private static Logger log = LoggerFactory.getLogger(AttributeValue.class);
	/**
	 * Internal map of the values. The keys are the extendedStringFrom of the environment in which the attribute applies.
	 */
	private Map<String, Value> values;

	/**
	 * Creates a new attribute values container.
	 */
	public AttributeValue(){
		values = new ConcurrentHashMap<String, Value>();
	}

	/**
	 * Returns the value in the given environment. If no value is found for a specific environment the code will fallback to a less complex environment (reduced form of the environment)
	 * unless a value is found or the environment is not further reduceable.
	 * @param in environment
	 * @return the value in the given environment
	 */
	public Value get(Environment in){
		if (log.isDebugEnabled())
			log.debug("looking up in "+in +"("+in.expandedStringForm()+")");
		Value retValue = values.get(in.expandedStringForm());
		if (retValue!=null)
			return retValue;
		if (!in.isReduceable())
			return null;
		return get(in.reduce());
	}

	/**
	 * Sets the value in a given environment.
	 * @param value the value to set
	 * @param in the environment in which the value applies
	 */
	public void set(Value value, Environment in){
		values.put(in.expandedStringForm(), value);
	}

	@Override public String toString(){
		return values.toString();
	}
}
