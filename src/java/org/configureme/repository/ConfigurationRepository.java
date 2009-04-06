package org.configureme.repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.configureme.Configuration;
import org.configureme.Environment;
import org.configureme.GlobalEnvironment;


public enum ConfigurationRepository {
	INSTANCE;
	
	private Map<String, Artefact> artefacts = new ConcurrentHashMap<String, Artefact>();
	
	public Artefact createArtefact(String name){
		Artefact old = artefacts.get(name);
		if (old!=null)
			throw new IllegalArgumentException("Artefact already exists.");
		
		Artefact a = new Artefact(name);
		artefacts.put(a.getName(), a);
		return a;
	}
	
	public Artefact getArtefact(String name){
		return artefacts.get(name);
	}
	
	public boolean hasConfiguration(String name){
		return getArtefact(name) != null;
	}
	
	public Configuration getConfiguration(String name, Environment environment){
		if (environment==null)
			environment = GlobalEnvironment.INSTANCE;
		Artefact a = getArtefact(name);
		if (a==null)
			throw new IllegalArgumentException("No such artefact: "+name);
		ConfigurationImpl configurationImpl = new ConfigurationImpl(a.getName());
		List<String> attributeNames = a.getAttributeNames();
		for (String attributeName : attributeNames){
			configurationImpl.setAttribute(attributeName, a.getAttribute(attributeName).getValue(environment));
		}
		return configurationImpl;
	}
}
