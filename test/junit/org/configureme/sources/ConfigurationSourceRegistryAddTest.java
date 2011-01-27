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
	
	
	@Test(expected=IllegalArgumentException.class) public void testUnsupportedTypeFailureAvailability(){
		ConfigurationSourceKey key = new ConfigurationSourceKey(Type.FIXTURE, Format.JSON, "fixture");
		ConfigurationSourceRegistry.INSTANCE.isConfigurationAvailable(key);
		fail("ConfigurationSourceRegistry should have thrown an exception");
	}

	@Test(expected=IllegalArgumentException.class) public void testUnsupportedTypeFailureLoading(){
		ConfigurationSourceKey key = new ConfigurationSourceKey(Type.FIXTURE, Format.JSON, "fixture");
		ConfigurationSourceRegistry.INSTANCE.readConfigurationSource(key);
		fail("ConfigurationSourceRegistry should have thrown an exception");
	}
}
