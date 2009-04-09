package org.configureme.environments;

import org.configureme.Environment;
import org.configureme.GlobalEnvironment;
import org.junit.Test;

import static org.junit.Assert.*;

public class DynamicEnvironmentTest {
	@Test public void parseForthAndBack(){
		testDynamicEnvironment(GlobalEnvironment.INSTANCE);
		testDynamicEnvironment(new DynamicEnvironment("a", "b", "c"));
	}
	
	private void testDynamicEnvironment(Environment de){
		String s = de.expandedStringForm();
		Environment parsed = DynamicEnvironment.parse(s);
		assertEquals("Parsed environment is not equal to parameter environment", de, parsed);
		assertEquals("Parsed environment expanded form is not equal to parameter environment extended form", s, parsed.expandedStringForm());
	}
}
