package org.configureme;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.configureme.repository.ConfigurationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class SystemPropertiesTest {
	@Disabled @Test public void testUnset(){
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
	
	@BeforeEach public void ensureReload(){
		ConfigurationRepository.INSTANCE.resetForUnitTests();
	}
}
