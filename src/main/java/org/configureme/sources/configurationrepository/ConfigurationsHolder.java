package org.configureme.sources.configurationrepository;



import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Roman Stetsiuk on 5/5/16.
 *
 * @author another
 * @version $Id: $Id
 */
public enum ConfigurationsHolder {

    /**
     * The configurations holder is a singleton.
     */
    INSTANCE;

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ConfigurationsHolder.class);

    /**
     * key - configuration name
     * value - configuration context
     */
    private final Map<String, Configuration> configurations = new HashMap<>();

    /**
     * Returns configuration content by configuration name.
     *
     * @param name the configuration name.
     * @return configuration content as string.
     * @throws IllegalArgumentException if no configuration exists for the given name.
     */
    public String getConfigurationByName(String name) {
        Configuration configuration = configurations.get(name);
        if (configuration == null) {
            throw new IllegalArgumentException("No configuration found for name: " + name);
        }
        return configuration.getContent();
    }

    /**
     * Returns configuration content by configuration name.
     *
     * @param name the configuration name.
     * @return configuration content as string.
     * @throws IllegalArgumentException if no configuration exists for the given name.
     * @deprecated Use {@link #getConfigurationByName(String)} instead.
     */
    @Deprecated
    public String getConfigurationByname(String name) {
        return getConfigurationByName(name);
    }

    /**
     * <p>putConfigurationWithName.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param context a {@link java.lang.Object} object.
     */
    public void putConfigurationWithName(String name, Object context) {
        configurations.put(name, new Configuration(System.currentTimeMillis(), mapObjectToString(context)));
    }

    /**
     * <p>deleteConfigurationWithName.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String deleteConfigurationWithName(final String name) {
        Configuration removedConfiguration = configurations.remove(name);
        return removedConfiguration != null ? removedConfiguration.getContent() : "";
    }

    private String mapObjectToString(final Object toMap) {
        try {
            return new Gson().toJson(toMap);
        } catch (final RuntimeException e) {
            log.error("Json parsing exception: ", e);
            return null;
        }
    }

    /**
     * <p>isConfigurationWithNameExist.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean isConfigurationWithNameExist(String name) {
        return configurations.containsKey(name);
    }

    /**
     * <p>getConfigurationTimestamp.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a long.
     */
    public long getConfigurationTimestamp(String name) {
        Configuration configuration = configurations.get(name);
        if (configuration == null) {
            throw new IllegalArgumentException("No configuration found for name: " + name);
        }
        return configuration.getTimestamp();
    }

    private class Configuration {
        private final long timestamp;
        private final String content;

        public Configuration(long timestamp, String content) {
            this.timestamp = timestamp;
            this.content = content;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getContent() {
            return content;
        }
    }

}
