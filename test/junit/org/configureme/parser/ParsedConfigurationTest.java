package org.configureme.parser;

import java.util.ArrayList;
import java.util.List;

import org.configureme.GlobalEnvironment;
import org.junit.Test;

import static junit.framework.Assert.*;

public class ParsedConfigurationTest {
	@Test public void basicFunctionality(){
		ParsedConfiguration bla = new ParsedConfiguration("foo");
		assertEquals(bla.getParseTimestamp(), System.currentTimeMillis());
		assertNotNull(bla.toString());

		ParsedAttribute dummy = new ParsedAttribute();
		dummy.setName("test");
		dummy.setValue("test");
		dummy.setEnvironment(GlobalEnvironment.INSTANCE);
		
		List<ParsedAttribute> aList = new ArrayList<ParsedAttribute>();
		aList.add(dummy);
		bla.addAttribute(dummy);
		assertEquals(1, bla.getAttributes().size());
		assertEquals(bla.getAttributes(), aList);
		
		bla.setAttributes(aList);
		assertEquals(1, bla.getAttributes().size());
		assertEquals(bla.getAttributes(), aList);
		
		bla.addAttribute(new ParsedAttribute());
		assertEquals(2, bla.getAttributes().size());
		
		//ensure to string still works
		assertNotNull(bla.toString());
		
	}
	
	@Test public void coverException(){
		//yes, this IS dumb, but the alternative would be to remove the (message) constructor from the exception class, and that would be incovinient for future parser impl
		new ConfigurationParserException("dummy");
	}
}
