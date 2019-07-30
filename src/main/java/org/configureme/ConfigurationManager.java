package org.configureme;

import java.lang.annotation.Annotation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.configureme.annotations.ConfigureMe;
import org.configureme.environments.DynamicEnvironment;
import org.configureme.mbean.WatchedConfigFiles;
import org.configureme.mbean.util.MBeanRegisterUtil;
import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.ConfigurationSourceKey.Format;
import org.configureme.sources.ConfigurationSourceKey.Type;
import org.configureme.sources.ConfigurationSourceRegistry;
import org.configureme.util.ConfigUtils;
import org.configureme.util.StringUtils;

/**
 * Configuration manager (this is the one YOU must use) is a utility class for retrieval of configurations and automatical configurations of components.
 * Configured components are 'watched', any changes in the configuration source (file) lead to a reconfiguration.
 * The configuration manager also supports retrieval of the configurations in different environments. Its usually a good idea to specify a <b>defaultEnvironment</b>
 * by {@code -Dconfigureme.defaultEnvironment=a_b_c}...
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public enum ConfigurationManager {
    /**
     * The configuration manager is a singleton.
     */
    INSTANCE;

    /**
     * The default environment for configuration.
     */
    private Environment defaultEnvironment = null;

    /**
     * Default configuration source type (file is default, but fixture is also supported for junit tests and configserver may be supported in the near future).
     */
    private ConfigurationSourceKey.Type defaultConfigurationSourceType = Type.DEFAULT;
    /**
     * The format of the configuration file. At the moment only json is supported. The format of the configuration file decides which parser is used to parse the configuration.
     */
    private final ConfigurationSourceKey.Format defaultConfigurationSourceFormat = Format.DEFAULT;
    /**
     * Externally provided url for remote configuration repository.
     */
    private String remoteConfigurationRepositoryUrl = "";

    /**
     * Property name for the system property which ConfigurationManager checks to set its defaultEnvironment with at startup.
     */
    public static final String PROP_NAME_DEFAULT_ENVIRONMENT = "configureme.defaultEnvironment";
    /**
     * Property name for the system property which ConfigurationManager checks to set its remote configuration repository url with at startup.
     */
    public static final String PROP_NAME_CONFIGURATION_REPOSITORY = "configurationRepository";
    /**
     * Property name for the system property which ConfigurationManager checks to set its remote configuration repository url with at startup.
     */
    public static final String PROP_NAME_USED_IN_CONFIGURATION_REPOSITORY = "usedInConfigurationRepository";

    /**
     * Initializes the one and only instance of the ConfigurationManager.
     */
    ConfigurationManager() {
        MBeanRegisterUtil.regMBean(new WatchedConfigFiles());
        final String defEnvironmentAsString = System.getProperty(PROP_NAME_DEFAULT_ENVIRONMENT, "");
        defaultEnvironment = DynamicEnvironment.parse(defEnvironmentAsString);
        setExternalConfigurationRepository();
        setConfigurationRepository();
    }

    /**
     * This method is used to check and set an external configuration repository url for further processing.
     */
    private void setExternalConfigurationRepository() {
        final String rmtConfRepUrl = System.getProperty(PROP_NAME_CONFIGURATION_REPOSITORY);
        if (rmtConfRepUrl != null) {
            remoteConfigurationRepositoryUrl = rmtConfRepUrl;
            defaultConfigurationSourceType = Type.REST;
        }
    }

    /**
     * Check and set if configureme used in configuration repository
     */
    private void setConfigurationRepository() {
        final String usedForConfRep = System.getProperty(PROP_NAME_USED_IN_CONFIGURATION_REPOSITORY);
        if (usedForConfRep != null)
            defaultConfigurationSourceType = Type.REPOSITORY;
    }


    /**
     * Returns true if the object is properly annotated and can be configured by the configuration manager.
     * Calling configure with an Object o as parameter, where isConfigurable(o) will result in an Error.
     *
     * @param o
     *         object to check
     * @return true if object is properly and can be configured
     */
    public boolean isConfigurable(Object o) {
        return o.getClass().isAnnotationPresent(ConfigureMe.class);
    }

    /**
     * Configures a configurable component in the default environment. The object must be annotated with ConfigureMe and the configuration source must be present.
     *
     * @param o
     *         object to configure
     */
    public void configure(Object o) {
        configure(o, defaultEnvironment);
    }

    /**
     * Configures a configurable component in the default environment. The object must be annotated with ConfigureMe and the configuration source must be present.
     *
     * @param o
     *         object to configure
     * @param format
     *         a {@link org.configureme.sources.ConfigurationSourceKey.Format} object.
     */
    public void configure(final Object o, final Format format) {
        configure(o, defaultEnvironment, format);
    }

    /**
     * Configures a configurable component in the default environment. The object must be annotated with ConfigureMe and the configuration source must be present.
     *
     * @param o
     *         object to configure
     * @param name
     *         configuration name
     */
    public void configureAs(final Object o, final String name) {
        configureAs(o, defaultEnvironment, name, defaultConfigurationSourceFormat);
    }

    /**
     * Configures java bean in the default environment.
     *
     * @param o
     *         object to configure
     * @param name
     *         configuration name
     */
    public void configureBeanAs(final Object o, final String name) {
        configurePojoAs(o, name);
    }

    /**
     * Configures java bean in the given environment.
     *
     * @param o
     *         object to configure
     * @param name
     *         configuration name
     * @param in
     *         environment
     */
    public void configureBeanAsIn(final Object o, final String name, final Environment in) {
        configurePojoAsIn(o, name, in);
    }

    /**
     * Configures pojo object in the default environment.
     *
     * @param o
     *         object to configure
     * @param name
     *         configuration name
     */
    public void configurePojoAs(final Object o, final String name) {
        configurePojoAsIn(o, name, defaultEnvironment);
    }

    /**
     * Configures pojo object in the given environment.
     *
     * @param o
     *         object to configure
     * @param name
     *         configuration name
     * @param in
     *         environment
     */
    public void configurePojoAsIn(final Object o, final String name, final Environment in) {
        final ConfigureMe ann = new ConfigureMe() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return ConfigureMe.class;
            }

            @Override
            public boolean watch() {
                return false;
            }

            @Override
            public Type type() {
                return Type.FILE;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public boolean allfields() {
                return true;
            }
        };

        final ConfigurationSourceKey configSourceKey = new ConfigurationSourceKey();
        configSourceKey.setFormat(Format.JSON);
        configSourceKey.setTypeIfNotDefault(defaultConfigurationSourceType, ann.type());
        configSourceKey.setName(name);
        configSourceKey.setRemoteConfigurationRepositoryUrl(remoteConfigurationRepositoryUrl);

        ConfigurationProcessor.instance().configureInitially(configSourceKey, o, in, ann);
    }

    /**
     * Configures a configurable component in the given environment. The object must be annotated with ConfigureMe and the configuration must be present.
     *
     * @param o
     *         object to configure.
     * @param in
     *         the environment for the configuration.
     * @param configurationName
     *         name of the configuration.
     * @param format
     *         a {@link org.configureme.sources.ConfigurationSourceKey.Format} object.
     */
    public void configureAs(final Object o, final Environment in, final String configurationName, final Format format) {
        if (!isConfigurable(o))
            throw new IllegalArgumentException("Class " + o.getClass() + " is not annotated as ConfigureMe, called with: " + o + ", class: " + o.getClass());

        final Class<?> clazz = o.getClass();
        final ConfigureMe ann = clazz.getAnnotation(ConfigureMe.class);

        final ConfigurationSourceKey configSourceKey = new ConfigurationSourceKey();
        configSourceKey.setFormat(format);
        configSourceKey.setTypeIfNotDefault(defaultConfigurationSourceType, ann.type());
        configSourceKey.setName(configurationName);
        configSourceKey.setRemoteConfigurationRepositoryUrl(remoteConfigurationRepositoryUrl);

        configureAs(o, in, configSourceKey);
    }

    /**
     * Configures a configurable component in the given environment. The object must be annotated with ConfigureMe and the configuration must be present.
     *
     * @param o
     *         object to configure.
     * @param configSourceKey
     *         source definition.
     * @param in
     *         a {@link org.configureme.Environment} object.
     */
    public void configureAs(final Object o, final Environment in, final ConfigurationSourceKey configSourceKey) {
        if (!isConfigurable(o))
            throw new IllegalArgumentException("Class " + o.getClass() + " is not annotated as ConfigureMe, called with: " + o + ", class: " + o.getClass());

        final ConfigureMe ann = o.getClass().getAnnotation(ConfigureMe.class);
        ConfigurationProcessor.instance().configureInitially(configSourceKey, o, in, ann);
    }

    /**
     * Configure object in the given environment.
     *
     * @param o
     *         object to configure
     * @param in
     *         environment
     */
    public void configure(final Object o, final Environment in) {
        configure(o, in, defaultConfigurationSourceFormat);
    }

    /**
     * Configures a configurable component in the givent environment. The object must be annotated with ConfigureMe and the configuration must be present.
     *
     * @param o
     *         object to configure
     * @param in
     *         the environment for the configuration
     * @param format
     *         a {@link org.configureme.sources.ConfigurationSourceKey.Format} object.
     */
    public void configure(final Object o, final Environment in, final Format format) {
        if (!isConfigurable(o))
            throw new IllegalArgumentException("Class " + o.getClass() + " is not annotated as ConfigureMe, called with: " + o + ", class: " + o.getClass());

        final Class<?> clazz = o.getClass();
        final ConfigureMe ann = clazz.getAnnotation(ConfigureMe.class);
        final String configurationName = StringUtils.isEmpty(ann.name()) ? ConfigUtils.extractConfigurationNameFromClassName(clazz) : ann.name();
        configureAs(o, in, configurationName, format);
    }

    /**
     * Returns a configuration snapshot for this configurationname in the global environment. Snapshot means that only the part of the
     * configuration which is valid now and only for global environment is returned.
     *
     * @param configurationName
     *         the name of the configuration to check
     * @return a configuration snapshot for this configurationname in the global environment
     */
    public Configuration getConfiguration(final String configurationName) {
        return getConfiguration(configurationName, defaultEnvironment);
    }

    /**
     * Returns a configuration snapshot for this configurationname in the given environment. Snapshot means that only the part of the
     * configuration which is valid now and only for the given environment is returned.
     * defaultConfigurationSourceFormat and defaultConfigurationSourceType are used for format and type. At the moment its JSON and File.
     *
     * @param configurationName
     *         the name of the configuration source.
     * @param in
     *         the environment
     * @return a configuration snapshot for this configurationname in the given environment
     */
    public Configuration getConfiguration(final String configurationName, final Environment in) {
        final ConfigurationSourceKey configSourceKey = new ConfigurationSourceKey();
        configSourceKey.setFormat(defaultConfigurationSourceFormat);
        configSourceKey.setType(defaultConfigurationSourceType);
        configSourceKey.setName(configurationName);

        return ConfigurationProcessor.instance().getConfiguration(configSourceKey, in);
    }

    /**
     * Sets the default environment. The default environment is used in methods configure(Object) and getConfiguration(String) which have no explicit Environemnt parameter.
     *
     * @param anEnvironment
     *         a {@link org.configureme.Environment} object.
     */
    @SuppressFBWarnings ("ME_ENUM_FIELD_SETTER")
    public final void setDefaultEnvironment(Environment anEnvironment) {
        defaultEnvironment = anEnvironment;
    }

    /**
     * Returns the previously set default Environment. If no environment has been set, either by method call, or by property, GlobalEnvironment.INSTANCE is returned.
     *
     * @return the previously set default Environment
     */
    public final Environment getDefaultEnvironment() {
        return defaultEnvironment;
    }

    /**
     * Used to shutdown the confirmation manager in a reloadable environment like tomcat or any other web container.
     * If you want to ensure cleanup on application stop, call ConfigurationManager.INSTANCE.shutdown();
     */
    public void shutdown() {
        ConfigurationSourceRegistry.INSTANCE.shutdown();
    }
}
