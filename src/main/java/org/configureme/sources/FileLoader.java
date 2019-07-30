package org.configureme.sources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.configureme.util.DateUtils;
import org.configureme.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A source loader for files.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public class FileLoader implements SourceLoader{
	
	/**
	 * Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(FileLoader.class);
	
	/**
	 * property key for external configuration path (see: {@link #externalConfigPath}). 
	 */
	private static final String EXTERNAL_CONF_PATH = "org.configureme.configuration-path";

	/**
	 * Returns the file name for the given source key.
	 *
	 * @param source configuration source key
	 * @return the file name for the given source key
	 */
	public static String getFileName(final ConfigurationSourceKey source){
		//ensure an exception is thrown if we are not file.
		if (source.getType()!=ConfigurationSourceKey.Type.FILE)
			throw new AssertionError("Can only load configuration sources with type "+ConfigurationSourceKey.Type.FILE);
		return source.getName()+ '.' +source.getFormat().getExtension();
	}
	
	/**
	 * property value for external configuration path. so you can, for example,
	 * setup a system property like
	 * 'configureme.configuration-path=[MY_PATH]' to have all
	 * configurations somewhere outside of your classpath.
	 */
	private final String externalConfigPath = System.getProperty(EXTERNAL_CONF_PATH, null);
	
	@Override
	public boolean isAvailable(final ConfigurationSourceKey key){
		return getFile(key) != null;
	}

	@Override
	public long getLastChangeTimestamp(ConfigurationSourceKey key){
		final File f = getFile(key);
		if (f==null)
			throw new IllegalArgumentException("unable to find configuration with key : " + key);
		if (log.isDebugEnabled())
			log.debug("Checking timestamp for file: "+f.getAbsolutePath());

		long ret =  f.lastModified();
		if (log.isDebugEnabled())
			log.debug("file "+f.getAbsolutePath()+" last modified is: "+ DateUtils.toISO8601String(ret));
		return ret;
	}
	
	@Override
	public String getContent(final ConfigurationSourceKey key){
		final File f = getFile(key);
		if (f == null)
			throw new IllegalArgumentException("unable to find configuration with key : " + key);
		if (log.isInfoEnabled())
			log.info("load configuration from file: " + f.getAbsolutePath());
		try{
			if (!f.exists())
				return getContentFromJar(getFileName(key));
			
			return IOUtils.readFileBufferedAsString(f, "UTF-8");
		}catch(final IOException e){
			log.error("getContent("+key+ ')', e);
			throw new RuntimeException("can't read source: "+key, e);
		}
	}
	
	private String getContentFromJar(final String fileName) throws IOException{
		final ClassLoader myLoader = getClass().getClassLoader();
		final InputStream input = myLoader.getResourceAsStream(fileName);
		return IOUtils.readInputStreamBufferedAsString(input, "UTF-8");
	}
	
	
	/**
	 * Determine a {@link File} handle related to given {@link ConfigurationSourceKey}. This method returns 
	 * NULL if file does not exists - neither on file-system nor within a classpath-JAR-file. 
	 * Keep in mind, if file is located within JAR, {@link File#exists()} will return FALSE.
	 * 
	 * @param key
	 *            {@link ConfigurationSourceKey}
	 * @return {@link File} related to given {@link ConfigurationSourceKey} or
	 *         maybe NULL if no such configuration URL could be found.
	 */
	private File getFile(final ConfigurationSourceKey key) {
		final String fileName = getFileName(key);
		if (externalConfigPath != null) {
			final File f = new File(externalConfigPath, fileName);
			if (f.exists())
				return f; // return overwritten configuration location
		}
		
		// configuration-file was not overwritten, so read from class path
		final ClassLoader myLoader = getClass().getClassLoader();
		final URL url = myLoader.getResource(fileName);
		
		return url == null ? null : new File(url.getFile());
	}

}
