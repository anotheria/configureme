package org.configureme;

import static org.junit.Assert.*;

import org.configureme.environments.DynamicEnvironment;
import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.ConfigurationSourceKey.Format;
import org.configureme.sources.ConfigurationSourceKey.Type;
import org.junit.Test;
public class ConfigurableWrapperTest {
	
	@Test public void testEquals(){
		//prepare objects
		ConfigurationSourceKey k1 = new ConfigurationSourceKey(Type.FILE, Format.JSON, "foo");
		ConfigurationSourceKey k2 = new ConfigurationSourceKey(Type.FILE, Format.JSON, "foo");
		ConfigurationSourceKey k3 = new ConfigurationSourceKey(Type.FILE, Format.JSON, "bar");
		
		assertNotSame(k1,k2);
		assertNotSame(k1,k3);
		
		assertEquals(k1, k2);
		assertFalse(k1.equals(k3));
		
		Object c1 = new String("dummy");
		Object c2 = new String("dummy");
		Object c3 = new String("genius");
		
		assertNotSame(c1,c2);
		assertNotSame(c1,c3);
		
		assertEquals(c1, c2);
		assertFalse(c1.equals(c3));
		
		Environment e1 = new DynamicEnvironment("a");
		Environment e2 = new DynamicEnvironment("a");
		Environment e3 = new DynamicEnvironment("b");
		
		assertNotSame(e1,e2);
		assertNotSame(e1,e3);
		
		assertEquals(e1, e2);
		assertFalse(e1.equals(e3));
		
		//// Now the test, first the easy variant
		ConfigurableWrapper w1 = new ConfigurableWrapper(k1, c1, e1);
		ConfigurableWrapper w2 = new ConfigurableWrapper(k2, c2, e2);
		ConfigurableWrapper w3 = new ConfigurableWrapper(k3, c3, e3);
		
		assertNotSame(w1,w2);
		assertNotSame(w1,w3);
		
		assertEquals(w1, w2);
		assertFalse(w1.equals(w3));
		
		assertFalse("equals should check for proper class type", w1.equals(k1));
	}
}
