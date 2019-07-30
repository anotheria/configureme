package org.configureme;

import org.configureme.sources.ConfigurationSource;
import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.ConfigurationSourceListener;

/**
 * A wrapper object to store a configurable object in ram among the info about its configuration. It is used to keep reference to the object for later
 * re-configuration. Its also registered at the appropriate ConfigurationSource as listener.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public class ConfigurableWrapper implements ConfigurationSourceListener{
	/**
	 * The wrapped configurable object.
	 */
	private final Object configurable;
	/**
	 * The environment in which the object was configured (stored for reconfiguration).
	 */
	private final Environment environment;
	
	/**
	 * The key of objects configuration.
	 */
	private final ConfigurationSourceKey key;

	/**
	 * Creates a new wrapper object.
	 *
	 * @param aKey a {@link org.configureme.sources.ConfigurationSourceKey} object.
	 * @param aConfigurable a {@link java.lang.Object} object.
	 * @param anEnvironment a {@link org.configureme.Environment} object.
	 */
	public ConfigurableWrapper(ConfigurationSourceKey aKey, Object aConfigurable, Environment anEnvironment){
		key = aKey;
		configurable = aConfigurable;
		environment = anEnvironment;
	}

	/**
	 * Returns the configurable object.
	 *
	 * @return the configurable object
	 */
	public Object getConfigurable() {
		return configurable;
	}

	/**
	 * Returns the configuration key.
	 *
	 * @return the configuration key
	 */
	public ConfigurationSourceKey getKey() {
		return key;
	}

	/**
	 * Returns the environment.
	 *
	 * @return the environment
	 */
	public Environment getEnvironment() {
		return environment;
	}

	@Override
	public String toString(){
        return key +", "+ configurable;
	}

	/**
	 * {@inheritDoc}
	 *
	 * This method is called by the ConfigurationSourceRegistry if watched source has been updated.
	 */
	@Override
	public void configurationSourceUpdated(final ConfigurationSource source) {
		ConfigurationProcessor.instance().reconfigure(key, configurable, environment);
	}
	
	@Override public boolean equals(Object anotherObject){
		if (!(anotherObject instanceof ConfigurableWrapper ))
			return false;
		final ConfigurableWrapper w = (ConfigurableWrapper)anotherObject;
        return key.equals(w.key) && configurable.equals(w.configurable) && environment.equals(w.environment);
	}
	
	@Override
	public int hashCode(){
		return key == null ? 42 : key.hashCode();
	}
}
