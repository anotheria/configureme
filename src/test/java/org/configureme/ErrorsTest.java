package org.configureme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Date;

import org.configureme.annotations.BeforeConfiguration;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.Set;
import org.configureme.annotations.SetAll;
import org.configureme.annotations.SetIf;
import org.configureme.annotations.SetIf.SetIfCondition;
import org.configureme.sources.ConfigurationSourceKey.Format;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ErrorsTest {


	@Test public void configureNotConfigurable(){
		Object foo = new Object();
		assertThrows(IllegalArgumentException.class, () -> ConfigurationManager.INSTANCE.configure(foo));
	}

	@Test public void configureNotConfigurableAsWithKey(){
		Object foo = new Object();
		assertThrows(IllegalArgumentException.class, () -> ConfigurationManager.INSTANCE.configureAs(foo, GlobalEnvironment.INSTANCE, null));
	}

	@Test public void configureNotConfigurableAsWithNameAndFormat(){
		Object foo = new Object();
		assertThrows(IllegalArgumentException.class, () -> ConfigurationManager.INSTANCE.configureAs(foo, GlobalEnvironment.INSTANCE, "foo", Format.JSON));
	}


	@Test public void configureForNotExistantConfiguration(){
		assertThrows(IllegalArgumentException.class, () -> ConfigurationManager.INSTANCE.configure(new FooConfig()));
	}

	@Test public void configureWithBrokenAnnotations(){
		assertThrows(IllegalArgumentException.class, () -> ConfigurationManager.INSTANCE.configure(new ObjectWithBrokenAnnotation()));
	}

	// Vacuously green under JUnit 4: expected=AssertionError.class was satisfied by the trailing
	// fail() (which throws AssertionError), not by configure(). configure() does not actually throw
	// for ObjectWithHiddenAnnotation (an empty @ConfigureMe class). Needs product-behavior review.
	@Disabled("configure() does not throw for ObjectWithHiddenAnnotation; test was a false positive under JUnit 4")
	@Test public void configureWithHiddenAnnotations(){
		assertThrows(AssertionError.class, () -> ConfigurationManager.INSTANCE.configure(new ObjectWithHiddenAnnotation()));
	}

	@Test public void configureWithErrorAnnotations(){
		assertThrows(RuntimeException.class, () -> ConfigurationManager.INSTANCE.configure(new ObjectWithErrorAnnotation()));
	}

	@Test public void configureWithExceptionsInSetMethods(){
		ConfigurationManager.INSTANCE.configure(new ObjectWithExceptionsInSetMethods());

	}


	@Test public void configureWithUnsupportedAttributeType(){
		ObjectWithUnsupportedAttribute a = new ObjectWithUnsupportedAttribute();
		try {
			ConfigurationManager.INSTANCE.configure(a);
		}catch(IllegalArgumentException ee){}

		assertTrue(a.isSetIntCalled(), "setInt should have been called");
		assertFalse(a.isSetDateCalled(), "setObject shouldn't have been called");
	}

	@Test public void configureWithUnsupportedPublicAttributeType(){
		ObjectWithUnsupportedPublicAttribute a = new ObjectWithUnsupportedPublicAttribute();
		ConfigurationManager.INSTANCE.configure(a);

		assertTrue(a.isIntValueSet(), "intValue should have been set");
		assertFalse(a.isStringValueSet(), "stringValue shouldn't have been set");
	}

	@Test public void configureFromBrokenFile(){
		BrokenConfig object = new BrokenConfig();
		assertThrows(IllegalArgumentException.class, () -> ConfigurationManager.INSTANCE.configure(object));
	}

	@Test public void configureWithAttributesMissingInConfig(){
		ObjectWithAttributesMissingInConfig object = new ObjectWithAttributesMissingInConfig();
		assertEquals(100, object.missingValue);
		ConfigurationManager.INSTANCE.configure(object);
		assertEquals(100, object.missingValue);
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
		{

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

		@Set("intValue") public void setObject(Date xxx){
			calledSetObject= true;
		}

		public boolean isSetDateCalled() {
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

	@ConfigureMe(name="fixture")
	private class ObjectWithMissingMethods{

		@Configure private int intValue = 0;
		//missing method setIntValue
	}

	@ConfigureMe(name="fixture")
	private class ObjectWithExceptionsInSetMethods{

		@Configure private int intValue;

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

		@SetIf(value="int",condition=SetIfCondition.startsWith) public void aSetIfMethod(){
			throw new RuntimeException("Set if failed");
		}
}


	@ConfigureMe(name="fixture")
	private class ObjectWithAttributesMissingInConfig{

		@Configure public int missingValue = 100;
		//missing method setIntValue

		@Override
		public String toString(){
			return ""+missingValue;
		}
	}



	@Test public void testSingleton(){
		assertEquals(1, ConfigurationManager.values().length, "Only one instance allowed");
		assertSame(ConfigurationManager.INSTANCE, ConfigurationManager.valueOf("INSTANCE"));
	}

}

