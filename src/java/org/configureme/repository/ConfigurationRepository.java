package org.configureme.repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.configureme.Configuration;
import org.configureme.Environment;
import org.configureme.GlobalEnvironment;
import org.configureme.sources.ConfigurationSource;
import org.configureme.sources.ConfigurationSourceListener;

/**
 * The configurationrepository is the internal storage for configurations. It caches all configuration which are ever loaded by the ConfigurationManager.
 * The configuration repository listens to the configuration source registry and removes the cached version from the internal storage to implicitely allow 
 * reloading (which is triggered by another listener).  
 * @author lrosenberg
 */
public enum ConfigurationRepository implements ConfigurationSourceListener{
	/**
	 * The one and only instance of the ConfigurationRepository
	 */
	INSTANCE;
	
	/**
	 * The internal artifact storage.
	 */
	private Map<String, Artefact> artefacts = new ConcurrentHashMap<String, Artefact>();
	
	/**
	 * Creates a new internal artefact for the given name.
	 * @param name the name of the artefact
	 * @return
	 */
	public Artefact createArtefact(String name){
		Artefact old = artefacts.get(name);
		if (old!=null)
			throw new IllegalArgumentException("Artefact '"+name+"' already exists: "+old);
		
		Artefact a = new Artefact(name);
		artefacts.put(a.getName(), a);
		return a;
	}
	
	/**
	 * Returns the artefact from the internal storage.
	 * @param name
	 * @return
	 */
	public Artefact getArtefact(String name){
		return artefacts.get(name);
	}
	
	/**
	 * Returns true if the configuration for the given configuration name is available.
	 * @param name the name of the configuration
	 * @return 
	 */
	public boolean hasConfiguration(String name){
		return getArtefact(name) != null;
	}
	
	/**
	 * Returns a snapshot of the configuration with the given name in the given environment.
	 * @param name the name of the configuration
	 * @param environment the environment of the configuration
	 * @return
	 */
	public Configuration getConfiguration(String name, Environment environment){
		if (environment==null)
			environment = GlobalEnvironment.INSTANCE;
		Artefact a = getArtefact(name);
		if (a==null)
			throw new IllegalArgumentException("No such artefact: "+name);
		ConfigurationImpl configurationImpl = new ConfigurationImpl(a.getName());
		List<String> attributeNames = a.getAttributeNames();
		for (String attributeName : attributeNames){
			String attributeValue = a.getAttribute(attributeName).getValue(environment);
			if (attributeValue!=null)
				configurationImpl.setAttribute(attributeName, attributeValue);
		}
		return configurationImpl;
	}

	@Override
	public void configurationSourceUpdated(ConfigurationSource target) {
		artefacts.remove(target.getKey().getName());
	}
	
	
}
