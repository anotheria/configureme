package org.configureme.sources.configurationrepository;


import org.configureme.ConfigurationManager;
import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.SourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
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
public class ConfigurationRepositorySourceLoader implements SourceLoader {
    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ConfigurationRepositorySourceLoader.class);
    /**
     * Configuration path.
     */
    private static final String PATH_FILE = "file";
	private static final String PATH_EXISTS = "exists";
    /**
     * Last change timestamp path.
     */
    private static final String PATH_LAST_CHANGE_TIMESTAMP = "lastModified";

    private String repositoryUrl;
    private String profile = "";

	public ConfigurationRepositorySourceLoader(){
		repositoryUrl = System.getProperty(ConfigurationManager.PROP_NAME_REPOSITORY_URL);
		profile       = System.getProperty(ConfigurationManager.PROP_NAME_DEFAULT_PROFILE);

		//add 'repository' at the end of the repository url if the user didn't do it
		if (!repositoryUrl.endsWith("/repository")){
			if (!repositoryUrl.endsWith("/"))
				repositoryUrl += "/";
			repositoryUrl += "repository";
		}
	}

    @Override
    public boolean isAvailable(final ConfigurationSourceKey key) {
        if (key == null) {
            throw new IllegalArgumentException("isAvailable(): ConfigurationSourceKey is null");
        }
        return getConfigurationReplyObject(key, PATH_EXISTS).isSuccess();
    }

    @Override
    public long getLastChangeTimestamp(final ConfigurationSourceKey key) {
        if (key == null) {
            throw new IllegalArgumentException("getLastChangeTimestamp(): ConfigurationSourceKey is null");
        }
        final ReplyObject replyObject = this.getConfigurationReplyObject(key, PATH_LAST_CHANGE_TIMESTAMP);
        final Map<String, Object> result = replyObject.getResults();
        return (long) result.get(getArtefactNameInRepository(key));
    }

    @Override
    public String getContent(final ConfigurationSourceKey key) {
		ReplyObject replyObject = getConfigurationReplyObject(key, PATH_FILE);
		String content = (String)replyObject.getResults().get(getArtefactNameInRepository(key));
		if (replyObject.isSuccess()==false)
			return null;
		return content;
    }
    
    private ReplyObject getConfigurationReplyObject(final ConfigurationSourceKey key, final String additionalPath) {
        
    	final Client client = getClientConfig().build();
	    final WebTarget resource = client.target(repositoryUrl).path(additionalPath).path(profile).path(getArtefactNameInRepository(key));
	    return resource.request(MediaType.APPLICATION_JSON).header("Content-type", MediaType.APPLICATION_JSON).get(ReplyObject.class);

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

    private String getArtefactNameInRepository(ConfigurationSourceKey key){
		return key.getName()+"."+key.getFormat().getExtension();
	}
}
