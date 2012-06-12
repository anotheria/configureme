package org.configureme;

import static org.junit.Assert.assertEquals;

import org.configureme.repository.ConfigurationRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SystemPropertiesTest {
	@Ignore @Test public void testUnset(){
		SystemPropertiesConfigurable spc = new SystemPropertiesConfigurable();
		ConfigurationManager.INSTANCE.configure(spc);
		assertEquals("${TEST_PROPERTY}", spc.getValue());
	}
	@Test public void testSet(){
		System.setProperty("TEST_PROPERTY", "HELLO");
		SystemPropertiesConfigurable spc = new SystemPropertiesConfigurable();
		ConfigurationManager.INSTANCE.configure(spc);
		assertEquals("HELLO",spc.getValue());
	}
	
	@Before public void ensureReload(){
		ConfigurationRepository.INSTANCE.resetForUnitTests();
	}
}
