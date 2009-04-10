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

	public void setConfigurable(Object configurable) {
		this.configurable = configurable;
	}

	public ConfigurationSourceKey getKey() {
		return key;
	}

	public void setKey(ConfigurationSourceKey key) {
		this.key = key;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public String toString(){
		return getKey()+", "+getConfigurable();
	}

	@Override
	public void configurationSourceUpdated(ConfigurationSource source) {
		ConfigurationManager.INSTANCE.reconfigure(key, configurable, environment);
	}
}
