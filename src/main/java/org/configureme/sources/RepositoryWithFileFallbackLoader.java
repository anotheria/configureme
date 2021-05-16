package org.configureme.sources;

import org.configureme.sources.configurationrepository.RestConfigurationRepositorySourceLoader;

/**
 * This loader tries to load files from the repository, and falls back to file system if not found in repository.
 *
 * @author lrosenberg
 * @since 14.05.21 23:04
 */
public class RepositoryWithFileFallbackLoader implements SourceLoader{
	FileLoader fallbackLoader = new FileLoader();
	RestConfigurationRepositorySourceLoader primaryLoader = new RestConfigurationRepositorySourceLoader();

	@Override
	public boolean isAvailable(ConfigurationSourceKey key) {
		System.out.println("RWFFL: is available called "+key);
		boolean primary = primaryLoader.isAvailable(key);
		System.out.println("Primary reply: "+primary);
		return primary ?
				true :
				fallbackLoader.isAvailable(key.toType(ConfigurationSourceKey.Type.FILE));
	}

	@Override
	public long getLastChangeTimestamp(ConfigurationSourceKey key) {
		System.out.println("RWFFL: getLastChangeTimestamp "+key);
		long primary = primaryLoader.getLastChangeTimestamp(key);
		return primary != -1 ?
				primary :
				fallbackLoader.getLastChangeTimestamp(key.toType(ConfigurationSourceKey.Type.FILE));
		
	}

	@Override
	public String getContent(ConfigurationSourceKey key) {
		System.out.println("RWFFL: getContent "+key);
		String content = primaryLoader.getContent(key);
		return content != null ?
				content :
				fallbackLoader.getContent(key.toType(ConfigurationSourceKey.Type.FILE));

	}
}
