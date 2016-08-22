package org.configureme.sources.configurationrepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
     * The configurationsholder is a singleton.
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
    private Map<String, Configuration> configurations = new HashMap<>();

    /**
     * <p>getConfigurationByname.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String getConfigurationByname(String name) {
        return configurations.get(name).getContent();
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
    public String deleteConfigurationWithName(String name) {
        Configuration removedConfiguration = configurations.remove(name);
        return removedConfiguration != null ? removedConfiguration.getContent() : "";
    }

    private String mapObjectToString(Object toMap) {
        ObjectMapper mapper = new ObjectMapper();
        String resultString = null;
        try {
            resultString = mapper.writeValueAsString(toMap);
        } catch (JsonProcessingException e) {
            log.error("Json parsing exception: ", e);
        }
        return resultString;
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
        return configurations.get(name).getTimestamp();
    }

    private class Configuration {
        private long timestamp;
        private String content;

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
