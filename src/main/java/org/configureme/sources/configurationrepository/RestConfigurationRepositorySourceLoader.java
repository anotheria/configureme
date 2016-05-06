package org.configureme.sources.configurationrepository;

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
    private static final String PATH_CONFIGURATION = "configuration";
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
        return ((String) result.get(key.getName()));
    }

    private ReplyObject getConfigurationReplyObject(ConfigurationSourceKey key) {
        Response response = getResponse(key, PATH_CONFIGURATION);
        if (response.getStatus() != 200) {
            log.error("Http request to url: " + key.getRemoteConfigurationRepositoryUrl() + " is failed");
        }
        return response.readEntity(ReplyObject.class);
    }

    private Response getResponse(ConfigurationSourceKey key, String additionalPath) {
        Client client = ClientBuilder.newClient();
        if (key.getRemoteConfigurationRepositoryUrl() == null) {
            throw new IllegalArgumentException("Target url unknown");
        }
        WebTarget webTarget = client.target(key.getRemoteConfigurationRepositoryUrl()).path(additionalPath).path(key.getName());
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON_TYPE);
        return invocationBuilder.get();
    }
}
