package org.configureme.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.configureme.GlobalEnvironment;
import org.junit.jupiter.api.Test;

public class ParsedConfigurationTest {
	@Test public void basicFunctionality(){
		ParsedConfiguration bla = new ParsedConfiguration("foo");
		assertTrue(bla.getParseTimestamp() <= System.currentTimeMillis());
		assertNotNull(bla.toString());

		ParsedAttribute dummy = new PlainParsedAttribute("test", GlobalEnvironment.INSTANCE, "test");

		List<ParsedAttribute<?>> aList = new ArrayList<ParsedAttribute<?>>();
		aList.add(dummy);
		bla.addAttribute(dummy);
		assertEquals(1, bla.getAttributes().size());
		assertEquals(bla.getAttributes(), aList);

		bla.setAttributes(aList);
		assertEquals(1, bla.getAttributes().size());
		assertEquals(bla.getAttributes(), aList);

		bla.addAttribute(new PlainParsedAttribute(null, null, null));
		assertEquals(2, bla.getAttributes().size());

		//ensure to string still works
		assertNotNull(bla.toString());

	}

	@Test public void coverException(){
		//yes, this IS dumb, but the alternative would be to remove the (message) constructor from the exception class, and that would be incovinient for future parser impl
		new ConfigurationParserException("dummy");
	}
}
