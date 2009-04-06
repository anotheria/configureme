package org.configureme;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import net.anotheria.util.StringUtils;

import org.apache.log4j.Logger;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.Set;
import org.configureme.parser.ConfigurationParser;
import org.configureme.parser.ConfigurationParserException;
import org.configureme.parser.ParsedArtefact;
import org.configureme.parser.ParsedAttribute;
import org.configureme.parser.json.JsonParser;
import org.configureme.repository.Artefact;
import org.configureme.repository.ConfigurationRepository;
import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.ConfigurationSourceRegistry;
import org.configureme.sources.ConfigurationSourceKey.Format;
import org.configureme.sources.ConfigurationSourceKey.Type;

public enum ConfigurationManager {
	
	INSTANCE;
	
	private static Environment defaultEnvironment = GlobalEnvironment.INSTANCE;
	
	private static final Logger log = Logger.getLogger(ConfigurationManager.class);
	
	/**
	 * Returns true if the object is properly annotated and can be configured by the configuration manager
	 * @param o
	 * @return
	 */
	public boolean isConfigurable(Object o){
		
		Class<?> clazz = o.getClass();
		return clazz.isAnnotationPresent(ConfigureMe.class);
	}
	
	public void configure(Object o){
		configure(o, defaultEnvironment);
	}
	
	public void configure(Object o, Environment in){
		
		if (!isConfigurable(o))
			throw new IllegalArgumentException("Class "+o.getClass()+" is not annotated as ConfigureMe, called with: "+o);
		
		Class<?> clazz = o.getClass();
		//System.out.println("Starting configuring "+o);
		
		
		String artefactName = "";
		ConfigureMe ann = clazz.getAnnotation(ConfigureMe.class);
		if (ann.name()==null || ann.name().length()==0)
			artefactName = extractConfigurationNameFromClassName(clazz);
		else
			artefactName = ann.name(); 
		//System.out.println("Configuring with name: "+artefactName);
		
		Configuration config = getConfiguration(artefactName, in);
		//System.out.println("Configuring with config: "+config);
		
//		if (component instanceof ConfigurationAware)
//			((ConfigurationAware)component).configurationStarted();

		//first set fields
		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields){
			if (f.isAnnotationPresent(Configure.class)){
				String attributeName = f.getName();
				String attributeValue = config.getAttribute(attributeName);
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
		
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods){
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


		//if (component instanceof ConfigurationAware)
		//	((ConfigurationAware)component).configurationFinished();
		if (log.isDebugEnabled()){
			log.debug("Finished configuration of "+o+" as "+artefactName);
		}
	}
	
	public Configuration getConfiguration(String artefactName){
		return getConfiguration(artefactName, GlobalEnvironment.INSTANCE);
	}
	
	public Configuration getConfiguration(String configurationName, Environment in){
		
		//for the first we will hardcode file as config source and json as config format.
		ConfigurationSourceKey configSourceKey = new ConfigurationSourceKey();
		configSourceKey.setFormat(Format.JSON);
		configSourceKey.setType(Type.FILE);
		configSourceKey.setName(configurationName);
		
		if (!ConfigurationRepository.INSTANCE.hasConfiguration(configurationName)){
			if (!ConfigurationSourceRegistry.INSTANCE.isConfigurationAvailable(configSourceKey)){
				throw new IllegalArgumentException("No such configuration: "+configurationName+" ("+configSourceKey+")");
			}
			//reading config
			String content = ConfigurationSourceRegistry.INSTANCE.readConfigurationSource(configSourceKey);

			content = StringUtils.removeCComments(content);
			content = StringUtils.removeCPPComments(content);
			ConfigurationParser parser = new JsonParser();
			ParsedArtefact pa = null;
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
	
	public static final void setDefaultEnvironment(Environment anEnvironment){
		defaultEnvironment = anEnvironment;
	}
	
	public static final Environment getDefaultEnvironment(){
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
