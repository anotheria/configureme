package org.configureme;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.configureme.environments.DynamicEnvironment;
import org.configureme.sources.ConfigurationSourceRegistryTest;
import org.junit.BeforeClass;
import org.junit.Test;

public class AutoConfigTest {
	
	@BeforeClass public static void setupRegistry(){
		//use the other test which can access protected methods
		ConfigurationSourceRegistryTest.setupRegistry();
	}
	
	@Test public void testLifecycleAnnotations(){
		TestConfigurable configurable = new TestConfigurable();
		ConfigurationManager.INSTANCE.configure(configurable);

		assertTrue(configurable.isBeforeConfigCalled());
		assertTrue(configurable.isAfterConfigCalled());
		
		assertTrue(configurable.isBeforeInitialConfigCalled());
		assertTrue(configurable.isAfterInitialConfigCalled());
		
		assertFalse(configurable.isBeforeReConfigCalled());
		assertFalse(configurable.isAfterReConfigCalled());
	}
	
	@Test public void configureInGlobalEnvironment(){
		TestConfigurable configurable = new TestConfigurable();
		ConfigurationManager.INSTANCE.configure(configurable);
		
		assertEquals(100, configurable.getShortValue());
		assertEquals(1234567890123L, configurable.getLongValue());
		assertEquals(1000, configurable.getIntValue());
		assertEquals(true, configurable.getBooleanValue());
		assertEquals("foo", configurable.getStringValue());
		assertEquals(-125, configurable.getByteValue());
		assertEquals(12.5f, configurable.getFloatValue());
		assertEquals(1234.11, configurable.getDoubleValue());
		assertEquals(0, configurable.getOnlyInA());
		assertEquals(0, configurable.getOnlyInB());
	}

	@Test public void configureInA(){
		Environment old = ConfigurationManager.INSTANCE.getDefaultEnvironment();
		ConfigurationManager.INSTANCE.setDefaultEnvironment(new DynamicEnvironment("a"));
		TestConfigurable configurable = new TestConfigurable();
		ConfigurationManager.INSTANCE.configure(configurable);
		
		assertEquals(100, configurable.getShortValue());
		assertEquals(1234567890123L, configurable.getLongValue());
		assertEquals(2000, configurable.getIntValue());
		assertEquals(true, configurable.getBooleanValue());
		assertEquals("aaaaa", configurable.getStringValue());
		assertEquals(-125, configurable.getByteValue());
		assertEquals(12.5f, configurable.getFloatValue());
		assertEquals(1234.11, configurable.getDoubleValue());
		assertEquals(1000, configurable.getOnlyInA());
		assertEquals(0, configurable.getOnlyInB());
		
		ConfigurationManager.INSTANCE.setDefaultEnvironment(old);
	}

	@Test public void configureInB(){
		DynamicEnvironment env = new DynamicEnvironment("a", "b");
		TestConfigurable configurable = new TestConfigurable();
		ConfigurationManager.INSTANCE.configure(configurable, env);
		
		assertEquals(100, configurable.getShortValue());
		assertEquals(1234567890123L, configurable.getLongValue());
		assertEquals(3000, configurable.getIntValue());
		assertEquals(true, configurable.getBooleanValue());
		assertEquals("bbbbb", configurable.getStringValue());
		assertEquals(-125, configurable.getByteValue());
		assertEquals(12.5f, configurable.getFloatValue());
		assertEquals(1234.11, configurable.getDoubleValue());
		assertEquals(1000, configurable.getOnlyInA());
		assertEquals(1000, configurable.getOnlyInB());
	}
	
	@Test public void configurePublicFieldsInGlobalEnvironment(){
		ConfigurableWithPublicFields configurable = new ConfigurableWithPublicFields();
		ConfigurationManager.INSTANCE.configure(configurable);
		
		assertEquals(100, configurable.shortValue);
		assertEquals(1234567890123L, configurable.longValue);
		assertEquals(1000, configurable.intValue);
		assertEquals(true, configurable.booleanValue);
		assertEquals("foo", configurable.stringValue);
		assertEquals(-125, configurable.byteValue);
		assertEquals(12.5f, configurable.floatValue);
		assertEquals(1234.11, configurable.doubleValue);
		assertEquals(0, configurable.onlyInA);
		assertEquals(0, configurable.onlyInB);
	}

	@Test public void configurePublicFieldsInA(){
		Environment old = ConfigurationManager.INSTANCE.getDefaultEnvironment();
		ConfigurationManager.INSTANCE.setDefaultEnvironment(new DynamicEnvironment("a"));
		ConfigurableWithPublicFields configurable = new ConfigurableWithPublicFields();
		ConfigurationManager.INSTANCE.configure(configurable);
		
		assertEquals(100, configurable.shortValue);
		assertEquals(1234567890123L, configurable.longValue);
		assertEquals(2000, configurable.intValue);
		assertEquals(true, configurable.booleanValue);
		assertEquals("aaaaa", configurable.stringValue);
		assertEquals(-125, configurable.byteValue);
		assertEquals(12.5f, configurable.floatValue);
		assertEquals(1234.11, configurable.doubleValue);
		assertEquals(1000, configurable.onlyInA);
		assertEquals(0, configurable.onlyInB);
		
		ConfigurationManager.INSTANCE.setDefaultEnvironment(old);
	}

	@Test public void configurePublicFieldsInB(){
		DynamicEnvironment env = new DynamicEnvironment("a", "b");
		ConfigurableWithPublicFields configurable = new ConfigurableWithPublicFields();
		ConfigurationManager.INSTANCE.configure(configurable, env);
		
		assertEquals(100, configurable.shortValue);
		assertEquals(1234567890123L, configurable.longValue);
		assertEquals(3000, configurable.intValue);
		assertEquals(true, configurable.booleanValue);
		assertEquals("bbbbb", configurable.stringValue);
		assertEquals(-125, configurable.byteValue);
		assertEquals(12.5f, configurable.floatValue);
		assertEquals(1234.11, configurable.doubleValue);
		assertEquals(1000, configurable.onlyInA);
		assertEquals(1000, configurable.onlyInB);
	}
}
