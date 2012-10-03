package org.configureme;

import org.configureme.repository.Value;
import org.configureme.sources.ConfigurationSourceKey;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This class represents a snapshot of a ConfigurationSource at once moment of time in one environment. It can be used for old style explicit configuration or debugging.
 *
 * @author lrosenberg
 */
public interface Configuration {
	/**
	 * The name of the configuration. Will be the same stated in getConfiguration() call to ConfigurationManager.
	 *
	 * @return the name of the configuration.
	 */
	String getName();

	/**
	 * Return the value of an attribute.
	 *
	 * @param attributeName the name of the attribute.
	 * @return value of the attribute.
	 */
	Value getAttribute(String attributeName);

	/**
	 * Returns the names of all contained attributes.
	 *
	 * @return the names of all contained attributes.
	 */
	Collection<String> getAttributeNames();

	/**
	 * Returns the set of entries of the underlying map. Used by @SetAll annotation.
	 *
	 * @return the set of entries of the underlying map
	 */
	Set<Entry<String, Value>> getEntries();

	/**
	 * Clear list of external configuration
	 */
	void clearExternalConfigurations();

	/**
	 * Add external configuration
	 *
	 * @param configurationSourceKey external configuration
	 */
	void addExternalConfiguration(ConfigurationSourceKey configurationSourceKey);

	/**
	 * Get set of external configurations
	 *
	 * @return set with external configuration
	 */
	Set<ConfigurationSourceKey> getExternalConfigurations();
}
