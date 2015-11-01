package org.configureme.cascading;

import org.configureme.ConfigurationManager;
import org.configureme.environments.DynamicEnvironment;
import org.junit.Test;
import static org.junit.Assert.*;

public class CascadingTest {
	@Test public void testCascadingInGlobalEnvironment(){
		Cascading obj = new Cascading();
		ConfigurationManager.INSTANCE.configure(obj, new DynamicEnvironment());
		
		assertEquals(1, obj.getA());
		assertEquals(2, obj.getB());
	}

	@Test public void testCascadingInTestEnvironment(){
		Cascading obj = new Cascading();
		ConfigurationManager.INSTANCE.configure(obj, new DynamicEnvironment("testenv"));
		
		assertEquals(3, obj.getA());
		assertEquals(4, obj.getB());
	}

	@Test public void testCascadingInSubEnvironment(){
		Cascading obj = new Cascading();
		ConfigurationManager.INSTANCE.configure(obj, new DynamicEnvironment("testenv","subenv"));
		
		assertEquals(3, obj.getA());
		assertEquals(4, obj.getB());
		assertEquals(5, obj.getC());
	}
}