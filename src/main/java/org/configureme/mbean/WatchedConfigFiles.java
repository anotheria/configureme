package org.configureme.mbean;

import java.util.HashSet;
import java.util.Set;

import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.ConfigurationSourceRegistry;

/**
 * MBean object which contains set of all watches project configuration names.
 *
 * @author asamoilich
 * @version $Id: $Id
 */
public class WatchedConfigFiles implements WatchedConfigFilesMBean {
	/**
	 * Configurations names.
	 */
	private final Set<String> configNames = new HashSet<>();


	@Override
	public Set<String> getConfigNames() {
		final Set<ConfigurationSourceKey> allSources = ConfigurationSourceRegistry.INSTANCE.getAllSourceKeys();
		for (final ConfigurationSourceKey sourceKey : allSources)
			configNames.add(sourceKey.getName());
		return configNames;
	}
}
