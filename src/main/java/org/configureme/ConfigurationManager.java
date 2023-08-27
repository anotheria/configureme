package org.configureme;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.configureme.annotations.AfterConfiguration;
import org.configureme.annotations.AfterInitialConfiguration;
import org.configureme.annotations.AfterReConfiguration;
import org.configureme.annotations.BeforeConfiguration;
import org.configureme.annotations.BeforeInitialConfiguration;
import org.configureme.annotations.BeforeReConfiguration;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureAlso;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.DontConfigure;
import org.configureme.annotations.Set;
import org.configureme.annotations.SetAll;
import org.configureme.annotations.SetIf;
import org.configureme.environments.DynamicEnvironment;
import org.configureme.mbean.ConfigInfo;
import org.configureme.mbean.WatchedConfigFiles;
import org.configureme.mbean.util.MBeanRegisterUtil;
import org.configureme.parser.ConfigurationParser;
import org.configureme.parser.ConfigurationParserException;
import org.configureme.parser.ParsedAttribute;
import org.configureme.parser.ParsedConfiguration;
import org.configureme.parser.json.JsonParser;
import org.configureme.parser.properties.PropertiesParser;
import org.configureme.repository.ArrayValue;
import org.configureme.repository.Artefact;
import org.configureme.repository.CompositeValue;
import org.configureme.repository.ConfigurationRepository;
import org.configureme.repository.IncludeValue;
import org.configureme.repository.PlainValue;
import org.configureme.repository.Value;
import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.ConfigurationSourceKey.Format;
import org.configureme.sources.ConfigurationSourceKey.Type;
import org.configureme.sources.ConfigurationSourceRegistry;
import org.configureme.util.ReflectionUtils;
import org.configureme.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

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
	 * The configurationmanager is a singleton.
	 */
	INSTANCE;

	/**
	 * Set of classes specifying plain attribute types.
	 * Allows quickly check whether an attribute is plain or not.
	 */
	private static final Collection<Class<?>> PLAIN_TYPES = new HashSet<Class<?>>(
			Arrays.asList(
					String.class,
					Boolean.class, boolean.class,
					Short.class, short.class,
					Integer.class, int.class,
					Long.class, long.class,
					Byte.class, byte.class,
					Float.class, float.class,
					Double.class, double.class));

	/**
	 * The default environment for configuration.
	 */
	private Environment defaultEnvironment = null;

	/**
	 * Default configuration source type (file is default, but fixture is also supported for junit tests and configserver may be supported in the near future).
	 */
	private ConfigurationSourceKey.Type defaultConfigurationSourceType = Type.FILE;
	/**
	 * The format of the configuration file. At the moment only json is supported. The format of the configuration file decides which parser is used to parse the configuration.
	 */
	private final ConfigurationSourceKey.Format defaultConfigurationSourceFormat = Format.JSON;
    /**
     * Externally provided url for remote configuration repository.
     */
    private String remoteConfigurationRepositoryUrl = "";
	/**
	 * A map which contains configuration parser for different formats.
	 */
	private final Map<Format, ConfigurationParser> parsers = new ConcurrentHashMap<>();

	/**
	 * Cache for object in oder to cover situation with loops in ConfigureAlso
	 */
	private final ThreadLocal<Map<String, Map<Environment, Object>>> localCache = new ThreadLocal<>();
	/**
	 * Annotations to call before initial configuration.
	 */
	private static final Class<? extends Annotation>[] CALL_BEFORE_INITIAL_CONFIGURATION = (Class<? extends Annotation>[]) new Class<?>[]{
			BeforeInitialConfiguration.class, BeforeConfiguration.class
	};

	/**
	 * Annotations to call after initial configuration.
	 */
	private static final Class<? extends Annotation>[] CALL_AFTER_INITIAL_CONFIGURATION = (Class<? extends Annotation>[]) new Class<?>[]{
			AfterConfiguration.class, AfterInitialConfiguration.class
	};

	/**
	 * Annotations to call before reconfiguration.
	 */
	private static final Class<? extends Annotation>[] CALL_BEFORE_RE_CONFIGURATION = (Class<? extends Annotation>[]) new Class<?>[]{
			BeforeReConfiguration.class, BeforeConfiguration.class
	};

	/**
	 * Annotations to call before after reconfiguration.
	 */
	private static final Class<? extends Annotation>[] CALL_AFTER_RE_CONFIGURATION = (Class<? extends Annotation>[]) new Class<?>[]{
			AfterConfiguration.class, AfterReConfiguration.class
	};

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
	 * Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(ConfigurationManager.class);

	/**
	 * Initializes the one and only instance of the ConfigurationManager.
	 */
	ConfigurationManager() {
		MBeanRegisterUtil.regMBean(new WatchedConfigFiles());
		final String defEnvironmentAsString = System.getProperty(PROP_NAME_DEFAULT_ENVIRONMENT, "");
		defaultEnvironment = DynamicEnvironment.parse(defEnvironmentAsString);
        setExternalConfigurationRepository();
		setConfigurationRepository();

		parsers.put(Format.JSON, new JsonParser());
		parsers.put(Format.PROPERTIES, new PropertiesParser());
	}


	/**
	 * Returns true if the object is properly annotated and can be configured by the configuration manager. Calling configure with an Object o as parameter, where isConfigurable(o) will result in an
	 * Error.
	 *
	 * @param o object to check
	 * @return true if object is properly and can be configured
	 */
	public boolean isConfigurable(Object o) {
		return o.getClass().isAnnotationPresent(ConfigureMe.class);
	}

	/**
	 * Configures a configurable component in the default environment. The object must be annotated with ConfigureMe and the configuration source must be present.
	 *
	 * @param o object to configure
	 */
	public void configure(Object o) {
		configure(o, defaultEnvironment);
	}

	/**
	 * Configures a configurable component in the default environment. The object must be annotated with ConfigureMe and the configuration source must be present.
	 *
	 * @param o object to configure
	 * @param format a {@link org.configureme.sources.ConfigurationSourceKey.Format} object.
	 */
	public void configure(final Object o, final Format format) {
		configure(o, defaultEnvironment, format);
	}

	/**
	 * Configures a configurable component in the default environment. The object must be annotated with ConfigureMe and the configuration source must be present.
	 *
	 * @param o    object to configure
	 * @param name configuration name
	 */
	public void configureAs(final Object o, final String name) {
		configureAs(o, defaultEnvironment, name, defaultConfigurationSourceFormat);
	}

	/**
	 * Configures java bean in the default environment.
	 *
	 * @param o    object to configure
	 * @param name configuration name
	 */
	public void configureBeanAs(final Object o, final String name) {
		configurePojoAs(o, name);
	}

	/**
	 * Configures java bean in the given environment.
	 *
	 * @param o    object to configure
	 * @param name configuration name
	 * @param in   environment
	 */
	public void configureBeanAsIn(final Object o, final String name, final Environment in) {
		configurePojoAsIn(o, name, in);
	}

	/**
	 * Configures pojo object in the default environment.
	 *
	 * @param o    object to configure
	 * @param name configuration name
	 */
	public void configurePojoAs(final Object o, final String name) {
		Environment in = defaultEnvironment;
		configurePojoAsIn(o, name, in);
	}

	/**
	 * Configures pojo object in the given environment.
	 *
	 * @param o    object to configure
	 * @param name configuration name
	 * @param in   environment
	 */
	public void configurePojoAsIn(final Object o, final String name, final Environment in) {
		ConfigureMe ann = new ConfigureMe() {

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

		configureInitially(configSourceKey, o, in, ann);
	}

	/**
	 * Configures a configurable component in the given environment. The object must be annotated with ConfigureMe and the configuration must be present.
	 *
	 * @param o                 object to configure.
	 * @param in                the environment for the configuration.
	 * @param configurationName name of the configuration.
	 * @param format a {@link org.configureme.sources.ConfigurationSourceKey.Format} object.
	 */
	public void configureAs(final Object o, final Environment in, final String configurationName, final Format format) {
		if (!isConfigurable(o))
			throw new IllegalArgumentException("Class " + o.getClass() + " is not annotated as ConfigureMe, called with: " + o + ", class: " + o.getClass());

		final Class<?> clazz = o.getClass();
		ConfigureMe ann = clazz.getAnnotation(ConfigureMe.class);

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
	 * @param o               object to configure.
	 * @param configSourceKey source definition.
	 * @param in a {@link org.configureme.Environment} object.
	 */
	public void configureAs(final Object o, final Environment in, final ConfigurationSourceKey configSourceKey) {
		if (!isConfigurable(o))
			throw new IllegalArgumentException("Class " + o.getClass() + " is not annotated as ConfigureMe, called with: " + o + ", class: " + o.getClass());

		ConfigureMe ann = o.getClass().getAnnotation(ConfigureMe.class);

		configureInitially(configSourceKey, o, in, ann);
	}

	/**
	 * Configure object in the given environment.
	 *
	 * @param o  object to configure
	 * @param in environment
	 */
	public void configure(final Object o, final Environment in) {
		configure(o, in, defaultConfigurationSourceFormat);
	}

	/**
	 * Configures a configurable component in the givent environment. The object must be annotated with ConfigureMe and the configuration must be present.
	 *
	 * @param o  object to configure
	 * @param in the environment for the configuration
	 * @param format a {@link org.configureme.sources.ConfigurationSourceKey.Format} object.
	 */
	public void configure(final Object o, final Environment in, final Format format) {
		if (!isConfigurable(o))
			throw new IllegalArgumentException("Class " + o.getClass() + " is not annotated as ConfigureMe, called with: " + o + ", class: " + o.getClass());

		final Class<?> clazz = o.getClass();
		final ConfigureMe ann = clazz.getAnnotation(ConfigureMe.class);
		final String configurationName = StringUtils.isEmpty(ann.name()) ? extractConfigurationNameFromClassName(clazz) : ann.name();
		configureAs(o, in, configurationName, format);
	}

    /**
     * This method is used to check and set an external configuration repository url for further processing.
     */
    private void setExternalConfigurationRepository() {
        final String rmtConfRepUrl = System.getProperty(PROP_NAME_CONFIGURATION_REPOSITORY);
		if(rmtConfRepUrl != null){
            remoteConfigurationRepositoryUrl = rmtConfRepUrl;
            defaultConfigurationSourceType = Type.REST;
        }
    }

	/**
	 * Check and set if configureme used in configuration repository
	 */
	private void setConfigurationRepository() {
		final String usedForConfRep = System.getProperty(PROP_NAME_USED_IN_CONFIGURATION_REPOSITORY);
		if(usedForConfRep != null)
			defaultConfigurationSourceType = Type.REPOSITORY;
	}

	/**
	 * Internal method used at initial, user triggered configuration.
	 *
	 * @param key the source key
	 * @param o   the object to configure
	 * @param in  the environment
	 * @param ann the configureme annotation instance with which o.getClass() was annotated.
	 */
	private void configureInitially(final ConfigurationSourceKey key, final Object o, final Environment in, final ConfigureMe ann) {

		configure(key, o, in, CALL_BEFORE_INITIAL_CONFIGURATION, CALL_AFTER_INITIAL_CONFIGURATION, ann);

		if (ann.watch()) {
			final ConfigurableWrapper wrapper = new ConfigurableWrapper(key, o, in);
			ConfigurationSourceRegistry.INSTANCE.addWatchedConfigurable(wrapper);
			MBeanRegisterUtil.regMBean(new ConfigInfo(key.getName()), key.getName());
		}

	}

	/**
	 * This method is used internally for calls to annotations at the start and the end of each configuration.
	 *
	 * @param configurable
	 * @param methods
	 * @param annotationClasses
	 */
	private void callAnnotations(final Object configurable, final Method[] methods, final Class<? extends Annotation>[] annotationClasses) {
		//check for annotations to call and call 'before' annotations
		for (final Method m : methods) {
			//System.out.println("Checking methid "+m);
			for (final Class<? extends Annotation> anAnnotationClass : annotationClasses) {
				//System.out.println("\tChecking annotation "+anAnnotationClass);
				final Annotation anAnnotation = m.getAnnotation(anAnnotationClass);
				//System.out.println("\t\t-->"+anAnnotation);
				if (anAnnotation == null)
					continue;

				try {
					m.invoke(configurable);
				} catch (final IllegalAccessException e) {
					log.error("callAnnotations(" + Arrays.toString(methods) + ", " + Arrays.toString(annotationClasses) + ')', e);
					throw new AssertionError("Error declaration in method " + m + ", wrong declaration (public void " + m.getName() + " expected)? - " + e.getMessage());
				} catch (final InvocationTargetException e) {
					log.error("callAnnotations(Exception in annotated method: " + m + ')', e);
					throw new RuntimeException("Exception in annotated method: " + e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * This method executes the configuration.
	 *
	 * @param key        the key for the configuration source
	 * @param o          the object to configure
	 * @param in         environment in which the object runs
	 * @param callBefore annotations, methods annotated with those will be called prior to the configuration
	 * @param callAfter  annotations, methods annotated with those will be called after the configuration
	 */
	private void configure(final ConfigurationSourceKey key, final Object o, final Environment in, Class<? extends Annotation>[] callBefore, final Class<? extends Annotation>[] callAfter, ConfigureMe ann) {
		Class<?> clazz = o.getClass();

		if (ann == null)
			ann = clazz.getAnnotation(ConfigureMe.class);
		if (ann == null)
			throw new AssertionError("An unannotated class shouldn't make it sofar, obj: " + o + " class " + o.getClass());

		final boolean configureAllFields = ann.allfields();

		final Configuration configuration = getConfiguration(key, in);
		configure(configuration, o, callBefore, callAfter, configureAllFields, in);

		// added all external configuration watchers
		if (ann.watch()) {
			for (ConfigurationSourceKey sourceKey : configuration.getExternalConfigurations()) {
				ConfigurableWrapper wrapper = new ConfigurableWrapper(sourceKey, o, in);
				ConfigurationSourceRegistry.INSTANCE.addWatchedConfigurable(wrapper);
				MBeanRegisterUtil.regMBean(new ConfigInfo(sourceKey.getName()), sourceKey.getName());
			}
		}

		if (log != null && log.isDebugEnabled()) {
			log.debug("Finished configuration of " + o + " as " + key);
		}
	}


	/**
	 * Applies specified configuration to specified object.
	 *
	 * @param config             the configuration to be applied
	 * @param o                  the object to be configured
	 * @param callBefore         annotations, methods annotated with those will be called prior to the configuration
	 * @param callAfter          annotations, methods annotated with those will be called after the configuration
	 * @param configureAllFields specifies whether to set all fields regardless if they are marked configured or not
	 */
	private void configure(final Configuration config, final Object o, final Class<? extends Annotation>[] callBefore, final Class<? extends Annotation>[] callAfter, final boolean configureAllFields, final Environment environment) {
		setCachedObject(config.getName(), environment, o);
		final Class<?> clazz = o.getClass();
		final Method[] methods = clazz.getDeclaredMethods();
		callAnnotations(o, methods, callBefore);

		//first set fields
		final List<Field> fields = ReflectionUtils.getAllFields(clazz);
		for (final Field f : fields) {
			if (f.isAnnotationPresent(ConfigureAlso.class)) {
				Object externalConfig = null;
				//TODO: check if constructor exist
				try {
					final Class<?> externalConfigClass = f.getType();
					externalConfig = externalConfigClass.newInstance();
					if (!externalConfigClass.isAnnotationPresent(ConfigureMe.class))
						continue;
					final ConfigureMe ann = externalConfigClass.getAnnotation(ConfigureMe.class);
					final Object cachedObject = getCachedObject(ann.name(), environment);
					if (cachedObject == null) {
						ConfigurationManager.INSTANCE.configure(externalConfig, environment);
						setCachedObject(ann.name(), environment, externalConfig);
					} else {
						externalConfig = cachedObject;
					}
				} catch (final Exception e) {
					log.error("Can't create external config task for class name=" + f.getType().getName());
				}
				if (Modifier.isPublic(f.getModifiers())) {
					try {
						f.set(o, externalConfig);
					} catch (final Exception e) {
						log.warn(f + ".set(" + o + ", " + externalConfig + ')', e);
					}
				} else {
					final String methodName = "set" + f.getName().toUpperCase().charAt(0) + f.getName().substring(1);
					try {
						final Method toSet = clazz.getMethod(methodName, f.getType());
						toSet.invoke(o, externalConfig);
					} catch (final NoSuchMethodException e) {
						log.error("can't find method " + methodName + " (" + f.getType() + ')');
					} catch (final Exception e) {
						log.error("can't set " + f.getName() + " to " + externalConfig + ", because: ", e);
					}
				}
				continue;
			}

			if (f.isAnnotationPresent(Configure.class) || (configureAllFields && !f.isAnnotationPresent(DontConfigure.class))) {
				final String attributeName = f.getName();
				final Value attributeValue = config.getAttribute(attributeName);
				if (attributeValue == null)
					continue;
				if (Modifier.isPublic(f.getModifiers())) {
					try {
						f.set(o, resolveValue(f.getType(), attributeValue, callBefore, callAfter, configureAllFields, environment));
					} catch (final Exception e) {
						log.warn(f + ".set(" + o + ", " + attributeValue + ')', e);
					}
				} else {
					final String methodName = "set" + f.getName().toUpperCase().charAt(0) + f.getName().substring(1);
					try {
						final Method toSet = clazz.getMethod(methodName, f.getType());
						toSet.invoke(o, resolveValue(f.getType(), attributeValue, callBefore, callAfter, configureAllFields, environment));
					} catch (final NoSuchMethodException e) {
						log.error("can't find method " + methodName + " (" + f.getType() + ')');
					} catch (final Exception e) {
						log.error("can't set " + attributeName + " to " + attributeValue + ", because: ", e);
					}
				}
			}
		}
		//end set fields

		for (final Method method : methods) {
			if (method.isAnnotationPresent(SetAll.class)) {
				final Collection<Entry<String, Value>> entries = config.getEntries();
				log.debug("Calling method " + method + " with " + entries);
				for (final Entry<String, Value> entry : entries) {
					try {
						method.invoke(o, entry.getKey(), resolveValue(method.getParameterTypes()[1], entry.getValue(), callBefore, callAfter, configureAllFields, environment));
					} catch (final Exception e) {
						log.warn(method.getName()
								+ "invoke(" + o + ", " + entry.getKey() + ", " + entry.getValue() + ')', e);
					}
				}
			}
			if (method.isAnnotationPresent(SetIf.class)) {
				final Collection<Entry<String, Value>> entries = config.getEntries();
				final SetIf setIfAnnotation = method.getAnnotation(SetIf.class);
				for (final Entry<String, Value> entry : entries) {
					if (SetIf.ConditionChecker.satisfyCondition(setIfAnnotation, entry.getKey())) {
						log.debug("Calling method " + method + " with parameters : \"" + entry.getKey() + "\", \"" + entry.getValue() + '"');
						try {
							method.invoke(o, entry.getKey(), resolveValue(method.getParameterTypes()[1], entry.getValue(), callBefore, callAfter, configureAllFields, environment));
						} catch (final Exception e) {
							log.warn(method.getName() + ".invoke(" + o + ", " + entry.getKey() + ", " + entry.getValue() + ')', e);
						}
					}
				}
			}
			if (method.isAnnotationPresent(Set.class)) {
				log.debug("method " + method + " is annotated");
				final Set setAnnotation = method.getAnnotation(Set.class);
				final String attributeName = setAnnotation.value();
				final Value attributeValue = config.getAttribute(attributeName);
				if (attributeValue != null) {
					log.debug("setting " + method.getName() + " to " + attributeValue + " configured by " + attributeName);
					Object value = null;
					try {
						value = resolveValue(method.getParameterTypes()[0], attributeValue, callBefore, callAfter, configureAllFields, environment);
					}catch(final Exception e){
						log.warn("Can't resolve value '"+attributeName+
								(method.getParameterTypes().length == 0 ? "'" :"' as '"+method.getParameterTypes()[0])
								+"' value: '"+attributeValue+"'", e);
					}

					try {
						if (value!=null) {
							method.invoke(o, value);
						}
					} catch (final Exception e) {
						log.warn(method.getName() + ".invoke(" + o + ", " + attributeValue + ')', e);
					}
				}

			}
		}

		callAnnotations(o, methods, callAfter);
	}

	/**
	 * Called by ConfigurationSource monitors/listeners to trigger a reconfiguration of a component.
	 *
	 * @param key
	 * @param o
	 * @param in
	 */
	void reconfigure(final ConfigurationSourceKey key, final Object o, final Environment in) {
		configure(key, o, in, CALL_BEFORE_RE_CONFIGURATION, CALL_AFTER_RE_CONFIGURATION, null);
	}


	/**
	 * Returns a configuration snapshot for this configurationname in the global environment. Snapshot means that only the part of the
	 * configuration which is valid now and only for global environment is returned.
	 *
	 * @param configurationName the name of the configuration to check
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
	 * @param configurationName the name of the configuration source.
	 * @param in                the environment
	 * @return a configuration snapshot for this configurationname in the given environment
	 */
	public Configuration getConfiguration(final String configurationName, final Environment in) {
		final ConfigurationSourceKey configSourceKey = new ConfigurationSourceKey();
		configSourceKey.setFormat(defaultConfigurationSourceFormat);
		configSourceKey.setType(defaultConfigurationSourceType);
		configSourceKey.setName(configurationName);

		return getConfiguration(configSourceKey, in);
	}

	/**
	 * Internal method for configuration retrieval.
	 *
	 * @param configSourceKey
	 * @param in
	 * @return
	 */
	private Configuration getConfiguration(final ConfigurationSourceKey configSourceKey, final Environment in) {

		//for the first we will hardcode file as config source and json as config format.
		final String configurationName = configSourceKey.getName();
		if (!ConfigurationRepository.INSTANCE.hasConfiguration(configurationName)) {
			if (!ConfigurationSourceRegistry.INSTANCE.isConfigurationAvailable(configSourceKey)) {
				throw new IllegalArgumentException("No such configuration: " + configurationName + " (" + configSourceKey + ')');
			}
			//reading config
			final String content = ConfigurationSourceRegistry.INSTANCE.readConfigurationSource(configSourceKey);

			final ConfigurationParser parser = parsers.get(configSourceKey.getFormat());
			if (parser == null)
				throw new IllegalArgumentException("Format " + configSourceKey.getFormat() + " is not supported (yet).");
			ParsedConfiguration pa;
			try {
				pa = parser.parseConfiguration(configurationName, content);
			} catch (final ConfigurationParserException e) {
				log.error("getConfiguration(" + configurationName + ", " + in + ')', e);
				throw new IllegalArgumentException(configSourceKey + " is not parseable: " + e.getMessage(), e);
			}
			System.out.println("Parsed "+pa);
			final List<? extends ParsedAttribute<?>> attributes = pa.getAttributes();
			final Artefact art = ConfigurationRepository.INSTANCE.createArtefact(configurationName);
			// set external includes
			for (final String include : pa.getExternalConfigurations())
				art.addExternalConfigurations(new ConfigurationSourceKey(defaultConfigurationSourceType, defaultConfigurationSourceFormat, include));

			for (final ParsedAttribute<?> a : attributes)
				art.addAttributeValue(a.getName(), a.getValue(), a.getEnvironment());

		}

		return ConfigurationRepository.INSTANCE.getConfiguration(configurationName, in);
	}

	/**
	 * Sets the default environment. The default environment is used in methods configure(Object) and getConfiguration(String) which have no explicit Environemnt parameter.
	 *
	 * @param anEnvironment a {@link org.configureme.Environment} object.
	 */
	@SuppressFBWarnings("ME_ENUM_FIELD_SETTER")
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
	 * Calculates default configuration artefact name for a java class.
	 *
	 * @param targetClazz target class
	 * @return default configuration artefact name for a given java class. For MyConfigurable it would be "myconfigurable"
	 */
	private static String extractConfigurationNameFromClassName(final Class<?> targetClazz) {
		return targetClazz.getName().substring(targetClazz.getName().lastIndexOf('.') + 1).toLowerCase();
	}

    /**
     * Resolves attribute value to an instance of specified value class.
     *
     * @param valueClass     class of the resulting value instance
     * @param attributeValue array attribute value specifying the configuration of the resulting instance
     * @param callBefore     annotations, methods annotated with those will be called prior to the configuration
     * @param callAfter      annotations, methods annotated with those will be called after the configuration
     * @return an instance of the specified value class which is configured according to the specified attribute value.
     */
    private Object resolveValue(final Class<?> valueClass, Value attributeValue, final Class<? extends Annotation>[] callBefore, Class<? extends Annotation>[] callAfter, final boolean configureAllFields, final Environment environment) throws InstantiationException, IllegalAccessException {
        while (true) {
            boolean isValueClassPlain = isPlain(valueClass);
            boolean isValueClassDummy = valueClass.equals(Object.class) || valueClass.equals(String.class);

            if (attributeValue instanceof PlainValue && !valueClass.isArray() && (isValueClassPlain || isValueClassDummy))
                return resolvePlainValue(valueClass, (PlainValue) attributeValue);
            if (attributeValue instanceof CompositeValue && !valueClass.isArray() && (!isValueClassPlain || isValueClassDummy))
                return resolveCompositeValue(valueClass, (CompositeValue) attributeValue, callBefore, callAfter, configureAllFields);
            if (attributeValue instanceof ArrayValue && (valueClass.isArray() || isValueClassDummy))
                return resolveArrayValue(valueClass, (ArrayValue) attributeValue, callBefore, callAfter, configureAllFields, environment);
            if (attributeValue instanceof IncludeValue && (!valueClass.isArray() || !isValueClassDummy)) {
                attributeValue = ((IncludeValue) attributeValue).getIncludedValue(environment);
                continue;
            }

            throw new IllegalArgumentException("Can't resolve attribute value " + attributeValue + " to type: " + valueClass.getCanonicalName());
        }
    }

	/**
	 * Checks whether the class specifies a plain type or array (with arbitrary number of dimensions) of plain types.
	 *
	 * @param type the type to be checked
	 * @return true if the type is plain, false otherwise
	 */
	private static boolean isPlain(final Class<?> type) {
		return (type.isArray())
				? isPlain(type.getComponentType())
				: PLAIN_TYPES.contains(type) || Enum.class.isAssignableFrom(type);
	}

	private static Object resolvePlainValue(final Class<?> type, final PlainValue value) {
		if (type == null)
			throw new IllegalArgumentException("Checkstyle forced me to do this, apparently type is null which can't happen in resolveValue(null, " + value + ')');
		if (type.equals(String.class) || type.equals(Object.class))
			return value.get();
		if (type.equals(Boolean.class) || type.equals(boolean.class))
			return Boolean.valueOf(value.get());
		if (type.equals(Short.class) || type.equals(short.class))
			return Short.valueOf(value.get());
		if (type.equals(Integer.class) || type.equals(int.class))
			return Integer.valueOf(value.get());
		if (type.equals(Long.class) || type.equals(long.class))
			return Long.valueOf(value.get());
		if (type.equals(Byte.class) || type.equals(byte.class))
			return Byte.valueOf(value.get());
		if (type.equals(Float.class) || type.equals(float.class))
			return Float.valueOf(value.get());
		if (type.equals(Double.class) || type.equals(double.class))
			return Double.valueOf(value.get());

		if (Enum.class.isAssignableFrom(type))
			try {
				return type.cast(type.getMethod("valueOf", String.class).invoke(null, value.get()));
			} catch (final SecurityException | IllegalArgumentException | ClassCastException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
				throw new IllegalArgumentException("Can not resolve '" + value + "' to " + type.getCanonicalName(), e);
			}

        throw new IllegalArgumentException("Can not resolve '" + value + "' to " + type.getCanonicalName());
	}

	/**
	 * Resolves array attribute value to an instance of specified array value class.
	 *
	 * @param valueClass     class of the resulting value instance
	 * @param attributeValue array attribute value specifying the configuration of the resulting instance
	 * @param callBefore     annotations, methods annotated with those will be called prior to the configuration
	 * @param callAfter      annotations, methods annotated with those will be called after the configuration
	 * @return an array instance of the specified value class which is configured according to the specified array attribute value.
	 */
	private Object resolveArrayValue(final Class<?> valueClass, final ArrayValue attributeValue, final Class<? extends Annotation>[] callBefore, final Class<? extends Annotation>[] callAfter, final boolean configureAllFields, final Environment environment) throws InstantiationException, IllegalAccessException {

		Gson gson = new Gson();

		if (valueClass.equals(Object.class))
			return attributeValue.getRaw();
		if (valueClass.equals(String.class))
			return (gson.toJsonTree(attributeValue.getRaw()).getAsJsonArray());

		final Object resolvedValue = Array.newInstance(valueClass.getComponentType(), attributeValue.get().size());
		for (int i = 0; i < attributeValue.get().size(); ++i)
			Array.set(resolvedValue, i, resolveValue(valueClass.getComponentType(), attributeValue.get().get(i), callBefore, callAfter, configureAllFields, environment));

		return resolvedValue;
	}

	/**
	 * Resolves composite attribute value to an instance of specified value class.
	 *
	 * @param valueClass     class of the resulting value instance
	 * @param attributeValue composite attribute value specifying the configuration of the resulting instance
	 * @param callBefore     annotations, methods annotated with those will be called prior to the configuration
	 * @param callAfter      annotations, methods annotated with those will be called after the configuration
	 * @return an instance of the specified value class which is configured according to the specified composite attribute value.
	 */
	private Object resolveCompositeValue(final Class<?> valueClass, final CompositeValue attributeValue, final Class<? extends Annotation>[] callBefore, final Class<? extends Annotation>[] callAfter, final boolean configureAllFields) throws InstantiationException, IllegalAccessException {
		Gson gson = new Gson();

		if (valueClass.equals(Object.class))
			return attributeValue.getRaw();
		if (valueClass.equals(String.class)) {
			String jsonString = gson.toJson(attributeValue.getRaw());
			return gson.fromJson(jsonString, JsonObject.class);
		}

		boolean configureAllFieldsNested = configureAllFields;
		if (valueClass.isAnnotationPresent(ConfigureMe.class)){
			configureAllFieldsNested = valueClass.getAnnotation(ConfigureMe.class).allfields();
		}

		final Object resolvedValue = valueClass.newInstance();
		configure(attributeValue.get(), resolvedValue, callBefore, callAfter, configureAllFieldsNested, defaultEnvironment);
		return resolvedValue;
	}

	/**
	 * Get cached object in order to handle situation with loop
	 *
	 * @param name        name of the config
	 * @param environment environment
	 * @return instance of the already configures object
	 */

	private Object getCachedObject(final String name, final Environment environment) {
		Map<String, Map<Environment, Object>> globalCache = localCache.get();
		if (globalCache == null) {
			globalCache = new HashMap<>();
			localCache.set(globalCache);
		}
		Map<Environment, Object> environmentCache = globalCache.get(name);
		if (environmentCache == null) {
			environmentCache = new HashMap<>();
			globalCache.put(name, environmentCache);
		}

		return environmentCache.get(environment);
	}

	/**
	 * Put configured object to the cache
	 *
	 * @param name        name of the config
	 * @param environment environment
	 * @param o           object to cache
	 */
	private void setCachedObject(final String name, final Environment environment, final Object o) {
		Map<String, Map<Environment, Object>> globalCache = localCache.get();
		if (globalCache == null) {
			globalCache = new HashMap<>();
			localCache.set(globalCache);
		}
		Map<Environment, Object> environmentCache = globalCache.get(name);
		if (environmentCache == null) {
			environmentCache = new HashMap<>();
			globalCache.put(name, environmentCache);
		}
		environmentCache.put(environment, o);
	}

	/**
	 * Used to shutdown the confirmation manager in a reloadable environment like tomcat or any other web container.
	 * If you want to ensure cleanup on application stop, call ConfigurationManager.INSTANCE.shutdown();
	 */
	public void shutdown(){
		ConfigurationSourceRegistry.INSTANCE.shutdown();
	}
}
