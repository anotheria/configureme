package org.configureme.repository;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.apache.log4j.BasicConfigurator;
import org.configureme.Environment;
import org.configureme.environments.DynamicEnvironment;
import org.junit.Test;

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

		v.set(new PlainValue("a"), a);
		v.set(new PlainValue("b"), b);
		v.set(new PlainValue("c"), c);
		v.set(new PlainValue("d"), d);

		assertEquals(new PlainValue("a"), v.get(a));
		assertEquals(new PlainValue("b"), v.get(b));
		assertEquals(new PlainValue("c"), v.get(c));
		assertEquals(new PlainValue("d"), v.get(d));

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
		v.set(new PlainValue("a"), a);
		assertEquals(new PlainValue("a"), v.get(d));
		assertFalse(emptyAttr.equals(v.toString()));

	}
}
