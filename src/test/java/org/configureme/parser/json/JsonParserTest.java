package org.configureme.parser.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.configureme.parser.ConfigurationParserException;
import org.configureme.parser.ParsedAttribute;
import org.configureme.parser.ParsedConfiguration;
import org.configureme.repository.ArrayValue;
import org.configureme.repository.PlainValue;
import org.configureme.repository.Value;
import org.configureme.util.StringUtils;
import org.junit.Test;


public class JsonParserTest {


    @Test
    public void basicFunctionality() throws ConfigurationParserException {


        JsonParser parser = new JsonParser();
        ParsedConfiguration bla;
//		try{
//			//Testing void configuration: no any json statements
//			bla = parser.parseConfiguration("foo", "");
//			assertEquals("Void configuration must be empty ParsedConfiguration!",0, bla.getAttributes().size());
//		}catch(NullPointerException e){
//			fail("Must not throws exception on empty configuration!");
//		}

        try {
            //Testing empty configuration: only empty JSON Object
            bla = parser.parseConfiguration("foobar", "{}");
            assertEquals("For empty configuration must be empty ParsedConfiguration!", 0, bla.getAttributes().size());
        } catch (NullPointerException e) {
            fail("Must not throws exception on empty configuration!");
        }

        try {
            //Testing empty configuration with cascades
            bla = parser.parseConfiguration("foo", "{dev:{},test:{}}");
            assertEquals("For empty configuration must be empty ParsedConfiguration!", 0, bla.getAttributes().size());
        } catch (NullPointerException e) {
            fail("Must not throws exception on empty configuration!");
        }

        ParsedAttribute<?> attr;

        //Testing plain configuration
        bla = parser.parseConfiguration("foobar", "{foo:bar, moo:zar}");
        assertEquals(2, bla.getAttributes().size());

        Map<String, ParsedAttribute<?>> attrsMap = createAttributesMap(bla.getAttributes());

        attr = attrsMap.get("foo");
        assertEquals(new PlainValue("bar"), attr.getValue());
        assertTrue(StringUtils.isEmpty(attr.getEnvironment().expandedStringForm()));
        attr = attrsMap.get("moo");
        assertEquals(new PlainValue("zar"), attr.getValue());
        assertTrue(StringUtils.isEmpty(attr.getEnvironment().expandedStringForm()));


        //Testing cascaded configuration
        bla = parser.parseConfiguration("foobar", "{foo:bar,dev:{dev_foo:dev_bar},test:{test_foo:test_bar}}");
        assertEquals(3, bla.getAttributes().size());
        attrsMap = createAttributesMap(bla.getAttributes());

        attr = attrsMap.get("foo");
        assertEquals(new PlainValue("bar"), attr.getValue());
        assertTrue(StringUtils.isEmpty(attr.getEnvironment().expandedStringForm()));

        attr = attrsMap.get("dev_foo");
        assertEquals(new PlainValue("dev_bar"), attr.getValue());
        assertEquals("dev", attr.getEnvironment().expandedStringForm());

        attr = attrsMap.get("test_foo");
        assertEquals(new PlainValue("test_bar"), attr.getValue());
        assertEquals("test", attr.getEnvironment().expandedStringForm());

        //Testing arrays support
        // String array v1
        bla = parser.parseConfiguration("foobar", "{foo: [\"s1\", \" s2\"]}");
        attrsMap = createAttributesMap(bla.getAttributes());
        assertEquals(new ArrayValue(Arrays.<Value>asList(new PlainValue("s1"), new PlainValue(" s2"))), attrsMap.get("foo").getValue());
        // String array v2
        bla = parser.parseConfiguration("foobar", "{foo: [s1, s2]}");
        attrsMap = createAttributesMap(bla.getAttributes());
        assertEquals(new ArrayValue(Arrays.<Value>asList(new PlainValue("s1"), new PlainValue("s2"))), attrsMap.get("foo").getValue());
        // Int array
        bla = parser.parseConfiguration("foobar", "{foo: [19, 50]}");
        attrsMap = createAttributesMap(bla.getAttributes());
        assertEquals(new ArrayValue(Arrays.<Value>asList(new PlainValue("19"), new PlainValue("50"))), attrsMap.get("foo").getValue());
        // Float array
        bla = parser.parseConfiguration("foobar", "{foo: [19.5, 50.02]}");
        attrsMap = createAttributesMap(bla.getAttributes());
        assertEquals(new ArrayValue(Arrays.<Value>asList(new PlainValue("19.5"), new PlainValue("50.02"))), attrsMap.get("foo").getValue());
        // Boolean array
        bla = parser.parseConfiguration("foobar", "{foo: [true,  false]}");
        attrsMap = createAttributesMap(bla.getAttributes());
        assertEquals(new ArrayValue(Arrays.<Value>asList(new PlainValue("true"), new PlainValue("false"))), attrsMap.get("foo").getValue());
        // Test Empty array
        bla = parser.parseConfiguration("foobar", "{foo: []}");
        attrsMap = createAttributesMap(bla.getAttributes());
        assertEquals(new ArrayValue(Collections.<Value>emptyList()), attrsMap.get("foo").getValue());
        // Test Array with environment
        bla = parser.parseConfiguration("foobar", "{dev:{dev_foo: [d1, d2]}, test:{test_foo: [t1, t2]}}");
        attrsMap = createAttributesMap(bla.getAttributes());
        assertEquals(new ArrayValue(Arrays.<Value>asList(new PlainValue("d1"), new PlainValue("d2"))), attrsMap.get("dev_foo").getValue());
        assertEquals("dev", attrsMap.get("dev_foo").getEnvironment().expandedStringForm());
        assertEquals(new ArrayValue(Arrays.<Value>asList(new PlainValue("t1"), new PlainValue("t2"))), attrsMap.get("test_foo").getValue());
        assertEquals("test", attrsMap.get("test_foo").getEnvironment().expandedStringForm());
    }

