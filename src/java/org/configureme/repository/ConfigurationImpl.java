package org.configureme.repository;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.configureme.Configuration;

public class ConfigurationImpl implements Configuration{
	private String name;
	private Map<String,String> attributes;
	
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
	
	public Set<Entry<String,String>> getEntries(){
		return attributes.entrySet();
	}

	@Override
	public String getName() {
		return name;
	}
	
	public void setAttribute(String attributeName, String attributeValue){
		attributes.put(attributeName, attributeValue);
	}
	
	public String toString(){
		return getName()+": "+attributes;
	}

	public boolean equals(Object o){
		return o instanceof ConfigurationImpl &&
		 name.equals(((ConfigurationImpl)o).name) && 
		 attributes.equals(((ConfigurationImpl)o).attributes);
	}

}
