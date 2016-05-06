package org.configureme.sources.configurationrepository;

import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.SourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A source loader singleton configuration holder
 */
public class ConfigurationHolderSourceLoader implements SourceLoader {
    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ConfigurationHolderSourceLoader.class);


    @Override
    public boolean isAvailable(ConfigurationSourceKey key) {
        if (key == null) {
            throw new IllegalArgumentException("isAvailable(): ConfigurationSourceKey is null");
        }
        return ConfigurationsHolder.INSTANCE.isConfigurationWithNameExist(key.getName());
    }

    @Override
    public long getLastChangeTimestamp(ConfigurationSourceKey key) {
        if (key == null) {
            throw new IllegalArgumentException("getLastChangeTimestamp(): ConfigurationSourceKey is null");
        }
        return ConfigurationsHolder.INSTANCE.getConfigurationTimestamp(key.getName());
    }

    @Override
    public String getContent(ConfigurationSourceKey key) {
        if (key.getType() != ConfigurationSourceKey.Type.REPOSITORY) {
            throw new IllegalStateException("Can only get configuration for type: " + ConfigurationSourceKey.Type.REPOSITORY);
        }
        return ConfigurationsHolder.INSTANCE.getConfigurationByname(key.getName());
    }
}
