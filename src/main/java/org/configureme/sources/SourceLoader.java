package org.configureme.sources;

/**
 * Defines a source type dependent content loader.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public interface SourceLoader {
	/**
	 * Returns true if the is a configuration available for the given source key.
	 *
	 * @param key the key of the configuration source which defines type, format and name
	 * @return true if the is a configuration available for the
	 */
	boolean isAvailable(ConfigurationSourceKey key);
	
	/**
	 * Returns the last change timestamp for given key, for reconfiguration triggering.
	 *
	 * @param key the key of the configuration source
	 * @return the last change timestamp for the given source key.
	 */
	long getLastChangeTimestamp(ConfigurationSourceKey key);
	
	/**
	 * Returns the content of this configuration source (i.e. content of the file).
	 *
	 * @param key the key of the configuration source
	 * @return the content of the given source key
	 */
	String getContent(ConfigurationSourceKey key);
}
