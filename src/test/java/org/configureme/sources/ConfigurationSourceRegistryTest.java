package org.configureme.sources;

import org.configureme.ConfigurableWrapper;
import org.configureme.GlobalEnvironment;
import org.configureme.sources.ConfigurationSourceKey.Format;
import org.configureme.sources.ConfigurationSourceKey.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ConfigurationSourceRegistryTest {
	@BeforeAll public static void setupRegistry(){
		ConfigurationSourceRegistry.INSTANCE.reset();
		ConfigurationSourceRegistry.INSTANCE.addLoader(Type.FIXTURE, new FixtureLoader());
	}
	
	@BeforeEach public void resetFixture(){
		FixtureLoader.reset();
	}
	
	@Test public void testAvailability(){
		ConfigurationSourceKey presentKey = new ConfigurationSourceKey(Type.FIXTURE, Format.JSON, "fixture");
		ConfigurationSourceKey notPresentKey = new ConfigurationSourceKey(Type.FIXTURE, Format.JSON, "foobar");
		assertTrue(ConfigurationSourceRegistry.INSTANCE.isConfigurationAvailable(presentKey), "expected "+presentKey+" to be there");
		assertFalse(ConfigurationSourceRegistry.INSTANCE.isConfigurationAvailable(notPresentKey), "expected "+notPresentKey+" not to be there");
		
		FixtureLoader.setContent(null);
		assertFalse(ConfigurationSourceRegistry.INSTANCE.isConfigurationAvailable(presentKey), "expected "+presentKey+" now not to be there");
	}
	
	@Test public void testWatchedResourceCaching(){
		Object dummy = new Object();
		ConfigurationSourceKey presentKey = new ConfigurationSourceKey(Type.FIXTURE, Format.JSON, "fixture");
		assertTrue(ConfigurationSourceRegistry.INSTANCE.isConfigurationAvailable(presentKey));
		ConfigurationSourceRegistry.INSTANCE.addWatchedConfigurable(new ConfigurableWrapper(presentKey, dummy, GlobalEnvironment.INSTANCE));
		
		FixtureLoader.setContent(null);
		//since the key is registered as watched the configurationsourceregistry thinks its there, even its not.
		assertTrue(ConfigurationSourceRegistry.INSTANCE.isConfigurationAvailable(presentKey));
		//cleanup
		ConfigurationSourceRegistry.INSTANCE.removeWatchedConfigurable(new ConfigurableWrapper(presentKey, dummy, GlobalEnvironment.INSTANCE));
		
		
	}
	
	
	@Test public void loadNonExistent(){
		ConfigurationSourceKey notPresentKey = new ConfigurationSourceKey(Type.FIXTURE, Format.JSON, "foobar");
		assertThrows(IllegalArgumentException.class, () -> ConfigurationSourceRegistry.INSTANCE.readConfigurationSource(notPresentKey));
	}
	
	@Test public void loadExistent(){
		ConfigurationSourceKey presentKey = new ConfigurationSourceKey(Type.FIXTURE, Format.JSON, "fixture");
		String content = ConfigurationSourceRegistry.INSTANCE.readConfigurationSource(presentKey);
		assertEquals(content, FixtureLoader.getContent());
	}
	
	@Test public void testForEnum(){
		assertEquals(1, ConfigurationSourceRegistry.values().length);
		assertNotNull(ConfigurationSourceRegistry.valueOf("INSTANCE"));
	}
	
	@Test public void removeUnknownListener(){
		ConfigurationSourceRegistry.INSTANCE.removeListener(new ConfigurationSourceKey(Type.FIXTURE, Format.JSON, "not-existent"), null);
	}
	
	@Test public void addNullConfigurable(){
		assertThrows(AssertionError.class, () -> ConfigurationSourceRegistry.INSTANCE.addWatchedConfigurable(new ConfigurableWrapper(null, null, null)));
	}
}
