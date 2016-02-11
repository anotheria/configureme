package org.configureme.sources;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.configureme.ConfigurableWrapper;
import org.configureme.sources.ConfigurationSourceKey.Type;
import org.configureme.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ConfigurationSourceRegistry is the singleton object that controls and manages all known configuration sources. It also has an internal thread that checks the sources for update in defined time periods.
 * Currently the update interval is 10 seconds.
 *
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
	private static Logger log = LoggerFactory.getLogger(ConfigurationSourceRegistry.class);
	/**
	 * The map with watched sources.
	 */
	private ConcurrentMap<ConfigurationSourceKey, ConfigurationSource> watchedSources = new ConcurrentHashMap<>();
	/**
	 * A map with loaders for different source types.
	 */
	private Map<ConfigurationSourceKey.Type, SourceLoader> loaders = new ConcurrentHashMap<>();

	/**
	 * Creates a new registry and starts the watcher thread.
	 * This constructor also adds the FileLoader.
	 */
	private ConfigurationSourceRegistry() {
		initLoaders();
		new WatcherThread().start();
	}

	private void initLoaders() {
		loaders.clear();
		loaders.put(Type.FILE, new FileLoader());
	}

	/**
	 * Returns true if the key is translateable in an configuration source and the source exists.
	 *
	 * @return true if the key is translateable in an configuration source and the source exists
	 */
	public boolean isConfigurationAvailable(ConfigurationSourceKey key) {
		if (watchedSources.containsKey(key)) {
			return true;
		}
		SourceLoader loader = loaders.get(key.getType());
		if (loader == null)
			throw new IllegalArgumentException("Unsupported type: " + key.getType());
		return loader.isAvailable(key);
	}

	/**
	 * Returns the content of the configuration source defined by the key.
	 *
	 * @param key configuration source key
	 * @return the content of the configuration source defined by the key
	 */
	public String readConfigurationSource(ConfigurationSourceKey key) {
		SourceLoader loader = loaders.get(key.getType());
		if (loader == null)
			throw new IllegalArgumentException("Unsupported type: " + key.getType());
		return loader.getContent(key);
	}

	/**
	 * Return set of {@link ConfigurationSourceKey}.
	 *
	 * @return set of {@link ConfigurationSourceKey}
	 */
	public Set<ConfigurationSourceKey> getAllSourceKeys() {
		return watchedSources.keySet();
	}

	/**
	 * Adds a listener for the defined source.
	 *
	 * @param key      configuration source key
	 * @param listener listener to add
	 */
	public void addListener(ConfigurationSourceKey key, ConfigurationSourceListener listener) {
		ConfigurationSource source = watchedSources.get(key);
		if (source != null) {
			source.addListener(listener);
			return;
		}
		source = new ConfigurationSource(key);
		ConfigurationSource existentSource = watchedSources.putIfAbsent(key, source);
		if (existentSource != null) {
			existentSource.addListener(listener);
			return;
		}
		source.addListener(listener);
	}

	/**
	 * Removes a listener.
	 *
	 * @param key      configuration source key
	 * @param listener listener to remove
	 */
	public void removeListener(ConfigurationSourceKey key, ConfigurationSourceListener listener) {
		ConfigurationSource source = watchedSources.get(key);
		if (source == null) {
			return;
		}
		source.removeListener(listener);
	}

	/**
	 * Removes a watched configurable.
	 *
	 * @param wrapper
	 */
	public void removeWatchedConfigurable(ConfigurableWrapper wrapper) {
		ConfigurationSource source = watchedSources.get(wrapper.getKey());
		removeListener(wrapper.getKey(), wrapper);
		watchedSources.remove(wrapper.getKey(), source);
	}

	/**
	 * `
	 * Adds a watched configurable.
	 *
	 * @param wrapper
	 */
	public void addWatchedConfigurable(ConfigurableWrapper wrapper) {
		if (wrapper.getConfigurable() == null) {
			throw new AssertionError("configurable is null");
		}
		ConfigurationSourceKey key = wrapper.getKey();
		addListener(key, wrapper);
	}

	/**
	 * WatcherThread runs in background and checks whether a configuration source has been updated all X seconds. In case it did, it fires an update event on the source triggering a reconfiguration.
	 *
	 * @author lrosenberg.
	 */
	private final class WatcherThread extends Thread {
		private WatcherThread() {
			setDaemon(true);
		}

		@Override
		public void run() {
			try {
				while (!Thread.interrupted()) {
					Thread.sleep(1000L * 10);
					Collection<ConfigurationSource> allSources = watchedSources.values();
					for (ConfigurationSource source : allSources) {
						SourceLoader loader = loaders.get(source.getKey().getType());
//						System.out.println("source: "+source);

						try {
							long lastUpdate = loader.getLastChangeTimestamp(source.getKey());
							log.debug("Checking source: " + source + ", lastUpdateFromLoader= " + DateUtils.toISO8601String(lastUpdate) + ", storedLastUpdate=" + DateUtils.toISO8601String(source.getLastChangeTimestamp()));
							if (source.isOlderAs(lastUpdate)) {
								log.debug("firing update event: " + source);
//								System.out.println("firing update on source: "+source);
								source.fireUpdateEvent(lastUpdate);
							}
						} catch (IllegalArgumentException e) {
							log.warn("Apparently checking for non existing source, how did it came into the registry anyway?", e);
						}

					}
				}
			} catch (InterruptedException ignored) {
			}
		}
	}

	/**
	 * Adds a loader for a type.
	 *
	 * @param type   the type for the loader to handle.
	 * @param loader the loader for the given type.
	 */
	protected void addLoader(ConfigurationSourceKey.Type type, SourceLoader loader) {
		loaders.put(type, loader);
	}

	/* test  */ void reset() {
		watchedSources = new ConcurrentHashMap<ConfigurationSourceKey, ConfigurationSource>();
		initLoaders();
	}
}
