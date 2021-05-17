package org.configureme.sources;

import org.configureme.sources.configurationrepository.ConfigurationRepositorySourceLoader;

/**
 * This loader tries to load files from the repository, and falls back to file system if not found in repository.
 *
 * @author lrosenberg
 * @since 14.05.21 23:04
 */
public class RepositoryWithFileFallbackLoader extends PrimarySecondarySourceLoader implements SourceLoader{
	public RepositoryWithFileFallbackLoader(){
		super(new ConfigurationRepositorySourceLoader(), new FileLoader());
	}
}
