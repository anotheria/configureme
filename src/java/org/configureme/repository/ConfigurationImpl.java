package org.configureme.repository;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.configureme.Configuration;

/**
 * An implementation of the Configuration. This is a de-facto configuration snapshot of a configuration in a defined environment.
 * @author lrosenberg
 */
public class ConfigurationImpl implements Configuration{
	/**
	 * The name of the configuration
	 */
	private String name;
	/**
	 * The attributes 
	 */
	private Map<String,String> attributes;
	
	/**
	 * Creates a new ConfigurationImpl
	 * @param aName
	 */
	public ConfigurationImpl(String aName){
		name = aName;
		attributes = new ConcurrentHashMap<String, String>();
	}
	
	@Override
	public String getAttribute(String attributeName) {
		return attributes.get(attributeName);
	}
	
	@Override
	public Collection<String> getAttributeNames(){
		return attributes.keySet();
	}
	
	@Override
	public Set<Entry<String,String>> getEntries(){
		return attributes.entrySet();
	}

	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the value of the attribute (in the selected environment).
	 * @param attributeName
	 * @param attributeValue
	 */
	public void setAttribute(String attributeName, String attributeValue){
		attributes.put(attributeName, attributeValue);
	}
	
	@Override public String toString(){
		return getName()+": "+attributes;
	}

	@Override public boolean equals(Object o){
		return o instanceof ConfigurationImpl &&
		 name.equals(((ConfigurationImpl)o).name) && 
		 attributes.equals(((ConfigurationImpl)o).attributes);
	}

}
