package org.configureme.sources.configurationrepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.SourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * A source loader for rest api configuration repositories
 *
 * @author andriiskrypnyk
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
    public boolean isAvailable(ConfigurationSourceKey key) {
        if (key == null) {
            throw new IllegalArgumentException("isAvailable(): ConfigurationSourceKey is null");
        }
        return getResponse(key, PATH_CONFIGURATION).getStatus() == 200;
    }

    @Override
    public long getLastChangeTimestamp(ConfigurationSourceKey key) {
        if (key == null) {
            throw new IllegalArgumentException("getLastChangeTimestamp(): ConfigurationSourceKey is null");
        }
        ReplyObject replyObject = getResponse(key, PATH_LAST_CHANGE_TIMESTAMP).readEntity(ReplyObject.class);
        Map<String, Object> result = replyObject.getResults();
        return ((long) result.get(key.getName()));
    }

    @Override
    public String getContent(ConfigurationSourceKey key) {
        if (key.getType() != ConfigurationSourceKey.Type.REST) {
            throw new IllegalStateException("Can only get configuration for type: " + ConfigurationSourceKey.Type.REST);
        }
        Map<String, Object> result = getConfigurationReplyObject(key).getResults();
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
        } catch (JsonProcessingException e) {
            log.error("Json parsing exception: ", e);
        }
        return resultString;
    }

    private ReplyObject getConfigurationReplyObject(ConfigurationSourceKey key) {
        Response response = getResponse(key, PATH_CONFIGURATION);
        if (response.getStatus() != 200) {
            log.error("Http request to url: " + key.getRemoteConfigurationRepositoryUrl() + " is failed");
        }
        try {
            return response.readEntity(ReplyObject.class);
        } finally {
            response.close();
        }

    }

    private Response getResponse(ConfigurationSourceKey key, String additionalPath) {
        if (key.getRemoteConfigurationRepositoryUrl() == null) {
            throw new IllegalArgumentException("Target url unknown");
        }
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(key.getRemoteConfigurationRepositoryUrl()).path(additionalPath).path(key.getName());
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON_TYPE);
        return invocationBuilder.get();
    }
}
