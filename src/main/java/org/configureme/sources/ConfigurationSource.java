package org.configureme.sources;

import java.util.ArrayList;
import java.util.List;

import org.configureme.repository.ConfigurationRepository;
import org.configureme.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a loaded configuration source for example a file. Doesn't contain the content of the file, only metadata is included. The ConfigurationSource object is a surogate which is used to execute functions
 * which are semantically ment to be executed on the configuration source itself, like registering for a change event etc.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public class ConfigurationSource {
	/**
	 * The key for the underlying source.
	 */
	private final ConfigurationSourceKey key;
	/**
	 * A list of listeners.
	 */
	private final List<ConfigurationSourceListener> listeners;
	/**
	 * Last detected change timestamp.
	 */
	private long lastChangeTimestamp;

	/**
	 * Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(ConfigurationSource.class);

	/**
	 * Creates a new configuration source.
	 *
	 * @param aKey a configuration source key the as unique identification of a configuration source
	 */
	public ConfigurationSource(ConfigurationSourceKey aKey){
		key = aKey;
		listeners = new ArrayList<>();
		lastChangeTimestamp = System.currentTimeMillis();
		listeners.add(ConfigurationRepository.INSTANCE);
	}

	/**
	 * Adds a listener to this source.
	 *
	 * @param listener a listener to add
	 */
	public void addListener(final ConfigurationSourceListener listener){
		synchronized(listeners){
			listeners.add(listener);
		}
	}
	/**
	 * Removes the listener from this source.
	 *
	 * @param listener a listener to remove
	 */
	public void removeListener(final ConfigurationSourceListener listener){
		synchronized(listeners){
			listeners.remove(listener);
		}
	}

	@Override public String toString(){
        return "ConfigurationSource "+key+", listeners: "+listeners.size()+", "+ DateUtils.toISO8601String(lastChangeTimestamp);
	}

	/**
	 * Return the last change timestamp of this source in millis.
	 *
	 * @return the last change timestamp of this source in millis
	 */
	public long getLastChangeTimestamp() {
		return lastChangeTimestamp;

	}

	/**
	 * Returns the config key of this source.
	 *
	 * @return the configuration key of this source
	 */
	public ConfigurationSourceKey getKey(){
		return key;
	}

	/**
	 * Returns true if this source's change timestamp is older as the given timestamp.
	 *
	 * @param sourceChangeTimestamp timestamp
	 * @return true if this source's change timestamp is older as the given timestamp
	 */
	public boolean isOlderAs(long sourceChangeTimestamp){
		return lastChangeTimestamp < sourceChangeTimestamp;
	}

	/**
	 * Called by the ConfigurationSourceRegistry if a change in the underlying source is detected.
	 *
	 * @param timestamp a long.
	 */
	public void fireUpdateEvent(final long timestamp){
		synchronized(listeners){
			for (final ConfigurationSourceListener listener : listeners){
				try{
					log.debug("Calling configurationSourceUpdated on "+listener);
					listener.configurationSourceUpdated(this);
				}catch(final Exception e){
					log.error("Error in notifying configuration source listener:"+listener, e);
				}
			}
		}
		lastChangeTimestamp = timestamp;
	}
}
