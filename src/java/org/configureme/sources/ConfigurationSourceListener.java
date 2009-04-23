package org.configureme.sources;

/**
 * A configuration source listener will be notified on changes in the configuration. 
 * @author lrosenberg
 */
public interface ConfigurationSourceListener {
	/**
	 * Called by the registry as soon as a source change is detected.
	 * @param target
	 */
	void configurationSourceUpdated(ConfigurationSource target);
}
