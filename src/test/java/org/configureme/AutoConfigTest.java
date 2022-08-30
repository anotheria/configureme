package org.configureme;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.configureme.environments.DynamicEnvironment;
import org.configureme.sources.ConfigurationSourceKey.Format;
import org.configureme.sources.ConfigurationSourceRegistryTest;
import org.configureme.sources.FixtureLoader;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AutoConfigTest {

	@BeforeClass public static void setupRegistry(){
		//use the other test which can access protected methods
		ConfigurationSourceRegistryTest.setupRegistry();
	}

	@Before public void resetFixture(){
		FixtureLoader.reset();
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
		//System.out.println("C VALUE: "+configurable.getIntValue());
		//System.out.println("FIXTURE: "+FixtureLoader.getContent());
		assertEquals(1000, configurable.getIntValue());
		assertEquals(true, configurable.getBooleanValue());
		assertEquals("foo", configurable.getStringValue());
		assertEquals(-125, configurable.getByteValue());
		assertEquals(12.5f, configurable.getFloatValue(),0);
		assertEquals(1234.11, configurable.getDoubleValue(),0);
		assertEquals(0, configurable.getOnlyInA());
		assertEquals(0, configurable.getOnlyInB());
		assertArrayEquals("Arrays should be equals", new String[]{"sa1.1 " , "sa2.1,sa2.2" , "sa3"}, configurable.getStringArray());
		assertTrue("Arrays should be equals", Arrays.equals(new float[] {2.3F, 3}, configurable.getFloatArray()));
		assertArrayEquals("Arrays should be equals", new int[] {2, 3, 5}, configurable.getIntArray());
		assertTrue("Arrays should be equals", Arrays.equals(new boolean[] {true, false, true}, configurable.getBooleanArray()));
		assertArrayEquals("Arrays should be equals", new int[] {}, configurable.getEmptyIntArray());
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
		assertEquals(12.5f, configurable.getFloatValue(),0);
		assertEquals(1234.11, configurable.getDoubleValue(),0);
		assertEquals(1000, configurable.getOnlyInA());
		assertEquals(0, configurable.getOnlyInB());

		ConfigurationManager.INSTANCE.setDefaultEnvironment(old);
	}

	@Test public void configureAsInA(){
		Environment old = ConfigurationManager.INSTANCE.getDefaultEnvironment();
		ConfigurationManager.INSTANCE.setDefaultEnvironment(new DynamicEnvironment("a"));
		TestConfigurableAs configurable = new TestConfigurableAs();
		ConfigurationManager.INSTANCE.configureAs(configurable, "fixture");

		assertEquals(100, configurable.getShortValue());
		assertEquals(1234567890123L, configurable.getLongValue());
		assertEquals(2000, configurable.getIntValue());
		assertEquals(true, configurable.getBooleanValue());
		assertEquals("aaaaa", configurable.getStringValue());
		assertEquals(-125, configurable.getByteValue());
		assertEquals(12.5f, configurable.getFloatValue(),0);
		assertEquals(1234.11, configurable.getDoubleValue(),0);
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
		assertEquals(12.5f, configurable.getFloatValue(),0);
		assertEquals(1234.11, configurable.getDoubleValue(),0);
		assertEquals(1000, configurable.getOnlyInA());
		assertEquals(1000, configurable.getOnlyInB());
	}

	@Test public void configureInBWithFormat(){//helper method to check if internal routing with format works.
		DynamicEnvironment env = new DynamicEnvironment("a", "b");
		TestConfigurable configurable = new TestConfigurable();
		ConfigurationManager.INSTANCE.configure(configurable, env, Format.JSON);

		assertEquals(100, configurable.getShortValue());
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
		assertEquals(12.5f, configurable.floatValue,0);
		assertEquals(1234.11, configurable.doubleValue,0);
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
		assertEquals(12.5f, configurable.floatValue,0);
		assertEquals(1234.11, configurable.doubleValue,0);
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
		assertEquals(12.5f, configurable.floatValue,0);
		assertEquals(1234.11, configurable.doubleValue,0);
		assertEquals(1000, configurable.onlyInA);
		assertEquals(1000, configurable.onlyInB);
	}

	@Test public void configureAll(){
		TestAllConfigurable configurable = new TestAllConfigurable();
		ConfigurationManager.INSTANCE.configure(configurable);

		assertEquals(100, configurable.getShortValue());
		assertEquals(1234567890123L, configurable.getLongValue());
		assertEquals(1000, configurable.getIntValue());
		assertEquals(true, configurable.getBooleanValue());
		assertEquals("foo", configurable.getStringValue());
		assertEquals(-125, configurable.getByteValue());
		assertEquals(12.5f, configurable.getFloatValue(),0);
		assertEquals(1234.11, configurable.getDoubleValue(),0);
	}

	@Test public void configureAllWithExclusion(){
		TestNotConfigurable configurable = new TestNotConfigurable();
		ConfigurationManager.INSTANCE.configure(configurable);

		assertEquals(100, configurable.getShortValue());
		assertEquals(0L, configurable.getLongValue());
		assertEquals(1000, configurable.getIntValue());
		assertEquals(false, configurable.getBooleanValue());
		assertEquals("foo", configurable.getStringValue());
		assertEquals(0, configurable.getByteValue());
		assertEquals(12.5f, configurable.getFloatValue(),0);
		assertEquals(0.0, configurable.getDoubleValue(),0);
	}

}
