package org.configureme;

import static junit.framework.Assert.assertEquals;

import org.configureme.environments.DynamicEnvironment;
import org.configureme.sources.ConfigurationSourceRegistryTest;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReadConfigurationViaManager {
	@BeforeClass public static void setupRegistry(){
		//use the other test which can access protected methods
		ConfigurationSourceRegistryTest.setupRegistry();
	}
	
	@Test public void readInGlobalEnvironment(){
		Configuration config = ConfigurationManager.INSTANCE.getConfiguration("fixture");
		 
		assertEquals(100, Short.valueOf(config.getAttribute("shortValue")).shortValue());
		assertEquals(1234567890123L, Long.valueOf(config.getAttribute("longValue")).longValue());
		assertEquals(1000, Integer.valueOf(config.getAttribute("intValue")).intValue());
		assertEquals(true, config.getAttribute("booleanValue").equals("true"));
		assertEquals("foo", config.getAttribute("stringValue"));
		assertEquals(-125, Byte.valueOf(config.getAttribute("byteValue")).byteValue());
		assertEquals(12.5f, Float.valueOf(config.getAttribute("floatValue")).floatValue());
		assertEquals(1234.11, Double.valueOf(config.getAttribute("doubleValue")).doubleValue());
		assertEquals(null, config.getAttribute("onlyInA"));
		assertEquals(null, config.getAttribute("onlyInB"));
	}

	
	@Test public void readInA(){
		Environment old = ConfigurationManager.INSTANCE.getDefaultEnvironment();
		ConfigurationManager.INSTANCE.setDefaultEnvironment(new DynamicEnvironment("a"));
		Configuration config = ConfigurationManager.INSTANCE.getConfiguration("fixture");
		
		assertEquals(100, Short.valueOf(config.getAttribute("shortValue")).shortValue());
		assertEquals(1234567890123L, Long.valueOf(config.getAttribute("longValue")).longValue());
		assertEquals(2000, Integer.valueOf(config.getAttribute("intValue")).intValue());
		assertEquals(true, config.getAttribute("booleanValue").equals("true"));
		assertEquals("aaaaa", config.getAttribute("stringValue"));
		assertEquals(-125, Byte.valueOf(config.getAttribute("byteValue")).byteValue());
		assertEquals(12.5f, Float.valueOf(config.getAttribute("floatValue")).floatValue());
		assertEquals(1234.11, Double.valueOf(config.getAttribute("doubleValue")).doubleValue());
		assertEquals(1000, Integer.valueOf(config.getAttribute("onlyInA")).intValue());
		assertEquals(null, config.getAttribute("onlyInB"));
		
		ConfigurationManager.INSTANCE.setDefaultEnvironment(old);
	}

	
	@Test public void readInB(){
		DynamicEnvironment env = new DynamicEnvironment("a", "b");
		Configuration config = ConfigurationManager.INSTANCE.getConfiguration("fixture", env);
		
		assertEquals(100, Short.valueOf(config.getAttribute("shortValue")).shortValue());
		assertEquals(1234567890123L, Long.valueOf(config.getAttribute("longValue")).longValue());
		assertEquals(3000, Integer.valueOf(config.getAttribute("intValue")).intValue());
		assertEquals(true, config.getAttribute("booleanValue").equals("true"));
		assertEquals("bbbbb", config.getAttribute("stringValue"));
		assertEquals(-125, Byte.valueOf(config.getAttribute("byteValue")).byteValue());
		assertEquals(12.5f, Float.valueOf(config.getAttribute("floatValue")).floatValue());
		assertEquals(1234.11, Double.valueOf(config.getAttribute("doubleValue")).doubleValue());
		assertEquals(1000, Integer.valueOf(config.getAttribute("onlyInA")).intValue());
		assertEquals(1000, Integer.valueOf(config.getAttribute("onlyInB")).intValue());
	}
//*/

}
