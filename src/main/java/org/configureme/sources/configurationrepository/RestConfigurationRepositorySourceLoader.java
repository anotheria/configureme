package org.configureme.sources.configurationrepository;



import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.SourceLoader;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @Override
    public boolean isAvailable(final ConfigurationSourceKey key) {
        if (key == null) {
            throw new IllegalArgumentException("isAvailable(): ConfigurationSourceKey is null");
        }
        return getConfigurationReplyObject(key, PATH_CONFIGURATION).isSuccess();
    }

    @Override
    public long getLastChangeTimestamp(final ConfigurationSourceKey key) {
        if (key == null) {
            throw new IllegalArgumentException("getLastChangeTimestamp(): ConfigurationSourceKey is null");
        }
        final ReplyObject replyObject = getConfigurationReplyObject(key, PATH_LAST_CHANGE_TIMESTAMP);
        final Map<String, Object> result = replyObject.getResults();
        return ((long) result.get(key.getName()));
    }

    @Override
    public String getContent(final ConfigurationSourceKey key) {
        if (key.getType() != ConfigurationSourceKey.Type.REST) {
            throw new IllegalStateException("Can only get configuration for type: " + ConfigurationSourceKey.Type.REST);
        }
        final Map<String, Object> result = getConfigurationReplyObject(key, PATH_CONFIGURATION).getResults();
        return mapObjectToString(result.get(key.getName()), key.getName());
    }

    private String mapObjectToString(final Object toMap, final String configName) {
        if (toMap == null) {
            throw new IllegalStateException("No configuration with name: " + configName);
        }
        final ObjectMapper mapper = new ObjectMapper();
        String resultString = null;
        try {
            resultString = mapper.writeValueAsString(toMap);
        } catch (final IOException e) {
            log.error("Json parsing exception: ", e);
        }
        return resultString;
    }


    private ReplyObject getConfigurationReplyObject(final ConfigurationSourceKey key, final String additionalPath) {
        if (key.getRemoteConfigurationRepositoryUrl() == null) {
            throw new IllegalArgumentException("Target url unknown");
        }
        final Client client = getClientConfig().build();
        final WebTarget resource = client.target(key.getRemoteConfigurationRepositoryUrl()).path(additionalPath).path(key.getName());
        return resource.request( MediaType.APPLICATION_JSON).header("Content-type", MediaType.APPLICATION_JSON).get(ReplyObject.class);
    }

    private ClientBuilder getClientConfig() {
        final ClientBuilder clientConfig = ClientBuilder.newBuilder();
        clientConfig.register(JacksonFeature.class);
        return clientConfig;
    }
}
