package org.configureme.sources;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.anotheria.util.NumberUtils;

import org.configureme.sources.ConfigurationSourceKey.Type;

public enum ConfigurationSourceRegistry {
	INSTANCE;
	
	private Map<ConfigurationSourceKey, ConfigurationSource> watchedSources = new ConcurrentHashMap<ConfigurationSourceKey, ConfigurationSource>();
	
	private ConfigurationSourceRegistry(){
		new WatcherThread().start();
	}
	
	public boolean isConfigurationAvailable(ConfigurationSourceKey key){
		if (watchedSources.containsKey(key))
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
	
	public void addWatchedConfigurable(ConfigurationSourceKey key, Object configurable){
		ConfigurationSource source = (ConfigurationSource) watchedSources.get(key);
		if (source==null){
			synchronized(watchedSources){
				source = (ConfigurationSource) watchedSources.get(key);
				if (source==null){
					source = new ConfigurationSource(key);
					watchedSources.put(key, source);
				}
			}
		}
		
		// --->
		ConfigurableWrapper wrapper = new ConfigurableWrapper(key, configurable);
		source.addListener(wrapper);
		
	}
	
	private class WatcherThread extends Thread{
		private WatcherThread(){
			setDaemon(true);
		}
		
		public void run(){
			try{
				while(!Thread.interrupted()){
					Thread.sleep(1000L*10);
					Collection<ConfigurationSource> allSources = watchedSources.values();
					for (ConfigurationSource source : allSources){
						if (source.getKey().getType()==Type.FILE){
							long lastUpdate = FileUtility.getSourceLastChangeTimestamp(source.getKey());
							System.out.println("Checking sourcE: "+source+", lastUpdateonFs= "+NumberUtils.makeISO8601TimestampString(lastUpdate));
							if (source.isOlderAs(lastUpdate)){
								System.out.println("Firing on source: "+source);
								source.fireUpdateEvent(lastUpdate);
							}
						}
					}
				}
			}catch(InterruptedException e){}
		}
	}
 	
}
