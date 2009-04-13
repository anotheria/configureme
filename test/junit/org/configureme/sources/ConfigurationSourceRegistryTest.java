package org.configureme.sources;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import org.configureme.ConfigurableWrapper;
import org.configureme.GlobalEnvironment;
import org.configureme.sources.ConfigurationSourceKey.Format;
import org.configureme.sources.ConfigurationSourceKey.Type;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConfigurationSourceRegistryTest {
	@BeforeClass public static void setupRegistry(){
		ConfigurationSourceRegistry.INSTANCE.addLoader(Type.FIXTURE, new FixtureLoader());
	}
	
	@Before public void resetFixture(){
		FixtureLoader.reset();
	}
	
	@Test public void testAvailability(){
		ConfigurationSourceKey presentKey = new ConfigurationSourceKey(Type.FIXTURE, Format.JSON, "fixture");
		ConfigurationSourceKey notPresentKey = new ConfigurationSourceKey(Type.FIXTURE, Format.JSON, "foobar");
		assertTrue(ConfigurationSourceRegistry.INSTANCE.isConfigurationAvailable(presentKey));
		assertFalse(ConfigurationSourceRegistry.INSTANCE.isConfigurationAvailable(notPresentKey));
		
		FixtureLoader.setContent(null);
		assertFalse(ConfigurationSourceRegistry.INSTANCE.isConfigurationAvailable(presentKey));
	}
	
	@Test public void testWatchedResourceCaching(){
		ConfigurationSourceKey presentKey = new ConfigurationSourceKey(Type.FIXTURE, Format.JSON, "fixture");
		assertTrue(ConfigurationSourceRegistry.INSTANCE.isConfigurationAvailable(presentKey));
		ConfigurationSourceRegistry.INSTANCE.addWatchedConfigurable(new ConfigurableWrapper(presentKey, new Object(), GlobalEnvironment.INSTANCE));
		
		FixtureLoader.setContent(null);
		//since the key is registered as watched the configurationsourceregistry thinks its there, even its not.
		assertTrue(ConfigurationSourceRegistry.INSTANCE.isConfigurationAvailable(presentKey));
		ConfigurationSourceRegistry.INSTANCE.removeWatchedConfigurable(new ConfigurableWrapper(presentKey, new Object(), GlobalEnvironment.INSTANCE));
		
		
	}
	
	
	@Test (expected=IllegalArgumentException.class) public void loadNonExistent(){
		ConfigurationSourceKey notPresentKey = new ConfigurationSourceKey(Type.FIXTURE, Format.JSON, "foobar");
		ConfigurationSourceRegistry.INSTANCE.readConfigurationSource(notPresentKey);
		fail("Exception should have been thrown");
	}
	
	@Test public void loadExistent(){
		ConfigurationSourceKey presentKey = new ConfigurationSourceKey(Type.FIXTURE, Format.JSON, "fixture");
		String content = ConfigurationSourceRegistry.INSTANCE.readConfigurationSource(presentKey);
		assertEquals(content, FixtureLoader.getContent());
	}
}
