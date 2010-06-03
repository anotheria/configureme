package org.configureme.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.configureme.Environment;
import org.configureme.GlobalEnvironment;

/**
 * The artefact is the internal representation of a configuration. 
 * @author lrosenberg
 */
public class Artefact {
	/**
	 * The name of the configuration and therefore the name of the artefact.
	 */
	private String name;
	/**
	 * The attribute map
	 */
	private Map<String, Attribute> attributes;
	
	/**
	 * Creates a new Artefact with the given name
	 * @param aName the name of the artefact
	 */
	Artefact(String aName){
		name = aName;
		attributes = new ConcurrentHashMap<String, Attribute>();
	}
	
	/**
	 * Returns the attribute with the given name. Throws an IllegalArgumentException if there is no such attribute.
	 * @param attributeName
	 * @return
	 */
	public Attribute getAttribute(String attributeName){
		Attribute a = attributes.get(attributeName);
		if (a==null)
			throw new IllegalArgumentException("Attribute "+attributeName+" doesn't exists");
		return a;
	}
	
	/**
	 * Adds an attribute value. If the attribute doesn't exist it will be created.
	 * @param attributeName the name of the attribute.
	 * @param attributeValue the value of the attribute in the given environment.
	 * @param in the environment in which the attribute value applies
	 */
	public void addAttributeValue(String attributeName, String attributeValue, Environment in){
		 if (in==null)
			 in = GlobalEnvironment.INSTANCE;
		 Attribute attr = attributes.get(attributeName);
		 if (attr==null){
			 attr = new Attribute(attributeName);
			 attributes.put(attr.getName(), attr);
		 }
		 attr.addValue(attributeValue, in);
	}
	
	@Override public String toString(){
		return getName()+": "+attributes;
	}
	
	/**
	 * Returns the name of the artefact
	 * @return
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Returns names of the contained attributes
	 * @return
	 */
	public List<String> getAttributeNames(){
		ArrayList<String> names = new ArrayList<String>();
		names.addAll(attributes.keySet());
		return names;
	}
}
