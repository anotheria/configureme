package org.configureme;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.anotheria.util.StringUtils;

import org.apache.log4j.Logger;
import org.configureme.annotations.AfterConfiguration;
import org.configureme.annotations.AfterInitialConfiguration;
import org.configureme.annotations.AfterReConfiguration;
import org.configureme.annotations.BeforeConfiguration;
import org.configureme.annotations.BeforeInitialConfiguration;
import org.configureme.annotations.BeforeReConfiguration;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.Set;
import org.configureme.annotations.SetAll;
import org.configureme.environments.DynamicEnvironment;
import org.configureme.parser.ConfigurationParser;
import org.configureme.parser.ConfigurationParserException;
import org.configureme.parser.ParsedConfiguration;
import org.configureme.parser.ParsedAttribute;
import org.configureme.parser.json.JsonParser;
import org.configureme.repository.Artefact;
import org.configureme.repository.ConfigurationRepository;
import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.ConfigurationSourceRegistry;
import org.configureme.sources.ConfigurationSourceKey.Format;
import org.configureme.sources.ConfigurationSourceKey.Type;

/**
 * Configuration manager is a utility class for retrieval of configurations and automatical configurations of components.
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
	
	private Environment defaultEnvironment = null;
	
	private static final Logger log = Logger.getLogger(ConfigurationManager.class);
	
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
	
	@SuppressWarnings("unchecked") private static Class<? extends Annotation>[] CALL_BEFORE_INITIAL_CONFIGURATION = (Class<? extends Annotation>[]) new Class<?>[]{
		BeforeInitialConfiguration.class, BeforeConfiguration.class
	};
	
	@SuppressWarnings("unchecked") private static Class<? extends Annotation>[] CALL_AFTER_INITIAL_CONFIGURATION = (Class<? extends Annotation>[]) new Class<?>[]{
		AfterConfiguration.class, AfterInitialConfiguration.class
	};

	@SuppressWarnings("unchecked") private static Class<? extends Annotation>[] CALL_BEFORE_RE_CONFIGURATION = (Class<? extends Annotation>[]) new Class<?>[]{
		BeforeReConfiguration.class, BeforeConfiguration.class
	};

	@SuppressWarnings("unchecked") private static Class<? extends Annotation>[] CALL_AFTER_RE_CONFIGURATION = (Class<? extends Annotation>[]) new Class<?>[]{
		AfterConfiguration.class, AfterReConfiguration.class
	};
	
	public static final String PROP_NAME_DEFAULT_ENVIRONMENT = "configureme.defaultEnvironment";
	
	private ConfigurationManager(){
		String defEnvironmentAsString = System.getProperty(PROP_NAME_DEFAULT_ENVIRONMENT, "");
		defaultEnvironment = DynamicEnvironment.parse(defEnvironmentAsString);
		
		parsers = new ConcurrentHashMap<Format, ConfigurationParser>();
		parsers.put(Format.JSON, new JsonParser());
	}
	
	/**
	 * Returns true if the object is properly annotated and can be configured by the configuration manager
	 * @param o
	 * @return
	 */
	public boolean isConfigurable(Object o){
		
		Class<?> clazz = o.getClass();
		return clazz.isAnnotationPresent(ConfigureMe.class);
	}
	
	/**
	 * Configures a configurable component in the default environment. The object must be annotated with ConfigureMe and the configuration must be present.
	 * @param o object to configure
	 */
	public void configure(Object o){
		configure(o, defaultEnvironment);
	}
	
	/**
	 * Configures a configurable component in the givent environment. The object must be annotated with ConfigureMe and the configuration must be present.
	 * @param o object to configure
	 * @param in the environment for the configuration
	 */
	public void configure(Object o, Environment in){
		
		if (!isConfigurable(o))
			throw new IllegalArgumentException("Class "+o.getClass()+" is not annotated as ConfigureMe, called with: "+o);
		
		Class<?> clazz = o.getClass();
		
		String configurationName = "";
		ConfigureMe ann = clazz.getAnnotation(ConfigureMe.class);
		if (ann.name()==null || ann.name().length()==0)
			configurationName = extractConfigurationNameFromClassName(clazz);
		else
			configurationName = ann.name(); 

		ConfigurationSourceKey configSourceKey = new ConfigurationSourceKey();
		configSourceKey.setFormat(Format.JSON);
		configSourceKey.setType(ann.type());
		configSourceKey.setName(configurationName);

		configureInitially(configSourceKey, o, in, ann);
		
	}
	
	private void configureInitially(ConfigurationSourceKey key, Object o, Environment in, ConfigureMe ann){
		
		configure(key, o, in, CALL_BEFORE_INITIAL_CONFIGURATION, CALL_AFTER_INITIAL_CONFIGURATION);
		
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
	private void callAnnotations(Object configurable, Method[] methods, Class<? extends Annotation> annotationClasses[]){
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
						log.error("callAnnotations("+methods+", "+annotationClasses+")", e);
						throw new AssertionError("Error declaration in method "+m+", wrong declaration (public void foo() expected)? - "+e.getMessage());
					} catch (InvocationTargetException e) {
						log.error("callAnnotations("+methods+", "+annotationClasses+")", e);
						throw new AssertionError("Error declaration in method "+m+", wrong declaration (public void foo() expected)? - "+e.getMessage());
					}
				}
					
			}
		}
	}
	
	private void configure(ConfigurationSourceKey key, Object o, Environment in, Class<? extends Annotation> callBefore[],  Class<? extends Annotation> callAfter[] ){
		//System.out.println("CALLED configure("+key+", "+o+","+in+")");
		Configuration config = getConfiguration(key, in);
		
		Class<?> clazz = o.getClass();

		Method[] methods = clazz.getDeclaredMethods();
		callAnnotations(o, methods, callBefore);


		//first set fields
		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields){
			if (f.isAnnotationPresent(Configure.class)){
				String attributeName = f.getName();
				String attributeValue = config.getAttribute(attributeName);
				if (attributeValue==null)
					continue;
				if (Modifier.isPublic(f.getModifiers()) ){
					try{
						f.set(o, resolveValue(f.getType(), attributeValue));
					}catch(Exception e){
						log.warn(f+".set("+o+", "+attributeValue+")", e);
					}
				}else{
					String methodName = "set"+f.getName().toUpperCase().charAt(0)+f.getName().substring(1);
					try{
						Method toSet = clazz.getMethod(methodName, f.getType());
						toSet.invoke(o, resolveValue(f.getType(), attributeValue));
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
				Collection<Entry<String,String>> entries = config.getEntries();
				log.debug("Calling method "+method+" with "+entries);
				for (Entry<String,String> entry : entries){
					try{
						method.invoke(o, entry.getKey(), entry.getValue());
					}catch(Exception e){
						log.warn(method.getName()+"invoke("+o+", "+entry.getKey()+", "+entry.getValue()+")", e);
					}
				}
			}
			if (method.isAnnotationPresent(Set.class)){
				log.debug("method "+method+" is annotated");
				Set setAnnotation = method.getAnnotation(Set.class);
				String attributeName = setAnnotation.value();
				String attributeValue = config.getAttribute(attributeName);
				if (attributeValue!=null){
					log.debug("setting "+method.getName()+" to "+attributeValue+" configured by "+attributeName);
					try{
						method.invoke(o, resolveValue(method.getParameterTypes()[0], attributeValue));
					}catch(Exception e){
						log.warn(method.getName()+"invoke("+o+", "+attributeValue+")", e);
					}
				}
					
			}
		}


		callAnnotations(o, methods, callAfter);

		if (log.isDebugEnabled()){
			log.debug("Finished configuration of "+o+" as "+key);
		}
	}
	
	void reconfigure(ConfigurationSourceKey key, Object o, Environment in){
		//call annotations
		
		configure(key, o, in, CALL_BEFORE_RE_CONFIGURATION, CALL_AFTER_RE_CONFIGURATION);
		
		//call after annotations
	}
	
	public Configuration getConfiguration(String artefactName){
		return getConfiguration(artefactName, defaultEnvironment);
	}
	
	public Configuration getConfiguration(String configurationName, Environment in){
		ConfigurationSourceKey configSourceKey = new ConfigurationSourceKey();
		configSourceKey.setFormat(defaultConfigurationSourceFormat);
		configSourceKey.setType(defaultConfigurationSourceType);
		configSourceKey.setName(configurationName);
		
		return getConfiguration(configSourceKey, in);
	}

	private Configuration getConfiguration(ConfigurationSourceKey configSourceKey, Environment in){
		
		//for the first we will hardcode file as config source and json as config format.
		String configurationName = configSourceKey.getName();
		if (!ConfigurationRepository.INSTANCE.hasConfiguration(configurationName)){
			if (!ConfigurationSourceRegistry.INSTANCE.isConfigurationAvailable(configSourceKey)){
				throw new IllegalArgumentException("No such configuration: "+configurationName+" ("+configSourceKey+")");
			}
			//reading config
			String content = ConfigurationSourceRegistry.INSTANCE.readConfigurationSource(configSourceKey);

			content = StringUtils.removeCComments(content);
			content = StringUtils.removeCPPComments(content);
			ConfigurationParser parser = parsers.get(configSourceKey.getFormat());
			ParsedConfiguration pa = null;
			try{
				pa = parser.parseArtefact(configurationName, content);
			}catch(ConfigurationParserException e){
				log.error("getConfiguration("+configurationName+", "+in+")", e );
				throw new IllegalArgumentException(configSourceKey+" is not parseable: "+e.getMessage());
			}
			//System.out.println("Parsed "+pa);
			List<ParsedAttribute> attributes = pa.getAttributes();
			Artefact art = ConfigurationRepository.INSTANCE.createArtefact(configurationName);
			for (ParsedAttribute a : attributes){
				art.addAttributeValue(a.getName(), a.getValue(), a.getEnvironment());
			}
			
		}
		
		Configuration config = null;
		
		config = ConfigurationRepository.INSTANCE.getConfiguration(configurationName, in);
		return config;
	}
	
	public final void setDefaultEnvironment(Environment anEnvironment){
		defaultEnvironment = anEnvironment;
	}
	
	public  final Environment getDefaultEnvironment(){
		return defaultEnvironment;
	}
	
	private static final String extractConfigurationNameFromClassName(Class<?> targetClazz){
		return targetClazz.getName().substring(targetClazz.getName().lastIndexOf('.')+1).toLowerCase();
	}

	private static final Object resolveValue(Class<?> type, String value){
		if (type.equals(String.class))
			return value;
		if (type.equals(Boolean.class) || type.equals(boolean.class))
			return Boolean.valueOf(value);
		if (type.equals(Short.class) || type.equals(short.class))
			return Short.valueOf(value);
		if (type.equals(Integer.class) || type.equals(int.class))
			return Integer.valueOf(value);
		if (type.equals(Long.class) || type.equals(long.class))
			return Long.valueOf(value);
		if (type.equals(Byte.class) || type.equals(byte.class))
			return Byte.valueOf(value);
		if (type.equals(Float.class) || type.equals(float.class))
			return Float.valueOf(value);
		if (type.equals(Double.class) || type.equals(double.class))
			return Double.valueOf(value);
		throw new IllegalArgumentException("Can't resolve type: "+type+", value: "+value);
	} 
	
	
}
