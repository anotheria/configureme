package org.configureme;

import org.configureme.annotations.BeforeConfiguration;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.Set;
import org.configureme.annotations.SetAll;
import org.junit.Test;

import static junit.framework.Assert.*;

public class TestForErrors {
	
	
	@Test(expected=IllegalArgumentException.class) public void configureNotConfigurable(){
		Object foo = new Object();
		ConfigurationManager.INSTANCE.configure(foo);
		fail("exception should been thrown");
	}
	
	@Test(expected=IllegalArgumentException.class) public void configureForNotExistantConfiguration(){
		ConfigurationManager.INSTANCE.configure(new FooConfig());
		fail("exception should been thrown");
	}
	
	@Test(expected=IllegalArgumentException.class)  public void configureWithBrokenAnnotations(){
		ConfigurationManager.INSTANCE.configure(new ObjectWithBrokenAnnotation());
		fail("exception should been thrown");
	}
	
	@Test(expected=AssertionError.class)  public void configureWithHiddenAnnotations(){
		ConfigurationManager.INSTANCE.configure(new ObjectWithHiddenAnnotation());
		fail("exception should been thrown");
	}

	@Test(expected=RuntimeException.class) public void configureWithErrorAnnotations(){
		ConfigurationManager.INSTANCE.configure(new ObjectWithErrorAnnotation());
		fail("exception should been thrown");
	}

	@Test public void configureWithExceptionsInSetMethods(){
		ConfigurationManager.INSTANCE.configure(new ObjectWithExceptionsInSetMethods());
		
	}
	
	
	@Test public void configureWithUnsupportedAttributeType(){
		ObjectWithUnsupportedAttribute a = new ObjectWithUnsupportedAttribute();
		ConfigurationManager.INSTANCE.configure(a);
		
		assertTrue("setInt should have been called", a.isSetIntCalled());
		assertFalse("setObject shouldn't have been called", a.isSetObjectCalled());
	}
	
	@Test public void configureWithUnsupportedPublicAttributeType(){
		ObjectWithUnsupportedPublicAttribute a = new ObjectWithUnsupportedPublicAttribute();
		ConfigurationManager.INSTANCE.configure(a);
		
		assertTrue("intValue should have been set", a.isIntValueSet());
		assertFalse("stringValue shouldn't have been set", a.isStringValueSet());
	}

	@Test(expected=IllegalArgumentException.class) public void configureFromBrokenFile(){
		BrokenConfig object = new BrokenConfig(); 
		ConfigurationManager.INSTANCE.configure(object);
		fail("Expect an exception");
	}
	
	@ConfigureMe
	private class FooConfig{
		
	}
	
	@ConfigureMe(name="fixture")
	private class ObjectWithBrokenAnnotation{
		@BeforeConfiguration public void methodWithWrongNumberOfAttributes(String foo){
			
		}
	}

	@ConfigureMe(name="fixture")
	private class ObjectWithHiddenAnnotation{
		@SuppressWarnings("unused") @BeforeConfiguration private void methodWithWrongVisibility(){
			
		}
	}

	@ConfigureMe(name="fixture")
	private class ObjectWithErrorAnnotation{
		@BeforeConfiguration public void methodWithWrongVisibility(){
			throw new RuntimeException("Hello world!");
		}
	}

	@ConfigureMe(name="fixture")
	private class ObjectWithUnsupportedAttribute{
		
		private boolean calledSetInt = false;;
		private boolean calledSetObject = false;
		
		@Set("intValue") public void setInt(int xxx){
			calledSetInt = true;
		}
		
		@Set("intValue") public void setObject(Object xxx){
			calledSetObject= true;
		}
		
		public boolean isSetObjectCalled() { 
			return calledSetObject;
		}

		public boolean isSetIntCalled() { 
			return calledSetInt;
		}
	}
	
	@ConfigureMe(name="fixture")
	private class ObjectWithUnsupportedPublicAttribute{
		
		@Configure public int intValue = 0;
		@Configure public int stringValue = 0; 
		
		public boolean isIntValueSet() { 
			return intValue!=0;
		}

		public boolean isStringValueSet() { 
			return stringValue!=0;
		}
	}

	@ConfigureMe(name="fixture")
	@SuppressWarnings("unused")
	private class ObjectWithUnsupportedMethods{
		
		@Configure private int intValue = 0;
		@Configure private String stringValue = "";
		
		public void setIntValue(){
			//missing parameter
			throw new AssertionError("Can't be called!");
		}
		
		public void setStringValueXXX(String s){
			throw new AssertionError("Method setStringValue is missing!");
		}
		
	}

	@ConfigureMe(name="brokenfixture")
	private class BrokenConfig{
		
	}
	
	@SuppressWarnings("unused")
	@ConfigureMe(name="fixture")
	private class ObjectWithMissingMethods{
		
		@Configure private int intValue = 0;
		//missing method setIntValue
	}
	
	@ConfigureMe(name="fixture")
	private class ObjectWithExceptionsInSetMethods{
		
		@SuppressWarnings("unused") @Configure private int intValue;
		
		@Set("intValue") public void aSetMethod(){
			throw new RuntimeException("Set failed");
		}
		@SetAll public void aSetAllMethod(){
			throw new RuntimeException("Set all failed");
		}
		//missing method setIntValue
		
		public void setIntValue(int aValue){
			throw new RuntimeException("setIntValue");
		}
	}
	


	@Test public void testSingleton(){
		assertEquals("Only one instance allowed", 1, ConfigurationManager.values().length);
		assertSame(ConfigurationManager.INSTANCE, ConfigurationManager.valueOf("INSTANCE"));
	}

}

