package org.configureme.repository;

import org.configureme.Configuration;
import org.configureme.ConfigurationManager;
import org.configureme.Environment;
import org.configureme.sources.ConfigurationSourceKey;

/**
 * Value of a include attribute. Link to the another config with a configurable attribute name
 *
 * @author ivanbatura
 * @since: 26.09.12
 */
public class IncludeValue implements Value {
	/**
	 * Configuration of the include attribute value.
	 */
	private Configuration config;

	/**
	 * Needed attribute name in the configuration
	 */
	private final String attributeName;

	public IncludeValue() {
		config = new ConfigurationImpl(null);
		attributeName = "";
	}

	@Override
	public Object getRaw() {
		return config.getAttribute(attributeName);
	}

	/**
	 * Get configuration name of the linked config
	 *
	 * @return configuration name of the linked config
	 */
	public ConfigurationSourceKey getConfigName() {
		return new ConfigurationSourceKey(ConfigurationSourceKey.Type.FILE, ConfigurationSourceKey.Format.JSON, config.getName());
	}

	/**
	 * Constructs new include attribute value.
	 *
	 * @param configurationName name config to load
	 * @param attributeName     namee of the attribute to get from loaded config
	 */
	public IncludeValue(Environment environment, String configurationName, String attributeName) {
		config = ConfigurationManager.INSTANCE.getConfiguration(configurationName, environment);
		this.attributeName = attributeName;
	}

	@Override
	public String toString() {
		return String.valueOf(config);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		IncludeValue that = (IncludeValue) o;

		if (attributeName != null ? !attributeName.equals(that.attributeName) : that.attributeName != null)
			return false;
		if (config != null ? !config.equals(that.config) : that.config != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = config != null ? config.hashCode() : 0;
		result = 31 * result + (attributeName != null ? attributeName.hashCode() : 0);
		return result;
	}
}
