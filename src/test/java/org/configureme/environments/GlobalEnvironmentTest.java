package org.configureme.environments;

import org.configureme.Environment;
import org.configureme.GlobalEnvironment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalEnvironmentTest {
	@Test public void testInstance(){
		Environment e1 = GlobalEnvironment.INSTANCE;
		Environment e2 = DynamicEnvironment.parse("");
		Environment e3 = GlobalEnvironment.valueOf("INSTANCE");
		assertEquals(e1, e2);
		assertSame(e1, e2);

		assertEquals(e1, e3);
		assertSame(e1, e3);
		
		assertEquals(e2, e3);
		assertSame(e2, e3);

}
	
	@Test public void testReduceability(){
		assertFalse(GlobalEnvironment.INSTANCE.isReduceable());
		assertThrows(AssertionError.class, () -> GlobalEnvironment.INSTANCE.reduce());
	}
	
	@Test public void testString(){
		assertEquals(GlobalEnvironment.INSTANCE.toString(), "global");
		assertEquals(GlobalEnvironment.INSTANCE.expandedStringForm(), "");
	}
	
	@Test public void whatElse(){
		GlobalEnvironment env[] = GlobalEnvironment.values();
		assertEquals(1, env.length, "The can be only one global environment");
		
		
	}
}
