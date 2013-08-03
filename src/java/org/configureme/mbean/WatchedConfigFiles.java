package org.configureme.mbean;

import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.ConfigurationSourceRegistry;

import java.util.HashSet;
import java.util.Set;

/**
 * MBean object which contains set of all watches project configuration names.
 *
 * @author asamoilich
 */
public class WatchedConfigFiles implements WatchedConfigFilesMBean {
	/**
	 * Configurations names.
	 */
	private Set<String> configNames = new HashSet<String>();


	@Override
	public Set<String> getConfigNames() {
		Set<ConfigurationSourceKey> allSources = ConfigurationSourceRegistry.INSTANCE.getAllSourceKeys();
		for (ConfigurationSourceKey sourceKey : allSources)
			configNames.add(sourceKey.getName());
		return configNames;
	}
}
