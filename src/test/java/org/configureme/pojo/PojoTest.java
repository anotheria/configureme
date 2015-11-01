package org.configureme.pojo;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.configureme.ConfigurationManager;
import org.configureme.environments.DynamicEnvironment;
import org.junit.Test;

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

    @Test public void configureNestedBeanTest() {
        DynamicEnvironment env = new DynamicEnvironment("a", "b");
        TestPojo pojo = new TestPojo();
        ConfigurationManager.INSTANCE.configurePojoAsIn(pojo, "fixture", env);

        assertEquals(new InnerTestPojo("foo", true), pojo.getInnerValue());
    }

    @Test public void configureNestedBeansArrayTest() {
        DynamicEnvironment env = new DynamicEnvironment("a", "b");
        TestPojo pojo = new TestPojo();
        ConfigurationManager.INSTANCE.configurePojoAsIn(pojo, "fixture", env);

        assertArrayEquals(new InnerTestPojo[] {
        		new InnerTestPojo("foo", true),
        		new InnerTestPojo("bar", false),
        		new InnerTestPojo("foobar", true)
        	}, pojo.getInnerValueArray());
    }

    @Test public void configureEnum(){
        DynamicEnvironment env = new DynamicEnvironment("a", "b");
        TestPojo pojo = new TestPojo();
        ConfigurationManager.INSTANCE.configureBeanAsIn(pojo, "fixture", env);

        assertEquals(TestPojo.EnumType.B, pojo.getEnumValue());
    }
}

