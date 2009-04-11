package org.configureme.repository;

import org.configureme.Configuration;
import org.configureme.GlobalEnvironment;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

public class ConfigurationRepositoryTest {
	@Test public void testLookup(){
		assertFalse(ConfigurationRepository.INSTANCE.hasConfiguration("foo"));
	}
	
	@Before public void setupConfigObject(){
		if (ConfigurationRepository.INSTANCE.hasConfiguration("test"))
			return;
		Artefact test = ConfigurationRepository.INSTANCE.createArtefact("test");
		assertTrue(ConfigurationRepository.INSTANCE.hasConfiguration("test"));
		test.addAttributeValue("foo","bar", GlobalEnvironment.INSTANCE);
	}
	
	@Test (expected=IllegalArgumentException.class) public void recreate(){
		ConfigurationRepository.INSTANCE.createArtefact("test");
		fail("Exception expected");
	}

	@Test (expected=IllegalArgumentException.class) public void queryNonExistent(){
		ConfigurationRepository.INSTANCE.getConfiguration("foo", GlobalEnvironment.INSTANCE);
		fail("Exception expected");
	}
	
	@Test public void readConfig(){
		Configuration config = ConfigurationRepository.INSTANCE.getConfiguration("test", null);
		Configuration config2 = ConfigurationRepository.INSTANCE.getConfiguration("test", GlobalEnvironment.INSTANCE);
		
		assertEquals(config, config2);
		
		assertEquals("bar", config.getAttribute("foo"));
		assertEquals("bar", config2.getAttribute("foo"));
	}

}
