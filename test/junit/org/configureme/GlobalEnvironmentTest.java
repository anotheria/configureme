package org.configureme;

import org.junit.Test;
import static junit.framework.Assert.*;

public class GlobalEnvironmentTest {
	@Test(expected=AssertionError.class) public void basicTest(){
		Environment e = GlobalEnvironment.INSTANCE;
		
		assertEquals("global environment has no string form", "", e.expandedStringForm());
		
		assertFalse("GlobalEnvironment can't be reduced", e.isReduceable());
		e.reduce();
		fail("GlobalEnvironment can't be reduced");
	}
}
