package org.configureme.sources;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.configureme.repository.ConfigurationRepository;

import net.anotheria.util.NumberUtils;

public class ConfigurationSource {
	
	private ConfigurationSourceKey key;
	private List<ConfigurationSourceListener> listeners;
	private long lastChangeTimestamp;
	
	private static Logger log = Logger.getLogger(ConfigurationSource.class);
	
	public ConfigurationSource(ConfigurationSourceKey aKey){
		key = aKey;
		listeners = new ArrayList<ConfigurationSourceListener>(); 
		lastChangeTimestamp = System.currentTimeMillis();
		listeners.add(ConfigurationRepository.INSTANCE);
	}
	
	public void addListener(ConfigurationSourceListener listener){
		synchronized(listeners){
			listeners.add(listener);
		}
	}
	
	public void removeListener(ConfigurationSourceListener listener){
		synchronized(listeners){
			listeners.remove(listener);
		}
	}
	
	public String toString(){
		return "ConfigurationSource "+key+", listeners: "+listeners.size()+", "+NumberUtils.makeISO8601TimestampString(getLastChangeTimestamp());
	}

	public long getLastChangeTimestamp() {
		return lastChangeTimestamp;
		
	}

	public ConfigurationSourceKey getKey(){
		return key;
	}
	
	public boolean isOlderAs(long sourceChangeTimestamp){
		return lastChangeTimestamp < sourceChangeTimestamp;
	}
	
	public void fireUpdateEvent(long timestamp){
		synchronized(listeners){
			for (ConfigurationSourceListener listener : listeners){
				try{
					log.debug("Calling configurationSourceUpdated on "+listener);
					listener.configurationSourceUpdated(this);
				}catch(Exception e){
					log.error("Error in notifying configuration source listener:"+listener, e);
				}
			}
		}
		lastChangeTimestamp = timestamp;
	}
}
