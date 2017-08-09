package org.configureme.repository;

import org.configureme.ConfigurationManager;
import org.configureme.TestComplexArrays;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ivan Batura
 */
public class ComplexArrayTypeTest {

    @Test
    public void testLists() throws Exception {
        final TestComplexArrays testComplexArrays = new TestComplexArrays();
        ConfigurationManager.INSTANCE.configure(testComplexArrays);
        Assert.assertNotNull("", testComplexArrays.getListString());
        Assert.assertEquals("", 4, testComplexArrays.getListString().size());
        Assert.assertNotNull("", testComplexArrays.getListInteger());
        Assert.assertEquals("", 4, testComplexArrays.getListInteger().size());
        Assert.assertNotNull("", testComplexArrays.getListBoolean());
        Assert.assertEquals("", 3, testComplexArrays.getListBoolean().size());

        Assert.assertNotNull("", testComplexArrays.getListListString());
        Assert.assertEquals("", 3, testComplexArrays.getListListString().size());
        Assert.assertEquals("", 2, testComplexArrays.getListListString().get(0).size());

        Assert.assertNotNull("", testComplexArrays.getSetString());
        Assert.assertEquals("", 3, testComplexArrays.getSetString().size());

        Assert.assertNotNull("", testComplexArrays.getListInteger());
        Assert.assertEquals("", 4, testComplexArrays.getListInteger().size());

        Assert.assertNotNull("", testComplexArrays.getSetBoolean());
        Assert.assertEquals("", 2, testComplexArrays.getSetBoolean().size());

        Assert.assertNotNull("", testComplexArrays.getSetsetString());
        Assert.assertEquals("", 3, testComplexArrays.getSetsetString().size());

        Assert.assertNotNull("", testComplexArrays.getListSetString());
        Assert.assertEquals("", 3, testComplexArrays.getListSetString().size());
    }
}
