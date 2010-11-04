package org.configureme.pojo;

import org.configureme.ConfigurationManager;
import org.configureme.environments.DynamicEnvironment;
import org.junit.Test;
import static org.junit.Assert.*;

public class PojoTest {
//	@ConfigureMe(name="fixture", type=ConfigurationSourceKey.Type.FIXTURE, watch=false)
	@Test public void configurePojo(){
		TestPojo pojo = new TestPojo();
		ConfigurationManager.INSTANCE.configurePojoAs(pojo, "fixture");
		
		assertEquals(1000, pojo.getIntValue());
	}
	
	@Test public void configurePojoIn(){
		DynamicEnvironment env = new DynamicEnvironment("a", "b");
		TestPojo pojo = new TestPojo();
		ConfigurationManager.INSTANCE.configurePojoAsIn(pojo, "fixture", env);
		
		assertEquals(3000, pojo.getIntValue());
		
	}
	
	@Test public void configureBean(){
		TestPojo pojo = new TestPojo();
		ConfigurationManager.INSTANCE.configureBeanAs(pojo, "fixture");
		
		assertEquals(1000, pojo.getIntValue());
	}
	
	@Test public void configureBeanIn(){
		DynamicEnvironment env = new DynamicEnvironment("a", "b");
		TestPojo pojo = new TestPojo();
		ConfigurationManager.INSTANCE.configureBeanAsIn(pojo, "fixture", env);
		
		assertEquals(3000, pojo.getIntValue());
		
	}
	
}