    @Test
    public void parseConfigurationWithSystemProperty() throws ConfigurationParserException {
        JsonParser parser = new JsonParser();

        System.setProperty("MY_SYSTEM_ENV", "MY_VALUE");

        ParsedConfiguration config = parser.parseConfiguration("foobar", "{foo: \"${MY_SYSTEM_ENV}\"}");
        Map<String, ParsedAttribute<?>> attrsMap = createAttributesMap(config.getAttributes());
        ParsedAttribute<?> attr = attrsMap.get("foo");

        assertEquals(new PlainValue("MY_VALUE"), attr.getValue());
    }

    @Test
    public void parseConfigurationWithUndefinedSystemProperty() throws ConfigurationParserException {
        JsonParser parser = new JsonParser();

        ParsedConfiguration config = parser.parseConfiguration("foobar", "{foo: \"${MY_OTHER_SYSTEM_ENV}\"}");
        Map<String, ParsedAttribute<?>> attrsMap = createAttributesMap(config.getAttributes());
        ParsedAttribute<?> attr = attrsMap.get("foo");

        assertEquals(new PlainValue("${MY_OTHER_SYSTEM_ENV}"), attr.getValue());
    }

    @Test
    public void parseConfigurationWithUndefinedSystemPropertyWithFallback() throws ConfigurationParserException {
        JsonParser parser = new JsonParser();

        ParsedConfiguration config = parser.parseConfiguration("foobar", "{foo: \"${MY_UNDEFINED_KEY:baz}\"}");
        Map<String, ParsedAttribute<?>> attrsMap = createAttributesMap(config.getAttributes());
        ParsedAttribute<?> attr = attrsMap.get("foo");

        assertEquals(new PlainValue("baz"), attr.getValue());
    }

    private static Map<String, ParsedAttribute<?>> createAttributesMap(List<ParsedAttribute<?>> attrs) {
        Map<String, ParsedAttribute<?>> ret = new HashMap<String, ParsedAttribute<?>>(attrs.size());
        for (ParsedAttribute<?> a : attrs) {
            if (ret.containsKey(a.getName()))
                fail("Please to simplify testing do not use duplicate configuration property even in different cascades!");
            ret.put(a.getName(), a);
        }
        return ret;
    }

}
