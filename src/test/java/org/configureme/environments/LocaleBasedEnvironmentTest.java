package org.configureme.environments;

import java.util.Locale;

import org.configureme.Environment;
import org.configureme.GlobalEnvironment;
import org.junit.Test;
import static org.junit.Assert.*;

public class LocaleBasedEnvironmentTest {
	
	@Test(expected=AssertionError.class) public void testForReduceError(){
		LocaleBasedEnvironment empty = new LocaleBasedEnvironment(new Locale("","",""));
		assertFalse(empty.isReduceable());
		empty.reduce();
		fail("assertion error should be thrown by reducing not reduceble environment");
	}
	
	@Test public void testEquals(){
		LocaleBasedEnvironment env1 = new LocaleBasedEnvironment(new Locale("de", "DE", "munich"));
		LocaleBasedEnvironment env2 = new LocaleBasedEnvironment(new Locale("de", "DE", "munich"));
		LocaleBasedEnvironment env3 = new LocaleBasedEnvironment(new Locale("de", "DE", "cologne"));
		
		assertEquals("object must be equal to itself", env1, env1);
		assertEquals("object must be equal to itself", env2, env2);
		assertEquals("object must be equal to itself", env3, env3);
		
		assertEquals("object must be equal to the same object", env1, env2);
		assertFalse(env1.equals(env3));
		assertFalse(env1.equals(null));
	}
	
	@Test public void testHashCode(){
		LocaleBasedEnvironment env1 = new LocaleBasedEnvironment(new Locale("de", "DE", "munich"));
		LocaleBasedEnvironment env2 = new LocaleBasedEnvironment(new Locale("de", "DE", "munich"));
		LocaleBasedEnvironment env3 = new LocaleBasedEnvironment(new Locale("de", "DE", "cologne"));
		
		assertEquals("object must have the same hashcode as itself", env1.hashCode(), env1.hashCode());
		assertEquals("object must be equal to itself", env2.hashCode(), env2.hashCode());
		assertEquals("object must be equal to itself", env3.hashCode(), env3.hashCode());
		
		assertEquals("objects must have same hashCode is they are equal", env1.equals(env2), env1.hashCode()==env2.hashCode());
		assertEquals("objects must have same hashCode is they are equal", env1.equals(env3), env1.hashCode()==env3.hashCode());
	}

	@Test public void testEmptyVariant(){
		  String country = "DE";
		  String language = "de";
		  String variant1 = " ";
		  String variant2 = "";
		  
		  LocaleBasedEnvironment env1 = new LocaleBasedEnvironment(new Locale(language, country, variant1));
		  LocaleBasedEnvironment env2 = new LocaleBasedEnvironment(new Locale(language, country, variant2));

		  LocaleBasedEnvironment env3 = new LocaleBasedEnvironment.Builder().language(language).country(country).variant(variant1).build();
		  LocaleBasedEnvironment env4 = new LocaleBasedEnvironment.Builder().language(language).country(country).variant(variant2).build();


		  assertEquals(env1, env2);
		  assertEquals(env1.reduce(), env2.reduce());

		  assertEquals(env3, env4);
		  assertEquals(env3.reduce(), env4.reduce());

		  //test cross
		  assertEquals(env1, env3);
		  assertEquals(env2.reduce(), env4.reduce());
}
	
	@Test public void testReduce(){
		  String country = "DE";
		  String language = "de";
		  String variant = "bavaria_munich_trudering_forest";
		  
		  LocaleBasedEnvironment env = new LocaleBasedEnvironment(new Locale(language, country, variant));
		  Environment toreduce = env;
		  while(toreduce.isReduceable()){
			  Environment reduced = toreduce.reduce();
			  assertFalse("Reduced environment shouldn't be equals to the parent environment", toreduce.equals(reduced));
			  toreduce = reduced;
		  }
		  
		  assertSame("final reduced environment must be the global environment", toreduce, GlobalEnvironment.INSTANCE); 
		  
		
	}
	
	@Test public void testBuilder(){
		  String country = "DE";
		  String language = "de";
		  String variant1 = "bavaria";
		  String variant2 = "bavaria_munich";
		  
		  Locale testLocale1 = new Locale(language, country, variant1);
		  Locale testLocale2 = new Locale(language, country, variant2);
		  
		  assertFalse(testLocale1.equals(testLocale2));
		  
		  LocaleBasedEnvironment l1a = new LocaleBasedEnvironment(testLocale1);
		  LocaleBasedEnvironment l2a = new LocaleBasedEnvironment(testLocale2);
		  
		  assertFalse(l1a.equals(l2a));
		  
		  LocaleBasedEnvironment l1b = new LocaleBasedEnvironment.Builder().language(language).country(country).variant(variant1).build();
		  LocaleBasedEnvironment l2b = new LocaleBasedEnvironment.Builder().language(language).country(country).variant(variant2).build();
		  assertFalse(l1b.equals(l2b));
		  
		  LocaleBasedEnvironment l1c = new LocaleBasedEnvironment.Builder(testLocale1).build();
		  LocaleBasedEnvironment l2c = new LocaleBasedEnvironment.Builder(testLocale2).build();
		  assertFalse(l1c.equals(l2c));

		  assertEquals(l1a, l1b);
		  assertEquals(l1a, l1c);
		  assertEquals(l1b, l1c);
		  
		  assertEquals(l2a, l2b);
		  assertEquals(l2a, l2c);
		  assertEquals(l2b, l2c);
		  
		  assertEquals(testLocale1.toString(), l1a.expandedStringForm());
		  assertEquals(testLocale1.toString(), l1b.expandedStringForm());
		  assertEquals(testLocale1.toString(), l1c.expandedStringForm());
		  assertEquals(testLocale2.toString(), l2a.expandedStringForm());
		  assertEquals(testLocale2.toString(), l2b.expandedStringForm());
		  assertEquals(testLocale2.toString(), l2c.expandedStringForm());
	}
}
