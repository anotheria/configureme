package org.configureme.sources.configurationrepository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Roman Stetsiuk on 5/5/16.
 */
public enum ConfigurationsHolder {
    /**
     * The configurationsholder is a singleton.
     */
    INSTANCE;

    /**
     * key - configuration name
     * value - configuration context
     */
    private Map<String, Configuration> configurations = new HashMap<>();

    public String getConfigurationByname(String name) {
        return configurations.get(name).getContent();
    }

    public void putConfigurationWithName(String name, String context) {
        configurations.put(name, new Configuration(System.currentTimeMillis(), context));
    }

    public boolean isConfigurationWithNameExist(String name) {
        return configurations.containsKey(name);
    }

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
