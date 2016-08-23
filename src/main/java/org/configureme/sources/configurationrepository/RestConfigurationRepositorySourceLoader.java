package org.configureme.sources.configurationrepository;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.SourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Map;

/**
 * A source loader for rest api configuration repositories
 *
 * @author andriiskrypnyk
 * @version $Id: $Id
 */
public class RestConfigurationRepositorySourceLoader implements SourceLoader {
    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(RestConfigurationRepositorySourceLoader.class);
    /**
     * Configuration path.
     */
    private static final String PATH_CONFIGURATION = "configurations";
    /**
     * Last change timestamp path.
     */
    private static final String PATH_LAST_CHANGE_TIMESTAMP = "lastChangeTimestamp";

    /** {@inheritDoc} */
    @Override
    public boolean isAvailable(ConfigurationSourceKey key) {
        if (key == null) {
            throw new IllegalArgumentException("isAvailable(): ConfigurationSourceKey is null");
        }
        return getConfigurationReplyObject(key, PATH_CONFIGURATION).isSuccess();
    }

    /** {@inheritDoc} */
    @Override
    public long getLastChangeTimestamp(ConfigurationSourceKey key) {
        if (key == null) {
            throw new IllegalArgumentException("getLastChangeTimestamp(): ConfigurationSourceKey is null");
        }
        ReplyObject replyObject = getConfigurationReplyObject(key, PATH_LAST_CHANGE_TIMESTAMP);
        Map<String, Object> result = replyObject.getResults();
        return ((long) result.get(key.getName()));
    }

    /** {@inheritDoc} */
    @Override
    public String getContent(ConfigurationSourceKey key) {
        if (key.getType() != ConfigurationSourceKey.Type.REST) {
            throw new IllegalStateException("Can only get configuration for type: " + ConfigurationSourceKey.Type.REST);
        }
        Map<String, Object> result = getConfigurationReplyObject(key, PATH_CONFIGURATION).getResults();
        return mapObjectToString(result.get(key.getName()), key.getName());
    }

    private String mapObjectToString(Object toMap, String configName) {
        if (toMap == null) {
            throw new IllegalStateException("No configuration with name: " + configName);
        }
        ObjectMapper mapper = new ObjectMapper();
        String resultString = null;
        try {
            resultString = mapper.writeValueAsString(toMap);
        } catch (IOException e) {
            log.error("Json parsing exception: ", e);
        }
        return resultString;
    }


    private ReplyObject getConfigurationReplyObject(ConfigurationSourceKey key, String additionalPath) {
        if (key.getRemoteConfigurationRepositoryUrl() == null) {
            throw new IllegalArgumentException("Target url unknown");
        }
        Client client = Client.create(getClientConfig());
        WebResource resource = client.resource(key.getRemoteConfigurationRepositoryUrl()).path(additionalPath).path(key.getName());
        return resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).get(ReplyObject.class);
    }

    private ClientConfig getClientConfig() {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getClasses().add(JacksonJaxbJsonProvider.class);
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        return clientConfig;
    }
}
