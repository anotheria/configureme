package org.configureme.sources;

import org.configureme.sources.ConfigurationSourceKey.Format;
import org.configureme.sources.ConfigurationSourceKey.Type;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationSourceRegistryAddTest {
	
	@BeforeAll public static void resetSourceRegistry(){
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

	@Test public void testUnsupportedTypeFailureLoading(){
		ConfigurationSourceKey key = new ConfigurationSourceKey(Type.FIXTURE, Format.JSON, "fixture");
		assertThrows(IllegalArgumentException.class, () -> ConfigurationSourceRegistry.INSTANCE.readConfigurationSource(key));
	}
}
