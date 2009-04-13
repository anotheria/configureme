package org.configureme;

import org.configureme.annotations.BeforeConfiguration;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.Set;
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
	
	@Test public void configureWithUnsupportedAttributeType(){
		ObjectWithUnsupportedAttribute a = new ObjectWithUnsupportedAttribute();
		ConfigurationManager.INSTANCE.configure(a);
		
		assertTrue("setInt should have been called", a.isSetIntCalled());
		assertFalse("setObject shouldn't have been called", a.isSetObjectCalled());
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
	
	@ConfigureMe(name="brokenfixture")
	private class BrokenConfig{
		
	}

}

