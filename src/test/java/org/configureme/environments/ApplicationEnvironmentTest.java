package org.configureme.environments;

import java.util.Random;

import org.configureme.Environment;
import org.configureme.GlobalEnvironment;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ApplicationEnvironmentTest {
	
	@Test public void testEquals(){
		ApplicationEnvironment env = new ApplicationEnvironment("a","b","c","d");
		assertFalse(env.equals(null), "equals(null) should return false");
		assertTrue(env.equals(env), "object should be equal to itself");
		assertFalse(env.equals(env.expandedStringForm()), "object should only be equal to objects of same type");
		DynamicEnvironment env2 = new DynamicEnvironment("a","b","c","d");
		assertTrue(env.expandedStringForm().equals(env2.expandedStringForm()), "test failed, since both environments aren't represented by same string");
		assertFalse(env.equals(env2), "test failed, similar environments aren't necessary equal");
		
		Environment reduced = env;
		while (reduced.isReduceable()){
			reduced = reduced.reduce();
			assertFalse(env.equals(reduced), "Environemnt shouldn't be equal to its reduced version");
		}
		assertTrue(reduced.equals(GlobalEnvironment.INSTANCE));
		assertFalse(env.equals(GlobalEnvironment.INSTANCE));
	}
	
	@Test public void testBuilder(){
		Random rnd = new Random(System.nanoTime());
		String system = "system"+rnd.nextInt(1000);
		String app = "app"+rnd.nextInt(1000);
		String service = "service"+rnd.nextInt(1000);
		String host = "host"+rnd.nextInt(1000);
		
		ApplicationEnvironment env = new ApplicationEnvironment.Builder().system(system).app(app).service(service).host(host).build();
		ApplicationEnvironment reversedOrder = new ApplicationEnvironment.Builder().host(host).app(app).service(service).system(system).build();
		ApplicationEnvironment fromPublicConstructor = new ApplicationEnvironment(system, app, service, host );
		
		assertEquals(env, reversedOrder, "builder must be build order resistent");
		assertEquals(system, env.getSystem(), "system doesn't match");
		assertEquals(app, env.getApp(), "app doesn't match");
		assertEquals(host, env.getHost(), "host doesn't match");
		assertEquals(service, env.getService(), "service doesn't match");
		assertEquals(env, fromPublicConstructor, "builder must be produce same result as the public constructor");
		
	}
	
	@Test public void testReduce(){
		String a = "a";
		ApplicationEnvironment e1 = new ApplicationEnvironment(a,a,a,a);
		ApplicationEnvironment e2 = new ApplicationEnvironment(a,a,a,"");
		ApplicationEnvironment e3 = new ApplicationEnvironment(a,a,"","");
		ApplicationEnvironment e4 = new ApplicationEnvironment(a,"","","");
		
		String stringform = a+"_"+a+"_"+a+"_"+a;
		
		assertEquals(stringform, e1.expandedStringForm());
		
		assertTrue(e1.isReduceable(), "Environment should be reduceable");
		assertTrue(e2.isReduceable(), "Environment should be reduceable");
		assertTrue(e3.isReduceable(), "Environment should be reduceable");
		assertTrue(e4.isReduceable(), "Environment should be reduceable");

		Environment env = null;
		env = e1.reduce();
		assertEquals(e2, env);
		
		env = e2.reduce();
		assertEquals(e3, env);

		env = e3.reduce();
		assertEquals(e4, env);

		env = e4.reduce();
		assertEquals(GlobalEnvironment.INSTANCE, env);
	}
	
	@Test public void testInvalidObject(){
		ApplicationEnvironment env = new ApplicationEnvironment("","","","");
		assertFalse(env.isReduceable());
		//this must throw an error
		assertThrows(AssertionError.class, () -> env.reduce());
	}

	@Test public void testInvalidObject2(){
		ApplicationEnvironment env = new ApplicationEnvironment(null,null,null,null);
		assertFalse(env.isReduceable());
		//this must throw an error
		assertThrows(AssertionError.class, () -> env.reduce());
	}
}
