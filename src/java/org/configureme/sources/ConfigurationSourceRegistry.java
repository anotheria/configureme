package org.configureme.sources;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.anotheria.util.NumberUtils;

import org.apache.log4j.Logger;
import org.configureme.ConfigurableWrapper;
import org.configureme.sources.ConfigurationSourceKey.Type;

/**
 * ConfigurationSourceRegistry is the singleton object that controls and manages all known configuration sources. It also has an internal thread that checks the sources for update in defined time periods.
 * Currently the update interval is 10 seconds.
 * @author lrosenberg
 */
public enum ConfigurationSourceRegistry {
	/**
	 * The one and only instance of the ConfigurationSourceRegistry.
	 */
	INSTANCE;
	/**
	 * Logger.
	 */
	private static Logger log = Logger.getLogger(ConfigurationSourceRegistry.class);
	/**
	 * The map with watched sources.
	 */
	private Map<ConfigurationSourceKey, ConfigurationSource> watchedSources = new ConcurrentHashMap<ConfigurationSourceKey, ConfigurationSource>();
	/**
	 * A map with loaders for different source types.
	 */
	private Map<ConfigurationSourceKey.Type, SourceLoader> loaders = new ConcurrentHashMap<Type, SourceLoader>();
	
	/**
	 * Creates a new registry and starts the watcher thread.
	 */
	private ConfigurationSourceRegistry(){
		loaders.put(Type.FILE, new FileLoader());
		new WatcherThread().start();
	}
	
	/**
	 * Returns true if the key is translateable in an configuration source and the source exists. 
	 */
	public boolean isConfigurationAvailable(ConfigurationSourceKey key){
		if (watchedSources.containsKey(key))
			return true;
		SourceLoader loader = loaders.get(key.getType());
		if (loader==null)
			throw new IllegalArgumentException("Unsupported type: "+key.getType());
		return loader.isAvailable(key);
	}
	
	/**
	 * Returns the content of the configation source defined by the key.
	 * @param key
	 * @return
	 */
	public String readConfigurationSource(ConfigurationSourceKey key){
		SourceLoader loader = loaders.get(key.getType());
		if (loader==null)
			throw new IllegalArgumentException("Unsupported type: "+key.getType());
		return loader.getContent(key);
	}
	
	/**
	 * Adds a listener for the defined source
	 * @param key
	 * @param listener
	 */
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
	
	/**
	 * Removes a listener
	 * @param key
	 * @param listener
	 */
	public void removeListener(ConfigurationSourceKey key, ConfigurationSourceListener listener){
		ConfigurationSource source = (ConfigurationSource) watchedSources.get(key);
		if (source==null){
			return;
		}
		
		source.removeListener(listener);
	}

	/**
	 * Removes a watched configurable.
	 * @param wrapper
	 */
	public void removeWatchedConfigurable(ConfigurableWrapper wrapper){
		removeListener(wrapper.getKey(), wrapper);
	}

	/**
	 * Adds a watched configurable
	 * @param wrapper
	 */
	public void addWatchedConfigurable(ConfigurableWrapper wrapper){
		if (wrapper.getConfigurable()==null){
			throw new AssertionError("configurable is null");
		}
		ConfigurationSourceKey key = wrapper.getKey();
		addListener(key, wrapper);
	}
	
	private final class WatcherThread extends Thread{
		private WatcherThread(){
			setDaemon(true);
		}
		
		@Override public void run(){
			try{
				while(!Thread.interrupted()){
					Thread.sleep(1000L*10);
					Collection<ConfigurationSource> allSources = watchedSources.values();
					for (ConfigurationSource source : allSources){
						SourceLoader loader = loaders.get(source.getKey().getType());
						//System.out.println("source: "+source);
						
						try{
							long lastUpdate = loader.getLastChangeTimestamp(source.getKey());
							log.debug("Checking source: "+source+", lastUpdateFromLoader= "+NumberUtils.makeISO8601TimestampString(lastUpdate)+", storedLastUpdate="+NumberUtils.makeISO8601TimestampString(source.getLastChangeTimestamp()));
							if (source.isOlderAs(lastUpdate)){
								log.debug("firing update event: "+ source);
								//System.out.println("firing update on source: "+source);
								source.fireUpdateEvent(lastUpdate);
							}
						}catch(IllegalArgumentException e){
							log.warn("Apparently checking for non existing source, how did it came into the registry anyway?", e);
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
