package org.configureme.sources;

/**
 * Defines a source type dependent content loader.
 * @author lrosenberg
 */
public interface SourceLoader {
	/**
	 * Returns true if the is a configuration available for the 
	 * @param key the key of the configuration source which defines type, format and name
	 * @return
	 */
	boolean isAvailable(ConfigurationSourceKey key);
	
	/**
	 * Returns the last change timestamp for given key, for reconfiguration triggering.
	 * @param key
	 * @return
	 */
	long getLastChangeTimestamp(ConfigurationSourceKey key);
	
	/**
	 * Returns the content of this configuration source (i.e. content of the file).
	 * @param key
	 * @return
	 */
	String getContent(ConfigurationSourceKey key);
}
