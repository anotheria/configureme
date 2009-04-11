package org.configureme.sources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

import net.anotheria.util.NumberUtils;

import org.apache.log4j.Logger;

public class FileLoader implements SourceLoader{
	
	private static Logger log = Logger.getLogger(FileLoader.class);
	
	public static final String getFileName(ConfigurationSourceKey source){
		if (source.getType()!=ConfigurationSourceKey.Type.FILE)
			throw new AssertionError("Can only load configuration sources with type "+ConfigurationSourceKey.Type.FILE);
		return source.getName()+"."+source.getFormat().getExtension();
	}
	
	public boolean isAvailable(ConfigurationSourceKey key){
		//ensure an exception is thrown if we are not file.
		String fileName = getFileName(key);
		ClassLoader myLoader = getClass().getClassLoader();

		URL u = myLoader.getResource(fileName);
		return u!=null;
	}

	public long getLastChangeTimestamp(ConfigurationSourceKey key){
		//ensure an exception is thrown if we are not file.
		String fileName = getFileName(key);
		ClassLoader myLoader = getClass().getClassLoader();

		URL u = myLoader.getResource(fileName);
		if (u==null){
			throw new IllegalArgumentException("File: "+fileName+" doesn't exists ("+u+")");
		}
		
		File f = new File(u.getFile());
		log.debug("Checking timestamp for file: "+f.getAbsolutePath());
		long ret =  f.lastModified();
		log.debug("file "+f.getAbsolutePath()+" last modified is: "+NumberUtils.makeISO8601TimestampString(ret));
		return ret;
	}
	
	public String getContent(ConfigurationSourceKey key){
		//ensure an exception is thrown if we are not file.
		String fileName = getFileName(key);
		ClassLoader myLoader = getClass().getClassLoader();

		URL u = myLoader.getResource(fileName);
		if (u==null){
			throw new IllegalArgumentException("File: "+fileName+" doesn't exists ("+u+")");
		}
		
		try{
			File f = new File(u.getFile());
			Reader reader = new BufferedReader(new FileReader(f));
			StringBuilder ret = new StringBuilder();
			int c ; 
			while((c=reader.read())!=-1)
				ret.append((char)c);
			return ret.toString();
		}catch(IOException e){
			log.error("getSourceContent("+key+")", e);
			throw new RuntimeException("can't read source: "+key, e);
		}
	}
//*/
}
