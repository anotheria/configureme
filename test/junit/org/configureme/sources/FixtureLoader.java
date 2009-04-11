package org.configureme.sources;

import net.anotheria.util.IOUtils;

public class FixtureLoader implements SourceLoader{
	
	private static long lastUpdateTimestamp = System.currentTimeMillis();
	
	private static String content = null;
	private static String originalContent = null;

	static{
		try{
			originalContent = IOUtils.readFileAtOnceAsString("test/junit/fixture.json"); 
		}catch(Exception e){
			e.printStackTrace();
			throw new AssertionError("can't run tests without fixture file");
		}
		reset();
	}

	@Override
	public String getContent(ConfigurationSourceKey key) {
		if (key.getName().equals("fixture"))
			return content;
		throw new IllegalArgumentException("File: "+key.getName()+" doesn't exist");
	}

	@Override
	public long getLastChangeTimestamp(ConfigurationSourceKey key) {
		return lastUpdateTimestamp;
	}

	@Override
	public boolean isAvailable(ConfigurationSourceKey key) {
		return key.getName().equals("fixture") && content!=null;
	}
	
	public static void setContent(String aContent){
		content = aContent;
	}
	
	public static void setLastUpdateTimestamp(){
		lastUpdateTimestamp = System.currentTimeMillis();
	}
	
	public static void setLastUpdateTimestamp(long aTimestamp){
		lastUpdateTimestamp = aTimestamp;
	}
	
	public static String getContent(){
		return content;
	}
	
	public static final void reset(){
		content = originalContent;
	}
}
