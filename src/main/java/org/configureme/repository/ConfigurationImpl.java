package org.configureme.repository;

import org.configureme.Configuration;
import org.configureme.sources.ConfigurationSourceKey;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An implementation of the Configuration. This is a de-facto configuration snapshot of a configuration in a defined environment.
 *
 * @author lrosenberg
 */
public class ConfigurationImpl implements Configuration {
	/**
	 * The name of the configuration.
	 */
	private String name;
	/**
	 * The attributes .
	 */
	private Map<String, Value> attributes;

	/**
	 * External configurations that was included in current configuration
	 * this field use for reconfiguration of the current configuration
	 */
	private Set<ConfigurationSourceKey> externalConfigurations;

	/**
	 * Creates a new ConfigurationImpl.
	 *
	 * @param aName
	 */
	public ConfigurationImpl(String aName) {
		name = aName;
		attributes = new ConcurrentHashMap<>();
		externalConfigurations = new HashSet<>();
	}

	@Override
	public Set<ConfigurationSourceKey> getExternalConfigurations() {
		return externalConfigurations;
	}

	@Override
	public void clearExternalConfigurations() {
		externalConfigurations.clear();
	}

	@Override
	public void addExternalConfiguration(ConfigurationSourceKey configurationSourceKey){
		if(configurationSourceKey==null)
			return;
		externalConfigurations.add(configurationSourceKey);
	}

	@Override
	public Value getAttribute(String attributeName) {
		return attributes.get(attributeName);
	}

	@Override
	public Collection<String> getAttributeNames() {
		return attributes.keySet();
	}

	@Override
	public Set<Entry<String, Value>> getEntries() {
		return attributes.entrySet();
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of the attribute (in the selected environment).
	 *
	 * @param attributeName
	 * @param attributeValue
	 */
	public void setAttribute(String attributeName, Value attributeValue) {
		attributes.put(attributeName, attributeValue);
	}

	@Override
	public String toString() {
        return name + ": " + attributes;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof ConfigurationImpl &&
				name.equals(((ConfigurationImpl) o).name) &&
				attributes.equals(((ConfigurationImpl) o).attributes);
	}

	@Override
	public int hashCode() {
		return name == null ? 0 : name.hashCode();
	}

}
