package org.configureme;

import java.util.Collection;
import java.util.Set;
import java.util.Map.Entry;

/**
 * This class represents a snapshot of a ConfigurationSource at once moment of time in one environment. It can be used for old style explicit configuration or debugging. 
 * @author another
 */
public interface Configuration {
	/**
	 * The name of the configuration. Will be the same stated in getConfiguration() call to ConfigurationManager.
	 * @return
	 */
	public String getName();
	
	/**
	 * Return the string value of an attribute.
	 * @param attributeName the name of the attribute.
	 * @return attributeName the name of the attribute.
	 */
	public String getAttribute(String attributeName);
	
	/**
	 * Returns the names of all contained attributes. 
	 * @return the names of all contained attributes.
	 */
	public Collection<String> getAttributeNames();

	/**
	 * Returns the set of entries of the underlying map. Used by @SetAll annotation.
	 * @return
	 */
	public Set<Entry<String,String>> getEntries();
}
