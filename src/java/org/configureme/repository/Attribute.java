package org.configureme.repository;

import org.apache.log4j.Logger;
import org.configureme.Environment;
import org.configureme.GlobalEnvironment;


public class Attribute {
	
	private static Logger log = Logger.getLogger(Attribute.class);
	
	private String name;
	
	private AttributeValue attributeValue;
	
	public Attribute(String aName){
		name = aName;
		attributeValue = new AttributeValue();
	}
	
	public String getValue(){
		return getValue(GlobalEnvironment.INSTANCE);
	}

	public String getValue(Environment in){
		log.debug("looking up value for "+name+" in "+in);
		return attributeValue.get(in);
	}
	
	public String getName(){
		return name;
	}
	
	public void addValue(String value, Environment in){
		attributeValue.set(value, in);
	}
	
	public String toString(){
		return getName()+"="+attributeValue;
	}
}
