package org.configureme.sources;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.configureme.sources.ConfigurationSourceKey.Type;

public enum ConfigurationSourceRegistry {
	INSTANCE;
	
	private Map<ConfigurationSourceKey, Object> watchedConfigs = new ConcurrentHashMap<ConfigurationSourceKey, Object>();
	
	public boolean isConfigurationAvailable(ConfigurationSourceKey key){
		if (watchedConfigs.containsKey(key))
			return true;
		if (key.getType()==Type.FILE)
			return FileUtility.isFileAvailable(key);
		
		//check other config types
		return false;
	}
	
	public String readConfigurationSource(ConfigurationSourceKey key){
		if (key.getType()==Type.FILE)
			return FileUtility.getSourceContent(key);
		throw new IllegalArgumentException("Unsupported type: "+key.getType());
	}
	
	
}
