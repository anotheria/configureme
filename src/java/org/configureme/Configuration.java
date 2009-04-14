package org.configureme;

import java.util.Collection;
import java.util.Set;
import java.util.Map.Entry;

public interface Configuration {
	public String getName();
	
	public String getAttribute(String attributeName);
	
	public Collection<String> getAttributeNames();

	public Set<Entry<String,String>> getEntries();
}
