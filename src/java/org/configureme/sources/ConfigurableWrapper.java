package org.configureme.sources;

import net.anotheria.util.NumberUtils;

public class ConfigurableWrapper implements ConfigurationSourceListener{
	private Object configurable;
	
	private ConfigurationSourceKey key;
	
	public ConfigurableWrapper(ConfigurationSourceKey aKey, Object aConfigurable){
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
	public void configurationSourceUpdated() {
		System.out.println("ConfigurationSourceUpdate call on "+this);
	}
	
	
}
