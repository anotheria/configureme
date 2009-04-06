package org.configureme.sources;

public class ConfigurationWatcher {
	private Object configurable;
	
	private ConfigurationSourceKey key;
	
	private long configurationTimeStamp;

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

	public long getConfigurationTimeStamp() {
		return configurationTimeStamp;
	}

	public void setConfigurationTimeStamp(long configurationTimeStamp) {
		this.configurationTimeStamp = configurationTimeStamp;
	}
}
