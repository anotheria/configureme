package org.configureme.sources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
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
	private static Logger log = Logger.getLogger(FileLoader.class);
	
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
		String fileName = getFileName(key);
		ClassLoader myLoader = getClass().getClassLoader();

		URL u = myLoader.getResource(fileName);
		return u!=null;
	}

	@Override
	public long getLastChangeTimestamp(ConfigurationSourceKey key){
		//ensure an exception is thrown if we are not file.
		String fileName = getFileName(key);
		ClassLoader myLoader = getClass().getClassLoader();

		URL u = myLoader.getResource(fileName);
		if (u==null){
			throw new IllegalArgumentException("File: "+fileName+" doesn't exists (URL is null)");
		}
		
		File f = new File(u.getFile());
		log.debug("Checking timestamp for file: "+f.getAbsolutePath());
		long ret =  f.lastModified();
		log.debug("file "+f.getAbsolutePath()+" last modified is: "+NumberUtils.makeISO8601TimestampString(ret));
		return ret;
	}
	
	@Override
	public String getContent(ConfigurationSourceKey key){
		//ensure an exception is thrown if we are not file.
		String fileName = getFileName(key);
		ClassLoader myLoader = getClass().getClassLoader();

		URL u = myLoader.getResource(fileName);
		if (u==null){
			throw new IllegalArgumentException("File: "+fileName+" doesn't exists (URL is null)");
		}
		Reader reader = null;
		try{
			File f = new File(u.getFile());
			if (!f.exists()){
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
	
//*/
}
