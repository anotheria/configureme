package org.configureme.sources;

/**
 * A configuration source listener will be notified on changes in the configuration.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public interface ConfigurationSourceListener {
	/**
	 * Called by the registry as soon as a source change is detected.
	 *
	 * @param target a {@link org.configureme.sources.ConfigurationSource} object.
	 */
	void configurationSourceUpdated(ConfigurationSource target);
}
