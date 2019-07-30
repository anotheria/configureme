package org.configureme.parser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.configureme.parser.json.JsonParser;
import org.configureme.parser.properties.PropertiesParser;
import org.configureme.sources.ConfigurationSourceKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration parser manger store and provide all parse configurations.
 *
 * @author Ivan Batura
 */
public class ConfigurationParserManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationParserManager.class);

    /**
     * Lock object for singleton creation.
     */
    private static final Object LOCK = new Object();

    /**
     * {@link ConfigurationParserManager} instance,
     */
    private static ConfigurationParserManager instance;

    /**
     * A map which contains configuration parser for different formats.
     */
    private final Map<ConfigurationSourceKey.Format, ConfigurationParser> parsers = new ConcurrentHashMap<>();

    /**
     * Private constructor.
     */
    private ConfigurationParserManager() {
        parsers.put(ConfigurationSourceKey.Format.JSON, new JsonParser());
        parsers.put(ConfigurationSourceKey.Format.PROPERTIES, new PropertiesParser());
    }

    /**
     * Get singleton instance of {@link ConfigurationParserManager}.
     *
     * @return {@link ConfigurationSourceKey}
     */
    public static ConfigurationParserManager instance() {
        if (instance != null)
            return instance;
        synchronized (LOCK) {
            if (instance != null)
                return instance;

            instance = new ConfigurationParserManager();
            return instance;
        }
    }

    /**
     * Get parser configuration for provided {@code format}.
     *
     * @param format
     *         {@link ConfigurationSourceKey.Format}
     * @return {@link ConfigurationParser} for provided {@code format}
     * @throws IllegalArgumentException
     *         if the is no parser configuration
     */
    public ConfigurationParser get(final ConfigurationSourceKey.Format format){
        if (format == null)
            throw new IllegalArgumentException("Parameter format is NULL.");

        final ConfigurationParser result = parsers.get(format);
        if (result == null)
            throw new IllegalArgumentException("Format " + format + " is not supported (yet).");
        return result;
    }

    /**
     * Add new {@code configuration} to the manager, if format ofthe new configuration matched the other one in manger.
     * The older one will be replaced with new configuration.
     *
     * @param configuration {@link ConfigurationParser}
     *
     * @throws ConfigurationParserException if something wrong with configuration
     */
    public void put(final ConfigurationParser configuration) throws ConfigurationParserException {
        if (configuration == null)
            throw new ConfigurationParserException("Parameter configuration is NULL.");
        if (configuration.getFormat() == null)
            throw new ConfigurationParserException("Parameter configuration.getFormat() is NULL.");

        parsers.put(configuration.getFormat(), configuration);
    }

    /**
     *  Parse content.
     *
     * @param configSourceKey {@link ConfigurationSourceKey}
     * @param content content to parse
     * @return {@link ParsedConfiguration}
     */
    public ParsedConfiguration parse(final ConfigurationSourceKey configSourceKey, final String content){
        final String configurationName = configSourceKey.getName();
        final ConfigurationParser parser = get(configSourceKey.getFormat());
        try {
            return parser.parseConfiguration(configurationName, content);
        } catch (final ConfigurationParserException e) {
            LOGGER.error("parseConfiguration(" + configurationName + ", " + content + ')', e);
            throw new IllegalArgumentException(configSourceKey + " is not parseable: " + e.getMessage(), e);
        }

    }
}
