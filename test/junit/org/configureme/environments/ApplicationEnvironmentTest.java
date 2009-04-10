package org.configureme.environments;

import java.util.Random;

import org.configureme.Environment;
import org.configureme.GlobalEnvironment;
import org.junit.Test;
import static junit.framework.Assert.*;

public class ApplicationEnvironmentTest {
	
	@Test public void testEquals(){
		ApplicationEnvironment env = new ApplicationEnvironment("a","b","c","d");
		assertFalse("equals(null) should return false", env.equals(null));
		assertTrue("object should be equal to itself", env.equals(env));
		assertFalse("object should only be equal to objects of same type", env.equals(env.expandedStringForm()));
		DynamicEnvironment env2 = new DynamicEnvironment("a","b","c","d");
		assertTrue("test failed, since both environments aren't represented by same string", env.expandedStringForm().equals(env2.expandedStringForm()));
		assertFalse("test failed, similar environments aren't necessary equal", env.equals(env2));
		
		Environment reduced = env;
		while (reduced.isReduceable()){
			reduced = reduced.reduce();
			assertFalse("Environemnt shouldn't be equal to its reduced version", env.equals(reduced));
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
		
		assertEquals("builder must be build order resistent", env, reversedOrder);
		assertEquals("system doesn't match", system, env.getSystem());
		assertEquals("app doesn't match", app, env.getApp());
		assertEquals("host doesn't match", host, env.getHost());
		assertEquals("service doesn't match", service, env.getService());
		assertEquals("builder must be produce same result as the public constructor", env, fromPublicConstructor);
		
	}
	
	@Test public void testReduce(){
		String a = "a";
		ApplicationEnvironment e1 = new ApplicationEnvironment(a,a,a,a);
		ApplicationEnvironment e2 = new ApplicationEnvironment(a,a,a,"");
		ApplicationEnvironment e3 = new ApplicationEnvironment(a,a,"","");
		ApplicationEnvironment e4 = new ApplicationEnvironment(a,"","","");
		
		String stringform = a+"_"+a+"_"+a+"_"+a;
		
		assertEquals(stringform, e1.expandedStringForm());
		
		assertTrue("Environment should be reduceable", e1.isReduceable());
		assertTrue("Environment should be reduceable", e2.isReduceable());
		assertTrue("Environment should be reduceable", e3.isReduceable());
		assertTrue("Environment should be reduceable", e4.isReduceable());

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
	
	@Test(expected=AssertionError.class) public void testInvalidObject(){
		ApplicationEnvironment env = new ApplicationEnvironment("","","","");
		assertFalse(env.isReduceable());
		//this must throw an error
		env.reduce();
		fail("An error should be thrown if trying to reduce unreduceable environment: "+env);
	}

	@Test(expected=AssertionError.class) public void testInvalidObject2(){
		ApplicationEnvironment env = new ApplicationEnvironment(null,null,null,null);
		assertFalse(env.isReduceable());
		//this must throw an error
		env.reduce();
		fail("An error should be thrown if trying to reduce unreduceable environment: "+env);
	}
}
