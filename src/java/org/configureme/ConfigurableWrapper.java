package org.configureme;

import org.configureme.sources.ConfigurationSource;
import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.ConfigurationSourceListener;

/**
 * A wrapper object to store a configurable object in ram among the info about its configuration. It is used to keep reference to the object for later 
 * re-configuration. Its also registered at the appropriate ConfigurationSource as listener.
 * @author another
 *
 */
public class ConfigurableWrapper implements ConfigurationSourceListener{
	private Object configurable;
	private Environment environment;
	
	private ConfigurationSourceKey key;
	
	/**
	 * Creates a new wrapper object.
	 * @param aKey
	 * @param aConfigurable
	 * @param anEnvironment
	 */
	public ConfigurableWrapper(ConfigurationSourceKey aKey, Object aConfigurable, Environment anEnvironment){
		key = aKey;
		configurable = aConfigurable;
		environment = anEnvironment;
	}

	public Object getConfigurable() {
		return configurable;
	}

	public ConfigurationSourceKey getKey() {
		return key;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public String toString(){
		return getKey()+", "+getConfigurable();
	}

	/**
	 * This method is called by the ConfigurationSourceRegistry if watched source has been updated.
	 */
	@Override
	public void configurationSourceUpdated(ConfigurationSource source) {
		ConfigurationManager.INSTANCE.reconfigure(key, configurable, environment);
	}
	
	public boolean equals(Object anotherObject){
		if (!(anotherObject instanceof ConfigurableWrapper ))
			return false;
		ConfigurableWrapper w = (ConfigurableWrapper)anotherObject;
		return key.equals(w.getKey()) && configurable.equals(w.getConfigurable()) && environment.equals(w.getEnvironment());
	}
}
