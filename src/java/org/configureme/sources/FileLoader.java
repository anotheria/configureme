package org.configureme.sources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import net.anotheria.util.NumberUtils;

import org.apache.log4j.Logger;

/**
 * A source loader for files.
 * @author lrosenberg
 */
public class FileLoader implements SourceLoader{
	
	/**
	 * Logger.
	 */
	private static final Logger log = Logger.getLogger(FileLoader.class);
	
	/**
	 * prefix for external configuration. so you can, for example, setup a system property like 
	 * 'configureme.external-configuration.moskito=[MY_PATH]' to have this configuration somewhere outside of your
	 * classpath.
	 */
	private static final String EXTERNAL_CONF_PREFIX = "configureme.external-configuration.";
	
	/**
	 * Returns the file name for the given source key.
	 * @param source configuration source key
	 * @return the file name for the given source key
	 */
	public static final String getFileName(ConfigurationSourceKey source){
		if (source.getType()!=ConfigurationSourceKey.Type.FILE)
			throw new AssertionError("Can only load configuration sources with type "+ConfigurationSourceKey.Type.FILE);
		return source.getName()+"."+source.getFormat().getExtension();
	}
	
	@Override
	public boolean isAvailable(ConfigurationSourceKey key){
		//ensure an exception is thrown if we are not file.
		File f = getFile(key);
		return f != null && f.exists();
	}

	@Override
	public long getLastChangeTimestamp(ConfigurationSourceKey key){
		//ensure an exception is thrown if we are not file.
		File f = getFile(key);
		if (f==null || !f.exists()) {
			throw new IllegalArgumentException("unable to find configuration with key : " + key);
		}

		log.debug("Checking timestamp for file: "+f.getAbsolutePath());
		long ret =  f.lastModified();
		log.debug("file "+f.getAbsolutePath()+" last modified is: "+NumberUtils.makeISO8601TimestampString(ret));
		return ret;
	}
	
	@Override
	public String getContent(ConfigurationSourceKey key){
		final File f = getFile(key);
		if (f == null) {
			throw new IllegalArgumentException("unable to find configuration with key : " + key);
		}
		
		if (log.isInfoEnabled()) {
			log.info("load configuration from file: " + f.getAbsolutePath());
		}

		Reader reader = null;
		try{
			if (!f.exists()){
				String fileName = getFileName(key);
				return getContentFromJar(fileName);
			}
			
			reader = new BufferedReader(new FileReader(f));
			StringBuilder ret = new StringBuilder();
			int c ; 
			while((c=reader.read())!=-1)
				ret.append((char)c);
			return ret.toString();
		}catch(IOException e){
			log.error("getSourceContent("+key+")", e);
			throw new RuntimeException("can't read source: "+key, e);
		}finally{
			try{
				if (reader!=null)
					reader.close();
			}catch(IOException ignored){}
		}
	}
	
	private String getContentFromJar(String fileName) throws IOException{
		ClassLoader myLoader = getClass().getClassLoader();
		InputStream input = myLoader.getResourceAsStream(fileName);
		byte[] data = new byte[input.available()];
		input.read(data);
		return new String(data);
	}
	
	
	/**
	 * Determine a {@link File} handle related to given
	 * {@link ConfigurationSourceKey}. Keep in mind, you still have to check if
	 * such a file exists!
	 * 
	 * @param key
	 *            {@link ConfigurationSourceKey}
	 * @return {@link File} related to given {@link ConfigurationSourceKey} or
	 *         NULL if no such configuration URL could be found.
	 */
	private File getFile(final ConfigurationSourceKey key) {
		File f = null;
		final String externalConfig = System.getProperty(EXTERNAL_CONF_PREFIX + key.getName(), null);
		if (externalConfig == null) {
			final String fileName = getFileName(key);
			final ClassLoader myLoader = getClass().getClassLoader();
			final URL url = myLoader.getResource(fileName);
			if (url != null) {
				f = new File(url.getFile());
			}
		
		} else {
			f = new File(externalConfig);
		}

		return f;
	}

}
