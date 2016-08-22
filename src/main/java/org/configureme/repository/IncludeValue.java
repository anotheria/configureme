package org.configureme.repository;

import org.configureme.ConfigurationManager;
import org.configureme.Environment;
import org.configureme.sources.ConfigurationSourceKey;

/**
 * Value of a include attribute. Link to the another config with a configurable attribute name.
 *
 * @since: 26.09.12
 * @version $Id: $Id
 */
public class IncludeValue implements Value {

	/**
	 * Needed attribute name in the configuration.
	 */
	private final String attributeName;

	/**
	 * Name of the target configuration.
	 */
	private String configurationName;

	/** {@inheritDoc} */
	@Override
	public Object getRaw() {
		return new PlainValue(attributeName+"->"+configurationName);
	}

	/**
	 * Resolves included value dynamically from the linked config in the specified environment.
	 *
	 * @param in target environment.
	 * @return a {@link org.configureme.repository.Value} object.
	 */
	public Value getIncludedValue(Environment in){
		return ConfigurationManager.INSTANCE.getConfiguration(configurationName, in).getAttribute(attributeName);
	}

	/**
	 * Get configuration name of the linked config
	 *
	 * @return configuration name of the linked config
	 */
	public ConfigurationSourceKey getConfigName() {
		return new ConfigurationSourceKey(ConfigurationSourceKey.Type.FILE, ConfigurationSourceKey.Format.JSON, configurationName);
	}

	/**
	 * Default constructor.
	 */
	public IncludeValue() {
		attributeName = "";
	}

	/**
	 * Constructs new include attribute value.
	 *
	 * @param configurationName name config to load
	 * @param attributeName     name of the attribute to get from loaded config
	 */
	public IncludeValue(String configurationName, String attributeName) {
		this.attributeName = attributeName;
		this.configurationName = configurationName;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return attributeName+"->"+configurationName;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof IncludeValue)) return false;

		IncludeValue that = (IncludeValue) o;

		if (attributeName != null ? !attributeName.equals(that.attributeName) : that.attributeName != null)
			return false;
		return configurationName != null ? configurationName.equals(that.configurationName) : that.configurationName == null;

	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		int result = attributeName != null ? attributeName.hashCode() : 0;
		result = 31 * result + (configurationName != null ? configurationName.hashCode() : 0);
		return result;
	}
}
