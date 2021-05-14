package org.configureme.sources.configurationrepository;



import com.fasterxml.jackson.databind.ObjectMapper;
import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.SourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientBuilder;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
        final ReplyObject replyObject = this.getConfigurationReplyObject(key, PATH_LAST_CHANGE_TIMESTAMP);
        final Map<String, Object> result = replyObject.getResults();
        return ((long) result.get(key.getName()));
    }

    @Override
    public String getContent(final ConfigurationSourceKey key) {
        final Map<String, Object> result = this.getConfigurationReplyObject(key, PATH_CONFIGURATION).getResults();
        return this.mapObjectToString(result.get(key.getName()), key.getName());
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
        
//        final Client client = getClientConfig().build();
  //      final WebTarget resource = client.target(key.getRemoteConfigurationRepositoryUrl()).path(additionalPath).path(key.getName());
    //    return resource.request(MediaType.APPLICATION_JSON).header("Content-type", MediaType.APPLICATION_JSON).get(ReplyObject.class);
	return null;
    }

    private ClientBuilder getClientConfig() {
        ClientBuilder clientConfig;

        try {
            clientConfig = ClientBuilder.newBuilder();
            Class clazz = Class.forName("org.glassfish.jersey.jackson.JacksonFeature");
            clientConfig.register(clazz);
        } catch (ClassNotFoundException e) {
            try {
                Class clazz = Class.forName("org.jboss.resteasy.spi.ResteasyProviderFactory");
                Method factoryMethod = clazz.getDeclaredMethod("getInstance");
                Object singleton = factoryMethod.invoke(null, null);

                Class clazz2 = Class.forName(" org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder");
                final Constructor constructor = clazz2.getConstructor(clazz);
                clientConfig = (ClientBuilder) constructor.newInstance(singleton);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e1) {
                throw new IllegalStateException("No Jackson provider available");
            }
        }

        return clientConfig;
    }
}
