package org.configureme.environments;

import org.configureme.Environment;
import org.configureme.GlobalEnvironment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
		DynamicEnvironment de4 = de1.add("dummy"); de4.reduceThis();
		DynamicEnvironment de5 = (DynamicEnvironment)de1.clone(); de5.extendThis("a"); de5.reduceThis();
		
		assertEquals(de1, de1, "Object must be equal to itself");
		assertEquals(de1, de2, "Object must be equal to the same object");
		assertEquals(de1, de3, "Object must be equal to the cloned object");
		assertEquals(de1, de4, "Object must be equal to the reduced object");
		assertEquals(de1, de5, "Object must be equal to the extended and reduced object");
		assertFalse(de1.equals(GlobalEnvironment.INSTANCE), "Object must not be equal to GlobalEnvironment :");
		
		assertTrue(de1.isReduceable());
		assertTrue(de2.isReduceable());
		assertTrue(de3.isReduceable());
		assertTrue(de4.isReduceable());
		assertTrue(de5.isReduceable());
	}
	
	private void testDynamicEnvironment(Environment de){
		String s = de.expandedStringForm();
		Environment parsed = DynamicEnvironment.parse(s);
		assertEquals(de, parsed, "Parsed environment is not equal to parameter environment");
		assertEquals(s, parsed.expandedStringForm(), "Parsed environment expanded form is not equal to parameter environment extended form");
	} 
	
	@Test public void reduceUnreduceable(){
		DynamicEnvironment empty = new DynamicEnvironment();
		assertFalse(empty.isReduceable());
		assertThrows(AssertionError.class, () -> empty.reduceThis());
	}
}
