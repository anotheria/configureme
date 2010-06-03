package org.configureme.annotations;

import java.util.HashMap;
import java.util.Map;

import org.configureme.ConfigurationManager;
import org.configureme.annotations.SetIf.SetIfCondition;
import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.ConfigurationSourceRegistryTest;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class SetIfTest {
	
	@BeforeClass public static void setupRegistry(){
		//use the other test which can access protected methods
		ConfigurationSourceRegistryTest.setupRegistry();
	}
	
	@Test public void testSetIf(){
		Configurable c = new Configurable();
		ConfigurationManager.INSTANCE.configure(c);
		//test results
		assertEquals(9, c.counts.get("containsvalue").intValue());
		assertEquals(1, c.counts.get("containsint").intValue());
		assertEquals(1, c.counts.get("startsfloat").intValue());
		assertEquals(null, c.counts.get("startsvalue"));
		assertEquals(1, c.counts.get("matchesintvalue").intValue());
	}

	@ConfigureMe(name="fixture", type=ConfigurationSourceKey.Type.FIXTURE, watch=false)
	public static class Configurable{
		
		Map<String, Integer> counts = new HashMap<String, Integer>();
		
		//this method should be called 9 times in the current fixture
		@SetIf(value="Value", condition=SetIfCondition.contains) public void setifcontainsvalue(String key, String value){
			increase("containsvalue");
		}

		//this method should be called 1 times in the current fixture
		@SetIf(value="int", condition=SetIfCondition.contains) public void setifcontainsint(String key, String value){
			increase("containsint");
		}
		
		//this method should be called 1 times in the current fixture
		@SetIf(value="float", condition=SetIfCondition.startsWith) public void setifstartsfloat(String key, String value){
			increase("startsfloat");
		}
		
		//this method should be called 0 times in the current fixture
		@SetIf(value="Value", condition=SetIfCondition.startsWith) public void setifstartsvalue(String key, String value){
			increase("startsvalue");
		}
		
		@SetIf(value="intValue", condition=SetIfCondition.matches) public void setifmatchesintvalue(String key, String value){
			increase("matchesintvalue");
		}

		private void increase(String key){
			Integer old = counts.get(key);
			Integer result = null;
			if (old == null)
				result = Integer.valueOf(1);
			else
				result = Integer.valueOf(old.intValue()+1);
			counts.put(key, result);
		}
	}
}
