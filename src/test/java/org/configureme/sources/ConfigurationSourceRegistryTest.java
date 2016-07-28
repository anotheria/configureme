package org.configureme.sources;

import org.configureme.ConfigurableWrapper;
import org.configureme.GlobalEnvironment;
import org.configureme.sources.ConfigurationSourceKey.Format;
import org.configureme.sources.ConfigurationSourceKey.Type;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ConfigurationSourceRegistryTest {
	@BeforeClass public static void setupRegistry(){
		ConfigurationSourceRegistry.INSTANCE.reset();
		ConfigurationSourceRegistry.INSTANCE.addLoader(Type.FIXTURE, new FixtureLoader());
	}
	
	@Before public void resetFixture(){
		FixtureLoader.reset();
	}
	
	@Test public void testAvailability(){
		ConfigurationSourceKey presentKey = new ConfigurationSourceKey(Type.FIXTURE, Format.JSON, "fixture");
		ConfigurationSourceKey notPresentKey = new ConfigurationSourceKey(Type.FIXTURE, Format.JSON, "foobar");
		assertTrue("expected "+presentKey+" to be there", ConfigurationSourceRegistry.INSTANCE.isConfigurationAvailable(presentKey));
		assertFalse("expected "+notPresentKey+" not to be there", ConfigurationSourceRegistry.INSTANCE.isConfigurationAvailable(notPresentKey));
		
		FixtureLoader.setContent(null);
		assertFalse("expected "+presentKey+" now not to be there",ConfigurationSourceRegistry.INSTANCE.isConfigurationAvailable(presentKey));
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
	
	@Test public void testForEnum(){
		assertEquals(1, ConfigurationSourceRegistry.values().length);
		assertNotNull(ConfigurationSourceRegistry.valueOf("INSTANCE"));
	}
	
	@Test public void removeUnknownListener(){
		ConfigurationSourceRegistry.INSTANCE.removeListener(new ConfigurationSourceKey(Type.FIXTURE, Format.JSON, "not-existent"), null);
	}
	
	@Test(expected=AssertionError.class) public void addNullConfigurable(){
		ConfigurationSourceRegistry.INSTANCE.addWatchedConfigurable(new ConfigurableWrapper(null, null, null));
	}
}
