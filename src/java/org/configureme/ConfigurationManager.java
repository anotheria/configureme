package org.configureme;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.configureme.annotations.AfterConfiguration;
import org.configureme.annotations.AfterInitialConfiguration;
import org.configureme.annotations.AfterReConfiguration;
import org.configureme.annotations.BeforeConfiguration;
import org.configureme.annotations.BeforeInitialConfiguration;
import org.configureme.annotations.BeforeReConfiguration;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.DontConfigure;
import org.configureme.annotations.Set;
import org.configureme.annotations.SetAll;
import org.configureme.annotations.SetIf;
import org.configureme.environments.DynamicEnvironment;
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
import org.configureme.repository.PlainValue;
import org.configureme.repository.Value;
import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.ConfigurationSourceKey.Format;
import org.configureme.sources.ConfigurationSourceKey.Type;
import org.configureme.sources.ConfigurationSourceRegistry;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Configuration manager (this is the one YOU must use) is a utility class for retrieval of configurations and automatical configurations of components.
 * Configured components are 'watched', any changes in the configuration source (file) lead to a reconfiguration.
 * The configuration manager also supports retrieval of the configurations in different environments. Its usually a good idea to specify a <b>defaultEnvironment</b>
 * by <code>-Dconfigureme.defaultEnvironment=a_b_c</code>...
 * @author lrosenberg
 *
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
	@SuppressWarnings("unchecked")
	private static final java.util.Set<Class<?>> PLAIN_TYPES = new HashSet<Class<?>>(
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
	private ConfigurationSourceKey.Format defaultConfigurationSourceFormat = Format.JSON;

	/**
	 * A map which contains configuration parser for different formats.
	 */
	private ConcurrentHashMap<ConfigurationSourceKey.Format, ConfigurationParser> parsers;

	/**
	 * Annotations to call before initial configuration.
	 */
	@SuppressWarnings("unchecked") private static final Class<? extends Annotation>[] CALL_BEFORE_INITIAL_CONFIGURATION = (Class<? extends Annotation>[]) new Class<?>[]{
		BeforeInitialConfiguration.class, BeforeConfiguration.class
	};

	/**
	 * Annotations to call after initial configuration.
	 */
	@SuppressWarnings("unchecked") private static final Class<? extends Annotation>[] CALL_AFTER_INITIAL_CONFIGURATION = (Class<? extends Annotation>[]) new Class<?>[]{
		AfterConfiguration.class, AfterInitialConfiguration.class
	};

	/**
	 * Annotations to call before reconfiguration.
	 */
	@SuppressWarnings("unchecked") private static final Class<? extends Annotation>[] CALL_BEFORE_RE_CONFIGURATION = (Class<? extends Annotation>[]) new Class<?>[]{
		BeforeReConfiguration.class, BeforeConfiguration.class
	};

	/**
	 * Annotations to call before after reconfiguration.
	 */
	@SuppressWarnings("unchecked") private static final Class<? extends Annotation>[] CALL_AFTER_RE_CONFIGURATION = (Class<? extends Annotation>[]) new Class<?>[]{
		AfterConfiguration.class, AfterReConfiguration.class
	};

	/**
	 * Property name for the system property which ConfigurationManager checks to set its defaultEnvironment with at startup.
	 */
	public static final String PROP_NAME_DEFAULT_ENVIRONMENT = "configureme.defaultEnvironment";


	/**
	 * Logger.
	 */
	private static final Logger log = Logger.getLogger(ConfigurationManager.class);

	/**
	 * Initializes the one and only instance of the ConfigurationManager.
	 */
	private ConfigurationManager(){
		String defEnvironmentAsString = System.getProperty(PROP_NAME_DEFAULT_ENVIRONMENT, "");
		defaultEnvironment = DynamicEnvironment.parse(defEnvironmentAsString);

		parsers = new ConcurrentHashMap<Format, ConfigurationParser>();
		parsers.put(Format.JSON, new JsonParser());
		parsers.put(Format.PROPERTIES, new PropertiesParser());
	}

	/**
	 * Returns true if the object is properly annotated and can be configured by the configuration manager. Calling configure with an Object o as parameter, where isConfigurable(o) will result in an
	 * Error.
	 * @param o object to check
	 * @return true if object is properly and can be configured
	 */
	public boolean isConfigurable(Object o){

		Class<?> clazz = o.getClass();
		return clazz.isAnnotationPresent(ConfigureMe.class);
	}

	/**
	 * Configures a configurable component in the default environment. The object must be annotated with ConfigureMe and the configuration source must be present.
	 * @param o object to configure
	 */
	public void configure(Object o){
		configure(o, defaultEnvironment);
	}

	/**
	 * Configures a configurable component in the default environment. The object must be annotated with ConfigureMe and the configuration source must be present.
	 * @param o object to configure
	 */
	public void configure(Object o, Format format){
		configure(o, defaultEnvironment, format);
	}

	/**
	 * Configures a configurable component in the default environment. The object must be annotated with ConfigureMe and the configuration source must be present.
	 * @param o object to configure
	 * @param name configuration name
	 */
	public void configureAs(Object o, String name){
		configureAs(o, defaultEnvironment, name, defaultConfigurationSourceFormat);
	}

	/**
	 * Configures java bean in the default environment.
	 * @param o object to configure
	 * @param name configuration name
	 */
	public void configureBeanAs(Object o, String name){
		configurePojoAs(o, name);
	}

	/**
	 * Configures java bean in the given environment.
	 * @param o object to configure
	 * @param name configuration name
	 * @param in environment
	 */
	public void configureBeanAsIn(Object o, String name, final Environment in){
		configurePojoAsIn(o, name, in);
	}

	/**
	 * Configures pojo object in the default environment.
	 * @param o object to configure
	 * @param name configuration name
	 */
	public void configurePojoAs(final Object o, final String name){
		Environment in = defaultEnvironment;
		configurePojoAsIn(o, name, in);
	}

	/**
	 * Configures pojo object in the given environment.
	 * @param o object to configure
	 * @param name configuration name
	 * @param in environment
	 */
	public void configurePojoAsIn(final Object o, final String name, final Environment in){
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

		ConfigurationSourceKey configSourceKey = new ConfigurationSourceKey();
		configSourceKey.setFormat(Format.JSON);
		configSourceKey.setType(ann.type());
		configSourceKey.setName(name);

		configureInitially(configSourceKey, o, in, ann);
	}

	/**
	 * Configures a configurable component in the given environment. The object must be annotated with ConfigureMe and the configuration must be present.
	 * @param o object to configure.
	 * @param in the environment for the configuration.
	 * @param configurationName name of the configuration.
	 */
	public void configureAs(Object o, Environment in, String configurationName, Format format){
		if (!isConfigurable(o))
			throw new IllegalArgumentException("Class "+o.getClass()+" is not annotated as ConfigureMe, called with: "+o+", class: "+o.getClass());

		Class<?> clazz = o.getClass();
		ConfigureMe ann = clazz.getAnnotation(ConfigureMe.class);

		ConfigurationSourceKey configSourceKey = new ConfigurationSourceKey();
		configSourceKey.setFormat(format);
		configSourceKey.setType(ann.type());
		configSourceKey.setName(configurationName);

		configureAs(o, in, configSourceKey);
	}

	/**
	 * Configures a configurable component in the given environment. The object must be annotated with ConfigureMe and the configuration must be present.
	 * @param o object to configure.
	 * @param configSourceKey source definition.
	 */
	public void configureAs(Object o, Environment in, ConfigurationSourceKey configSourceKey){
		if (!isConfigurable(o))
			throw new IllegalArgumentException("Class "+o.getClass()+" is not annotated as ConfigureMe, called with: "+o+", class: "+o.getClass());

		Class<?> clazz = o.getClass();
		ConfigureMe ann = clazz.getAnnotation(ConfigureMe.class);

		configureInitially(configSourceKey, o, in, ann);
	}

	/**
	 * Configure object in the given environment.
	 * @param o object to configure
	 * @param in environment
	 */
	public void configure(Object o, Environment in){
		configure(o, in, defaultConfigurationSourceFormat);
	}

	/**
	 * Configures a configurable component in the givent environment. The object must be annotated with ConfigureMe and the configuration must be present.
	 * @param o object to configure
	 * @param in the environment for the configuration
	 */
	public void configure(Object o, Environment in, Format format){

		if (!isConfigurable(o))
			throw new IllegalArgumentException("Class "+o.getClass()+" is not annotated as ConfigureMe, called with: "+o+", class: "+o.getClass());

		Class<?> clazz = o.getClass();

		String configurationName = "";
		ConfigureMe ann = clazz.getAnnotation(ConfigureMe.class);
		if (ann.name()==null || ann.name().length()==0)
			configurationName = extractConfigurationNameFromClassName(clazz);
		else
			configurationName = ann.name();

		configureAs(o, in, configurationName, format);
	}

	/**
	 * Internal method used at initial, user triggered configuration.
	 * @param key the source key
	 * @param o the object to configure
	 * @param in the environment
	 * @param ann the configureme annotation instance with which o.getClass() was annotated.
	 */
	private void configureInitially(ConfigurationSourceKey key, Object o, Environment in, ConfigureMe ann){

		configure(key, o, in, CALL_BEFORE_INITIAL_CONFIGURATION, CALL_AFTER_INITIAL_CONFIGURATION, ann);

		if (ann.watch()){
			ConfigurableWrapper wrapper = new ConfigurableWrapper(key, o, in);
			ConfigurationSourceRegistry.INSTANCE.addWatchedConfigurable(wrapper);
		}

	}

	/**
	 * This method is used internally for calls to annotations at the start and the end of each configuration.
	 * @param configurable
	 * @param methods
	 * @param annotationClasses
	 */
	private void callAnnotations(Object configurable, Method[] methods, Class<? extends Annotation>[] annotationClasses){
		//check for annotations to call and call 'before' annotations
		for (Method m : methods){
			//System.out.println("Checking methid "+m);
			for (Class<? extends Annotation> anAnnotationClass : annotationClasses){
				//System.out.println("\tChecking annotation "+anAnnotationClass);
				Annotation anAnnotation = m.getAnnotation(anAnnotationClass);
				//System.out.println("\t\t-->"+anAnnotation);
				if (anAnnotation!=null){
					try {
						m.invoke(configurable);
					} catch (IllegalAccessException e) {
						log.error("callAnnotations("+Arrays.toString(methods)+", "+Arrays.toString(annotationClasses)+")", e);
						throw new AssertionError("Error declaration in method "+m+", wrong declaration (public void "+m.getName()+" expected)? - "+e.getMessage());
					} catch (InvocationTargetException e) {
						log.error("callAnnotations(Exception in annotated method: "+m+")", e);
						throw new RuntimeException("Exception in annotated method: "+e.getMessage(), e);
					}
				}

			}
		}
	}

	/**
	 * This method executes the configuration.
	 * @param key the key for the configuration source
	 * @param o the object to configure
	 * @param in environment in which the object runs
	 * @param callBefore annotations, methods annotated with those will be called prior to the configuration
	 * @param callAfter annotations, methods annotated with those will be called after the configuration
	 */
	private void configure(ConfigurationSourceKey key, Object o, Environment in, Class<? extends Annotation>[] callBefore,  Class<? extends Annotation>[] callAfter, ConfigureMe ann){
		//System.out.println("CALLED configure("+key+", "+o+","+in+")");
		Class<?> clazz = o.getClass();

		if (ann==null)
			ann = clazz.getAnnotation(ConfigureMe.class);
		if (ann==null)
			throw new AssertionError("An unannotated class shouldn't make it sofar, obj: "+o+" class "+o.getClass());

		boolean configureAllFields = ann.allfields();

		configure(getConfiguration(key, in), o, callBefore, callAfter, configureAllFields);

		if (log!=null && log.isDebugEnabled()){
			log.debug("Finished configuration of "+o+" as "+key);
		}
	}

	/**
	 * Applies specified configuration to specified object.
	 *
	 * @param config the configuration to be applied
	 * @param o the object to be configured
	 * @param callBefore annotations, methods annotated with those will be called prior to the configuration
	 * @param callAfter annotations, methods annotated with those will be called after the configuration
	 * @param configureAllFields specifies whether to set all fields regardless if they are marked configured or not
	 */
	private void configure(Configuration config, Object o, Class<? extends Annotation>[] callBefore,  Class<? extends Annotation>[] callAfter, boolean configureAllFields) {
		Class<?> clazz = o.getClass();
		Method[] methods = clazz.getDeclaredMethods();
		callAnnotations(o, methods, callBefore);

		//first set fields
		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields){
			if (f.isAnnotationPresent(Configure.class) || (configureAllFields && !f.isAnnotationPresent(DontConfigure.class))){
				String attributeName = f.getName();
				Value attributeValue = config.getAttribute(attributeName);
				if (attributeValue==null)
					continue;
				if (Modifier.isPublic(f.getModifiers()) ){
					try{
						f.set(o, resolveValue(f.getType(), attributeValue, callBefore, callAfter, configureAllFields));
					}catch(Exception e){
						log.warn(f+".set("+o+", "+attributeValue+")", e);
					}
				}else{
					String methodName = "set"+f.getName().toUpperCase().charAt(0)+f.getName().substring(1);
					try{
						Method toSet = clazz.getMethod(methodName, f.getType());
						toSet.invoke(o, resolveValue(f.getType(), attributeValue, callBefore, callAfter, configureAllFields));
					}catch(NoSuchMethodException e){
						log.error("can't find method "+methodName+" ("+f.getType()+")");
					}catch(Exception e){
						log.error("can't set "+attributeName+" to "+attributeValue+", because: ", e);
					}
				}
			}
		}
		//end set fields

		for (Method method : methods){
			if (method.isAnnotationPresent(SetAll.class)){
				Collection<Entry<String,Value>> entries = config.getEntries();
				log.debug("Calling method "+method+" with "+entries);
				for (Entry<String,Value> entry : entries){
					try{
						method.invoke(o, entry.getKey(), resolveValue(method.getParameterTypes()[1], entry.getValue(), callBefore, callAfter, configureAllFields));
					}catch(Exception e){
						log.warn(method.getName()+"invoke("+o+", "+entry.getKey()+", "+entry.getValue()+")", e);
					}
				}
			}
			if (method.isAnnotationPresent(SetIf.class)){
				Collection<Entry<String,Value>> entries = config.getEntries();
				SetIf setIfAnnotation = method.getAnnotation(SetIf.class);
				for (Entry<String,Value> entry : entries){
					if (SetIf.ConditionChecker.satisfyCondition(setIfAnnotation, entry.getKey())){
						log.debug("Calling method "+method+" with parameters : \""+entry.getKey()+"\", \""+entry.getValue()+"\"");
						try{
							method.invoke(o, entry.getKey(), resolveValue(method.getParameterTypes()[1], entry.getValue(), callBefore, callAfter, configureAllFields));
						}catch(Exception e){
							log.warn(method.getName()+"invoke("+o+", "+entry.getKey()+", "+entry.getValue()+")", e);
						}
					}
				}
			}
			if (method.isAnnotationPresent(Set.class)){
				log.debug("method "+method+" is annotated");
				Set setAnnotation = method.getAnnotation(Set.class);
				String attributeName = setAnnotation.value();
				Value attributeValue = config.getAttribute(attributeName);
				if (attributeValue!=null){
					log.debug("setting "+method.getName()+" to "+attributeValue+" configured by "+attributeName);
					try{
						method.invoke(o, resolveValue(method.getParameterTypes()[0], attributeValue, callBefore, callAfter, configureAllFields));
					}catch(Exception e){
						log.warn(method.getName()+"invoke("+o+", "+attributeValue+")", e);
					}
				}

			}
		}

		callAnnotations(o, methods, callAfter);
	}

	/**
	 * Called by ConfigurationSource monitors/listeners to trigger a reconfiguration of a component.
	 * @param key
	 * @param o
	 * @param in
	 */
	void reconfigure(ConfigurationSourceKey key, Object o, Environment in){
		configure(key, o, in, CALL_BEFORE_RE_CONFIGURATION, CALL_AFTER_RE_CONFIGURATION, null);
	}


	/**
	 * Returns a configuration snapshot for this configurationname in the global environment. Snapshot means that only the part of the
	 * configuration which is valid now and only for global environment is returned.
	 * @param configurationName the name of the configuration to check
	 * @return a configuration snapshot for this configurationname in the global environment
	 */
	public Configuration getConfiguration(String configurationName){
		return getConfiguration(configurationName, defaultEnvironment);
	}

	/**
	 * Returns a configuration snapshot for this configurationname in the given environment. Snapshot means that only the part of the
	 * configuration which is valid now and only for the given environment is returned.
	 * defaultConfigurationSourceFormat and defaultConfigurationSourceType are used for format and type. At the moment its JSON and File.
	 * @param configurationName the name of the configuration source.
	 * @param in the environment
	 * @return a configuration snapshot for this configurationname in the given environment
	 */
	public Configuration getConfiguration(String configurationName, Environment in){
		ConfigurationSourceKey configSourceKey = new ConfigurationSourceKey();
		configSourceKey.setFormat(defaultConfigurationSourceFormat);
		configSourceKey.setType(defaultConfigurationSourceType);
		configSourceKey.setName(configurationName);

		return getConfiguration(configSourceKey, in);
	}

	/**
	 * Internal method for configuration retrieval.
	 * @param configSourceKey
	 * @param in
	 * @return
	 */
	private Configuration getConfiguration(ConfigurationSourceKey configSourceKey, Environment in){

		//for the first we will hardcode file as config source and json as config format.
		String configurationName = configSourceKey.getName();
		if (!ConfigurationRepository.INSTANCE.hasConfiguration(configurationName)){
			if (!ConfigurationSourceRegistry.INSTANCE.isConfigurationAvailable(configSourceKey)){
				throw new IllegalArgumentException("No such configuration: "+configurationName+" ("+configSourceKey+")");
			}
			//reading config
			String content = ConfigurationSourceRegistry.INSTANCE.readConfigurationSource(configSourceKey);

			ConfigurationParser parser = parsers.get(configSourceKey.getFormat());
			if (parser==null)
				throw new IllegalArgumentException("Format "+configSourceKey.getFormat()+" is not supported (yet).");
			ParsedConfiguration pa = null;
			try{
				pa = parser.parseConfiguration(configurationName, content);
			}catch(ConfigurationParserException e){
				log.error("getConfiguration("+configurationName+", "+in+")", e );
				throw new IllegalArgumentException(configSourceKey+" is not parseable: "+e.getMessage(), e);
			}
			//System.out.println("Parsed "+pa);
			List<? extends ParsedAttribute<?>> attributes = pa.getAttributes();
			Artefact art = ConfigurationRepository.INSTANCE.createArtefact(configurationName);
			for (ParsedAttribute<?> a : attributes){
				art.addAttributeValue(a.getName(), a.getValue(), a.getEnvironment());
			}
		}

		Configuration config = null;

		config = ConfigurationRepository.INSTANCE.getConfiguration(configurationName, in);
		return config;
	}

	/**
	 * Sets the default environment. The default environment is used in methods configure(Object) and getConfiguration(String) which have no explicit Environemnt parameter.
	 * @param anEnvironment
	 */
	public final void setDefaultEnvironment(Environment anEnvironment){
		defaultEnvironment = anEnvironment;
	}

	/**
	 * Returns the previously set default Environment. If no environment has been set, either by method call, or by property, GlobalEnvironment.INSTANCE is returned.
	 * @return the previously set default Environment
	 */
	public  final Environment getDefaultEnvironment(){
		return defaultEnvironment;
	}

	/**
	 * Calculates default configuration artefact name for a java class.
	 * @param targetClazz target class
	 * @return default configuration artefact name for a given java class. For MyConfigurable it would be "myconfigurable"
	 */
	private static String extractConfigurationNameFromClassName(Class<?> targetClazz){
		return targetClazz.getName().substring(targetClazz.getName().lastIndexOf('.')+1).toLowerCase();
	}

	/**
	 * Resolves attribute value to an instance of specified value class.
	 * @param valueClass class of the resulting value instance
	 * @param attributeValue array attribute value specifying the configuration of the resulting instance
	 * @param callBefore annotations, methods annotated with those will be called prior to the configuration
	 * @param callAfter annotations, methods annotated with those will be called after the configuration
	 * @return an instance of the specified value class which is configured according to the specified attribute value.
	 */
	private Object resolveValue(Class<?> valueClass, Value attributeValue, Class<? extends Annotation>[] callBefore,  Class<? extends Annotation>[] callAfter, boolean configureAllFields) throws InstantiationException, IllegalAccessException {
		boolean isValueClassPlain = isPlain(valueClass);
		boolean isValueClassDummy = valueClass.equals(Object.class) || valueClass.equals(String.class);

		if (attributeValue instanceof PlainValue && !valueClass.isArray() && (isValueClassPlain || isValueClassDummy))
			return resolvePlainValue(valueClass, (PlainValue) attributeValue);
		if (attributeValue instanceof CompositeValue && !valueClass.isArray() && (!isValueClassPlain || isValueClassDummy))
			return resolveCompositeValue(valueClass, (CompositeValue) attributeValue, callBefore, callAfter, configureAllFields);
		if (attributeValue instanceof ArrayValue && (valueClass.isArray() || isValueClassDummy))
			return resolveArrayValue(valueClass, (ArrayValue) attributeValue, callBefore, callAfter, configureAllFields);

		throw new IllegalArgumentException("Can't resolve attribute value " + attributeValue + " to type: " + valueClass.getCanonicalName());
	}

	/**
	 * Checks whether the class specifies a plain type or array (with arbitrary number of dimensions) of plain types.
	 * @param type the type to be checked
	 * @return true if the type is plain, false otherwise
	 */
	private static boolean isPlain(Class<?> type) {
		return (type.isArray())
				? isPlain(type.getComponentType())
				: PLAIN_TYPES.contains(type) || Enum.class.isAssignableFrom(type);
	}

	private static Object resolvePlainValue(Class<?> type, PlainValue value){
		if (type==null)
			throw new IllegalArgumentException("Checkstyle forced me to do this, apparently type is null which can't happen in resolveValue(null, "+value+")");
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
			} catch (SecurityException e) {
				throw new IllegalArgumentException("Can not resolve '" + value + "' to " + type.getCanonicalName(), e);
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException("Can not resolve '" + value + "' to " + type.getCanonicalName(), e);
			} catch (InvocationTargetException e) {
				throw new IllegalArgumentException("Can not resolve '" + value + "' to " + type.getCanonicalName(), e);
			} catch (NoSuchMethodException e) {
				throw new IllegalArgumentException("Can not resolve '" + value + "' to " + type.getCanonicalName(), e);
			} catch (ClassCastException e) {
				throw new IllegalArgumentException("Can not resolve '" + value + "' to " + type.getCanonicalName(), e);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Can not resolve '" + value + "' to " + type.getCanonicalName(), e);
			}

		throw new IllegalArgumentException("Can not resolve '" + value + "' to " + type.getCanonicalName());
	}

	/**
	 * Resolves array attribute value to an instance of specified array value class.
	 * @param valueClass class of the resulting value instance
	 * @param attributeValue array attribute value specifying the configuration of the resulting instance
	 * @param callBefore annotations, methods annotated with those will be called prior to the configuration
	 * @param callAfter annotations, methods annotated with those will be called after the configuration
	 * @return an array instance of the specified value class which is configured according to the specified array attribute value.
	 */
	private Object resolveArrayValue(Class<?> valueClass, ArrayValue attributeValue, Class<? extends Annotation>[] callBefore,  Class<? extends Annotation>[] callAfter, boolean configureAllFields) throws InstantiationException, IllegalAccessException {
		if (valueClass.equals(Object.class))
			return attributeValue.getRaw();
		if (valueClass.equals(String.class))
			return new JSONArray((Collection<?>) attributeValue.getRaw()).toString();

		Object resolvedValue = Array.newInstance(valueClass.getComponentType(), attributeValue.get().size());
		for (int i = 0; i < attributeValue.get().size(); ++i)
			Array.set(resolvedValue, i, resolveValue(valueClass.getComponentType(), attributeValue.get().get(i), callBefore, callAfter, configureAllFields));

		return resolvedValue;
	}

	/**
	 * Resolves composite attribute value to an instance of specified value class.
	 * @param valueClass class of the resulting value instance
	 * @param attributeValue composite attribute value specifying the configuration of the resulting instance
	 * @param callBefore annotations, methods annotated with those will be called prior to the configuration
	 * @param callAfter annotations, methods annotated with those will be called after the configuration
	 * @return an instance of the specified value class which is configured according to the specified composite attribute value.
	 */
	private Object resolveCompositeValue(Class<?> valueClass, CompositeValue attributeValue, Class<? extends Annotation>[] callBefore,  Class<? extends Annotation>[] callAfter, boolean configureAllFields) throws InstantiationException, IllegalAccessException {
		if (valueClass.equals(Object.class))
			return attributeValue.getRaw();
		if (valueClass.equals(String.class))
			return new JSONObject((Map<?, ?>) attributeValue.getRaw()).toString();

		Object resolvedValue = valueClass.newInstance();
		configure(attributeValue.get(), resolvedValue, callBefore, callAfter, configureAllFields);
		return resolvedValue;
	}
}
