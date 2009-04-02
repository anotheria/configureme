package org.configureme.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.configureme.Environment;
import org.configureme.GlobalEnvironment;


public class Artefact {
	private String name;
	
	private Map<String, Attribute> attributes;
	
	Artefact(String aName){
		name = aName;
		attributes = new ConcurrentHashMap<String, Attribute>();
	}
	
	public Attribute getAttribute(String name){
		Attribute a = attributes.get(name);
		if (a==null)
			throw new IllegalArgumentException("Attribute "+name+" doesn't exists");
		return a;
	}
	
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
	
	public String toString(){
		return getName()+": "+attributes;
	}
	
	public String getName(){
		return name;
	}
	
	public List<String> getAttributeNames(){
		ArrayList<String> names = new ArrayList<String>();
		names.addAll(attributes.keySet());
		return names;
	}
}
