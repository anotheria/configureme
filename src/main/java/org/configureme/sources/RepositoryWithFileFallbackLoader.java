package org.configureme.sources;

/**
 * This loader tries to load files from the repository, and falls back to file system if not found in repository.
 *
 * @author lrosenberg
 * @since 14.05.21 23:04
 */
public class RepositoryWithFileFallbackLoader implements SourceLoader{
	FileLoader fallbackLoader = new FileLoader();
	@Override
	public boolean isAvailable(ConfigurationSourceKey key) {
		System.out.println("RWFFL: is available called "+key);
		return fallbackLoader.isAvailable(key.toType(ConfigurationSourceKey.Type.FILE));
	}

	@Override
	public long getLastChangeTimestamp(ConfigurationSourceKey key) {
		System.out.println("RWFFL: getLastChangeTimestamp "+key);
		return fallbackLoader.getLastChangeTimestamp(key.toType(ConfigurationSourceKey.Type.FILE));
		
	}

	@Override
	public String getContent(ConfigurationSourceKey key) {
		System.out.println("RWFFL: getContent "+key);
		return fallbackLoader.getContent(key.toType(ConfigurationSourceKey.Type.FILE));

	}
}
