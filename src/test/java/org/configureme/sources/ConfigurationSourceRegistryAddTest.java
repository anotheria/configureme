package org.configureme.sources;

import org.configureme.sources.ConfigurationSourceKey.Format;
import org.configureme.sources.ConfigurationSourceKey.Type;
import org.junit.BeforeClass;
import org.junit.Test;
import static junit.framework.Assert.*;

public class ConfigurationSourceRegistryAddTest {
	
	@BeforeClass public static void resetSourceRegistry(){
		ConfigurationSourceRegistry.INSTANCE.reset();
	}
	
	
	@Test public void testUnsupportedTypeFailureAvailability(){
		ConfigurationSourceKey key = new ConfigurationSourceKey(Type.FIXTURE, Format.JSON, "fixture");
		try{
			ConfigurationSourceRegistry.INSTANCE.isConfigurationAvailable(key);
			fail("ConfigurationSourceRegistry should have thrown an exception");
		}catch(IllegalArgumentException e){
			//expected
		}
	}

	@Test(expected=IllegalArgumentException.class) public void testUnsupportedTypeFailureLoading(){
		ConfigurationSourceKey key = new ConfigurationSourceKey(Type.FIXTURE, Format.JSON, "fixture");
		ConfigurationSourceRegistry.INSTANCE.readConfigurationSource(key);
		fail("ConfigurationSourceRegistry should have thrown an exception");
	}
}
