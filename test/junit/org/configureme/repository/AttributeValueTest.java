package org.configureme.repository;

import org.apache.log4j.BasicConfigurator;
import org.configureme.Environment;
import org.configureme.environments.DynamicEnvironment;
import org.junit.Test;
import static junit.framework.Assert.*;

public class AttributeValueTest {
	
	static{
		BasicConfigurator.configure();
	}
	
	@Test public void testEnvironments(){
		Environment a = new DynamicEnvironment("a");
		Environment b = new DynamicEnvironment("a", "b");
		Environment c = new DynamicEnvironment("a", "b", "c");
		Environment d = new DynamicEnvironment("a", "b", "c", "d");
		
		AttributeValue v = new AttributeValue();
		
		v.set("a", a);
		v.set("b", b);
		v.set("c", c);
		v.set("d", d);
		
		assertEquals("a", v.get(a));
		assertEquals("b", v.get(b));
		assertEquals("c", v.get(c));
		assertEquals("d", v.get(d));
		
		assertTrue(v.toString().length()>0);
	}
	
	@Test public void testFallback(){
		Environment a = new DynamicEnvironment("a");
		Environment d = new DynamicEnvironment("a", "b", "c", "d");
		
		AttributeValue v = new AttributeValue();
		
		assertTrue(v.get(d)==null);

		String emptyAttr = v.toString();
		assertFalse(emptyAttr==null);
		assertFalse(emptyAttr.equals(""));
		v.set("a", a);
		assertEquals("a", v.get(d));
		assertFalse(emptyAttr.equals(v.toString()));
		
	}
}
