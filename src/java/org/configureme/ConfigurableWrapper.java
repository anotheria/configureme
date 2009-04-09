package org.configureme;

import org.configureme.sources.ConfigurationSource;
import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.ConfigurationSourceListener;

import net.anotheria.util.NumberUtils;

public class ConfigurableWrapper implements ConfigurationSourceListener{
	private Object configurable;
	private Environment environment;
	
	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	private ConfigurationSourceKey key;
	
	public ConfigurableWrapper(ConfigurationSourceKey aKey, Object aConfigurable, Environment anEnvironment){
		key = aKey;
		configurable = aConfigurable;
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

	public String toString(){
		return getKey()+", "+getConfigurable();
	}

	@Override
	public void configurationSourceUpdated(ConfigurationSource source) {
		ConfigurationManager.INSTANCE.reconfigure(key, configurable, environment);
	}
}
