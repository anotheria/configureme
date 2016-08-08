package org.configureme;

import org.configureme.environments.DynamicEnvironment;
import org.configureme.repository.PlainValue;
import org.configureme.sources.ConfigurationSourceRegistryTest;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ReadConfigurationViaManager {
	@BeforeClass public static void setupRegistry(){
		//use the other test which can access protected methods
		ConfigurationSourceRegistryTest.setupRegistry();
	}

	@Test public void readInGlobalEnvironment(){
		Configuration config = ConfigurationManager.INSTANCE.getConfiguration("fixture");

		assertEquals(100, Short.valueOf(((PlainValue) config.getAttribute("shortValue")).get()).shortValue());
		assertEquals(1234567890123L, Long.valueOf(((PlainValue) config.getAttribute("longValue")).get()).longValue());
		assertEquals(1000, Integer.valueOf(((PlainValue) config.getAttribute("intValue")).get()).intValue());
		assertEquals(true, ((PlainValue) config.getAttribute("booleanValue")).get().equals("true"));
		assertEquals("foo", ((PlainValue) config.getAttribute("stringValue")).get());
		assertEquals(-125, Byte.valueOf(((PlainValue) config.getAttribute("byteValue")).get()).byteValue());
		assertThat(Float.valueOf(((PlainValue) config.getAttribute("floatValue")).get()).floatValue(), is(12.5f));
		assertThat(Double.valueOf(((PlainValue) config.getAttribute("doubleValue")).get()).doubleValue(), is(1234.11));
		assertEquals(null, config.getAttribute("onlyInA"));
		assertEquals(null, config.getAttribute("onlyInB"));
	}


	@Test public void readInA(){
		Environment old = ConfigurationManager.INSTANCE.getDefaultEnvironment();
		ConfigurationManager.INSTANCE.setDefaultEnvironment(new DynamicEnvironment("a"));
		Configuration config = ConfigurationManager.INSTANCE.getConfiguration("fixture");

		assertEquals(100, Short.valueOf(((PlainValue) config.getAttribute("shortValue")).get()).shortValue());
		assertEquals(1234567890123L, Long.valueOf(((PlainValue) config.getAttribute("longValue")).get()).longValue());
		assertEquals(2000, Integer.valueOf(((PlainValue) config.getAttribute("intValue")).get()).intValue());
		assertEquals(true, ((PlainValue) config.getAttribute("booleanValue")).get().equals("true"));
		assertEquals("aaaaa", ((PlainValue) config.getAttribute("stringValue")).get());
		assertEquals(-125, Byte.valueOf(((PlainValue) config.getAttribute("byteValue")).get()).byteValue());
		assertThat(Float.valueOf(((PlainValue) config.getAttribute("floatValue")).get()).floatValue(), is(12.5f));
		assertThat(Double.valueOf(((PlainValue) config.getAttribute("doubleValue")).get()).doubleValue(), is(1234.11));
		assertEquals(1000, Integer.valueOf(((PlainValue) config.getAttribute("onlyInA")).get()).intValue());
		assertEquals(null, config.getAttribute("onlyInB"));

		ConfigurationManager.INSTANCE.setDefaultEnvironment(old);
	}


	@Test public void readInB(){
		DynamicEnvironment env = new DynamicEnvironment("a", "b");
		Configuration config = ConfigurationManager.INSTANCE.getConfiguration("fixture", env);

		assertEquals(100, Short.valueOf(((PlainValue) config.getAttribute("shortValue")).get()).shortValue());
		assertEquals(1234567890123L, Long.valueOf(((PlainValue) config.getAttribute("longValue")).get()).longValue());
		assertEquals(3000, Integer.valueOf(((PlainValue) config.getAttribute("intValue")).get()).intValue());
		assertEquals(true, ((PlainValue) config.getAttribute("booleanValue")).get().equals("true"));
		assertEquals("bbbbb", ((PlainValue) config.getAttribute("stringValue")).get());
		assertEquals(-125, Byte.valueOf(((PlainValue) config.getAttribute("byteValue")).get()).byteValue());
		assertThat(Float.valueOf(((PlainValue) config.getAttribute("floatValue")).get()).floatValue(), is(12.5f));
		assertThat(Double.valueOf(((PlainValue) config.getAttribute("doubleValue")).get()).doubleValue(), is(1234.11));
		assertEquals(1000, Integer.valueOf(((PlainValue) config.getAttribute("onlyInA")).get()).intValue());
		assertEquals(1000, Integer.valueOf(((PlainValue) config.getAttribute("onlyInB")).get()).intValue());
	}
//*/

}
