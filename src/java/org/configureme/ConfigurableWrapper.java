package org.configureme;

import org.configureme.sources.ConfigurationSource;
import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.ConfigurationSourceListener;

public class ConfigurableWrapper implements ConfigurationSourceListener{
	private Object configurable;
	private Environment environment;
	
	private ConfigurationSourceKey key;
	
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
