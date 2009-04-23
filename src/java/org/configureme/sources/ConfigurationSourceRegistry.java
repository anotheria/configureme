package org.configureme.sources;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.anotheria.util.NumberUtils;

import org.apache.log4j.Logger;
import org.configureme.ConfigurableWrapper;
import org.configureme.sources.ConfigurationSourceKey.Type;

/**
 * 
 * @author lrosenberg
 */
public enum ConfigurationSourceRegistry {
	INSTANCE;
	
	private static Logger log = Logger.getLogger(ConfigurationSourceRegistry.class);
	
	private Map<ConfigurationSourceKey, ConfigurationSource> watchedSources = new ConcurrentHashMap<ConfigurationSourceKey, ConfigurationSource>();
	private Map<ConfigurationSourceKey.Type, SourceLoader> loaders = new ConcurrentHashMap<Type, SourceLoader>();
	
	private ConfigurationSourceRegistry(){
		loaders.put(Type.FILE, new FileLoader());
		new WatcherThread().start();
	}
	
	public boolean isConfigurationAvailable(ConfigurationSourceKey key){
		if (watchedSources.containsKey(key))
			return true;
		SourceLoader loader = loaders.get(key.getType());
		if (loader==null)
			throw new IllegalArgumentException("Unsupported type: "+key.getType());
		return loader.isAvailable(key);
	}
	
	public String readConfigurationSource(ConfigurationSourceKey key){
		SourceLoader loader = loaders.get(key.getType());
		if (loader==null)
			throw new IllegalArgumentException("Unsupported type: "+key.getType());
		return loader.getContent(key);
	}
	
	public void addListener(ConfigurationSourceKey key, ConfigurationSourceListener listener){
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
		source.addListener(listener);
	}
	
	public void removeListener(ConfigurationSourceKey key, ConfigurationSourceListener listener){
		ConfigurationSource source = (ConfigurationSource) watchedSources.get(key);
		if (source==null){
			return;
		}
		
		source.removeListener(listener);
	}

	public void removeWatchedConfigurable(ConfigurableWrapper wrapper){
		removeListener(wrapper.getKey(), wrapper);
	}

	public void addWatchedConfigurable(ConfigurableWrapper wrapper){
		if (wrapper.getConfigurable()==null){
			throw new AssertionError("configurable is null");
		}
		ConfigurationSourceKey key = wrapper.getKey();
		addListener(key, wrapper);
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
						SourceLoader loader = loaders.get(source.getKey().getType());
						//System.out.println("source: "+source);
						
						long lastUpdate = loader.getLastChangeTimestamp(source.getKey());
						log.debug("Checking source: "+source+", lastUpdateonFs= "+NumberUtils.makeISO8601TimestampString(lastUpdate));
						if (source.isOlderAs(lastUpdate)){
							log.debug("firing update event: "+ source);
							//System.out.println("firing update on source: "+source);
							source.fireUpdateEvent(lastUpdate);
						}
						
					}
				}
			}catch(InterruptedException e){}
		}
	}
	
	protected void addLoader(ConfigurationSourceKey.Type type, SourceLoader loader){
		loaders.put(type, loader);
	}
 	
}
