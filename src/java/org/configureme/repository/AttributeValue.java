package org.configureme.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.configureme.Environment;

/**
 * The internal representation of the attribute value(s).  
 * @author lrosenberg
 */
public class AttributeValue {
	
	private static Logger log = Logger.getLogger(AttributeValue.class);
	
	private Map<String, String> values;
	
	public AttributeValue(){
		values = new ConcurrentHashMap<String, String>();
	}
	
	public String get(Environment in){
		if (log.isDebugEnabled())
			log.debug("looking up in "+in +"("+in.expandedStringForm()+")");
		String retValue = values.get(in.expandedStringForm());
		if (retValue!=null)
			return retValue;
		if (!in.isReduceable())
			return null;
		return get(in.reduce());
	}
	
	public void set(String value, Environment in){
		values.put(in.expandedStringForm(), value);
	}
	
	public String toString(){
		return values.toString();
	}
}
