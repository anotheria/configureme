package org.configureme.parser.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.anotheria.util.StringUtils;

import org.configureme.parser.ConfigurationParserException;
import org.configureme.parser.ParsedAttribute;
import org.configureme.parser.ParsedConfiguration;
import org.configureme.parser.StringArrayParser;
import org.junit.Test;


public class JsonParserTest {
	
	
	
	@Test public void basicFunctionality()throws ConfigurationParserException{
		
		
		
		JsonParser parser = new JsonParser();
		ParsedConfiguration bla;
//		try{
//			//Testing void configuration: no any json statements
//			bla = parser.parseConfiguration("foo", "");
//			assertEquals("Void configuration must be empty ParsedConfiguration!",0, bla.getAttributes().size());
//		}catch(NullPointerException e){
//			fail("Must not throws exception on empty configuration!");
//		}
		
		try{
			//Testing empty configuration: only empty JSON Object
			bla = parser.parseConfiguration("foobar", "{}");
			assertEquals("For empty configuration must be empty ParsedConfiguration!",0, bla.getAttributes().size());
		}catch(NullPointerException e){
			fail("Must not throws exception on empty configuration!");
		}
		
		try{
			//Testing empty configuration with cascades
			bla = parser.parseConfiguration("foo", "{dev:{},test:{}}");
			assertEquals("For empty configuration must be empty ParsedConfiguration!",0, bla.getAttributes().size());
		}catch(NullPointerException e){
			fail("Must not throws exception on empty configuration!");
		}
				
		ParsedAttribute attr;
		
		//Testing plain configuration
		bla = parser.parseConfiguration("foobar", "{foo:bar, moo:zar}");
		assertEquals(2, bla.getAttributes().size());
		
		Map<String, ParsedAttribute> attrsMap = createAttributesMap(bla.getAttributes());
		
		attr = attrsMap.get("foo");
		assertEquals("bar", attr.getValue());
		assertTrue(StringUtils.isEmpty(attr.getEnvironment().expandedStringForm()));
		attr = attrsMap.get("moo");
		assertEquals("zar", attr.getValue());
		assertTrue(StringUtils.isEmpty(attr.getEnvironment().expandedStringForm()));
		
		
		//Testing cascaded configuration
		bla = parser.parseConfiguration("foobar", "{foo:bar,dev:{dev_foo:dev_bar},test:{test_foo:test_bar}}");
		assertEquals(3, bla.getAttributes().size());
		attrsMap = createAttributesMap(bla.getAttributes());
		
		attr = attrsMap.get("foo");
		assertEquals("bar", attr.getValue());
		assertTrue(StringUtils.isEmpty(attr.getEnvironment().expandedStringForm()));

		attr = attrsMap.get("dev_foo");
		assertEquals("dev_bar", attr.getValue());
		assertEquals("dev", attr.getEnvironment().expandedStringForm());

		attr = attrsMap.get("test_foo");
		assertEquals("test_bar", attr.getValue());
		assertEquals("test", attr.getEnvironment().expandedStringForm());		
		
		//Testing arrays support
		// String array v1
		bla = parser.parseConfiguration("foobar", "{foo: [\"s1\", \" s2\"]}");
		attrsMap = createAttributesMap(bla.getAttributes());
		assertEquals("s1, s2", attrsMap.get("foo").getValue());
		// String array v2
		bla = parser.parseConfiguration("foobar", "{foo: [s1, s2]}");
		attrsMap = createAttributesMap(bla.getAttributes());
		assertEquals("s1,s2", attrsMap.get("foo").getValue());
		// String array incl comma
		bla = parser.parseConfiguration("foobar", "{foo: [\"s1.1 , s1.2\", \" s2\"]}");
		attrsMap = createAttributesMap(bla.getAttributes());
		assertEquals("s1.1 " + StringArrayParser.STRING_ARRAY_DELIM_CODE + " s1.2, s2", attrsMap.get("foo").getValue());
		// Int array
		bla = parser.parseConfiguration("foobar", "{foo: [19, 50]}");
		attrsMap = createAttributesMap(bla.getAttributes());
		assertEquals("19,50", attrsMap.get("foo").getValue());
		// Float array
		bla = parser.parseConfiguration("foobar", "{foo: [19.5, 50.02]}");
		attrsMap = createAttributesMap(bla.getAttributes());
		assertEquals("19.5,50.02", attrsMap.get("foo").getValue());
		// Boolean array
		bla = parser.parseConfiguration("foobar", "{foo: [true,  false]}");
		attrsMap = createAttributesMap(bla.getAttributes());
		assertEquals("true,false", attrsMap.get("foo").getValue());
		// Test Empty array
		bla = parser.parseConfiguration("foobar", "{foo: []}");
		attrsMap = createAttributesMap(bla.getAttributes());
		assertEquals("", attrsMap.get("foo").getValue());
		// Test Array with environment
		bla = parser.parseConfiguration("foobar", "{dev:{dev_foo: [d1, d2]}, test:{test_foo: [t1, t2]}}");
		attrsMap = createAttributesMap(bla.getAttributes());
		assertEquals("d1,d2", attrsMap.get("dev_foo").getValue());
		assertEquals("dev", attrsMap.get("dev_foo").getEnvironment().expandedStringForm());
		assertEquals("t1,t2", attrsMap.get("test_foo").getValue());
		assertEquals("test", attrsMap.get("test_foo").getEnvironment().expandedStringForm());
	}
	
	private static Map<String, ParsedAttribute> createAttributesMap(List<ParsedAttribute> attrs){
		Map<String, ParsedAttribute> ret = new HashMap<String, ParsedAttribute>(attrs.size());
		for(ParsedAttribute a: attrs){
			if(ret.containsKey(a.getName()))
				fail("Please to simplify testing do not use duplicate configuration property even in different cascades!");
			ret.put(a.getName(), a);
		}
		return ret;
	}

}
