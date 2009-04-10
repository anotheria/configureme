package org.configureme.environments;

import org.configureme.Environment;
import org.configureme.GlobalEnvironment;
import org.junit.Test;

import static org.junit.Assert.*;

public class DynamicEnvironmentTest {
	@Test public void parseForthAndBack(){
		testDynamicEnvironment(GlobalEnvironment.INSTANCE);
		testDynamicEnvironment(new DynamicEnvironment("a")); 
		testDynamicEnvironment(new DynamicEnvironment("a", "b", "c"));
	}
	
	@Test public void testEquals(){
		DynamicEnvironment de1 = new DynamicEnvironment("a");
		DynamicEnvironment de2 = new DynamicEnvironment("a");
		DynamicEnvironment de3 = (DynamicEnvironment)de1.clone();
		DynamicEnvironment de4 = de1.reduce(); de4.add("a");
		DynamicEnvironment de5 = (DynamicEnvironment)de1.clone(); de5.extendThis("a"); de5.reduceThis();
		
		assertEquals("Object must be equal to itself", de1, de1);
		assertEquals("Object must be equal to the same object", de1, de2);
		assertEquals("Object must be equal to the cloned object", de1, de3);
		assertEquals("Object must be equal to the reduced object", de1, de4);
		assertEquals("Object must be equal to the extended and reduced object", de1, de5);
		assertFalse("Object must not be equal to GlobalEnvironment :", de1.equals(GlobalEnvironment.INSTANCE));
		
		assertTrue(de1.isReduceable());
		assertTrue(de2.isReduceable());
		assertTrue(de3.isReduceable());
		assertTrue(de4.isReduceable());
		assertTrue(de5.isReduceable());
	}
	
	private void testDynamicEnvironment(Environment de){
		String s = de.expandedStringForm();
		Environment parsed = DynamicEnvironment.parse(s);
		assertEquals("Parsed environment is not equal to parameter environment", de, parsed);
		assertEquals("Parsed environment expanded form is not equal to parameter environment extended form", s, parsed.expandedStringForm());
	} 
}
