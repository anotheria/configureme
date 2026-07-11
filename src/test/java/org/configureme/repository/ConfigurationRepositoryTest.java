package org.configureme.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Map.Entry;
import java.util.Set;

import org.configureme.Configuration;
import org.configureme.GlobalEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigurationRepositoryTest {
	@Test public void testLookup(){
		assertFalse(ConfigurationRepository.INSTANCE.hasConfiguration("foo"));
	}

	@BeforeEach public void setupConfigObject(){
		if (ConfigurationRepository.INSTANCE.hasConfiguration("test"))
			return;
		Artefact test = ConfigurationRepository.INSTANCE.createArtefact("test");
		assertTrue(ConfigurationRepository.INSTANCE.hasConfiguration("test"));
		test.addAttributeValue("foo", new PlainValue("bar"), GlobalEnvironment.INSTANCE);

		if (ConfigurationRepository.INSTANCE.hasConfiguration("test2"))
			return;
		Artefact test2 = ConfigurationRepository.INSTANCE.createArtefact("test2");
		assertTrue(ConfigurationRepository.INSTANCE.hasConfiguration("test2"));
		test2.addAttributeValue("foo", new PlainValue("bar"), GlobalEnvironment.INSTANCE);

	}

	@BeforeEach public void setupLargeConfig(){
		if (ConfigurationRepository.INSTANCE.hasConfiguration("large"))
			return;
		Artefact large = ConfigurationRepository.INSTANCE.createArtefact("large");
		assertTrue(ConfigurationRepository.INSTANCE.hasConfiguration("large"));
		for (int i=0; i<10; i++)
			large.addAttributeValue("key"+i, new PlainValue("value"+i), GlobalEnvironment.INSTANCE);

	}

	@Test public void readMultipleValue(){
		Configuration config = ConfigurationRepository.INSTANCE.getConfiguration("large", null);
		assertNotNull(config.toString());
		assertEquals(10, config.getAttributeNames().size());
		assertEquals(10, config.getEntries().size());

		Set<Entry<String,Value>> entries = config.getEntries();
		for (Entry<String,Value> entry : entries){
			String str = ((PlainValue) entry.getValue()).get();
			String key = entry.getKey().substring(0, entry.getKey().length()-1);
			String value = str.substring(0, str.length()-1);
			int keyNumber = Integer.parseInt(""+entry.getKey().charAt(entry.getKey().length()-1));
			int valueNumber = Integer.parseInt(""+str.charAt(str.length()-1));
			assertEquals("key",key);
			assertEquals("value",value);
			assertEquals(keyNumber, valueNumber);
		}
	}

	@Test public void recreate(){
		assertThrows(IllegalArgumentException.class, () -> ConfigurationRepository.INSTANCE.createArtefact("test"));
	}

	@Test public void queryNonExistent(){
		assertThrows(IllegalArgumentException.class, () -> ConfigurationRepository.INSTANCE.getConfiguration("foo", GlobalEnvironment.INSTANCE));
	}

	@Test public void readConfig(){
		Configuration config = ConfigurationRepository.INSTANCE.getConfiguration("test", null);
		Configuration config2 = ConfigurationRepository.INSTANCE.getConfiguration("test", GlobalEnvironment.INSTANCE);
		Configuration config3 = ConfigurationRepository.INSTANCE.getConfiguration("test2", GlobalEnvironment.INSTANCE);
		assertNotNull(config.toString());
		assertNotNull(config2.toString());
		assertNotNull(config3.toString());

		assertEquals(config, config2);
		assertFalse(config.equals(config3));

		assertEquals(new PlainValue("bar"), config.getAttribute("foo"));
		assertEquals(new PlainValue("bar"), config2.getAttribute("foo"));
	}

	@Test public void coverEnumFunctions(){
		assertEquals(1, ConfigurationRepository.values().length, "Only one instance of repository is allowed");
		assertSame(ConfigurationRepository.INSTANCE, ConfigurationRepository.valueOf("INSTANCE"));
	}

}
