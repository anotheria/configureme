package org.configureme.sources;

public interface SourceLoader {
	public boolean isAvailable(ConfigurationSourceKey key);
	
	public long getLastChangeTimestamp(ConfigurationSourceKey key);
	
	public String getContent(ConfigurationSourceKey key);
}
