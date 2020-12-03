package org.configureme.repository;

import java.util.ArrayList;

import org.configureme.ConfigurationManager;
import org.configureme.TestComplexArrays;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ivan Batura
 */
public class ComplexArrayTypeTest {

    @Test
    public void testLists() {
        final TestComplexArrays testComplexArrays = new TestComplexArrays();
        ConfigurationManager.INSTANCE.configure(testComplexArrays);

        Assert.assertNotNull("ListString is null", testComplexArrays.getListString());
        Assert.assertTrue("ListString is null", testComplexArrays.getListString() instanceof ArrayList);
        Assert.assertEquals("ListString size incorrect", 4, testComplexArrays.getListString().size());

        Assert.assertNotNull("ListInteger is null", testComplexArrays.getListInteger());
        Assert.assertEquals("ListInteger size incorrect", 4, testComplexArrays.getListInteger().size());

        Assert.assertNotNull("ListBoolean is null", testComplexArrays.getListBoolean());
        Assert.assertEquals("ListBoolean size incorrect", 3, testComplexArrays.getListBoolean().size());

        Assert.assertNotNull("ListListString is null", testComplexArrays.getListListString());
        Assert.assertEquals("ListListString size incorrect", 3, testComplexArrays.getListListString().size());
        Assert.assertEquals("ListListString [0] element size incorrect", 2, testComplexArrays.getListListString().get(0).size());

        Assert.assertNotNull("SetString is null", testComplexArrays.getSetString());
        Assert.assertEquals("SetString size incorrect", 3, testComplexArrays.getSetString().size());

        Assert.assertNotNull("ListInteger is null", testComplexArrays.getListInteger());
        Assert.assertEquals("ListInteger size incorrect ", 4, testComplexArrays.getListInteger().size());

        Assert.assertNotNull("SetBoolean is null", testComplexArrays.getSetBoolean());
        Assert.assertEquals("SetBoolean size incorrect", 2, testComplexArrays.getSetBoolean().size());

        Assert.assertNotNull("SetsetString is null", testComplexArrays.getSetsetString());
        Assert.assertEquals("SetsetString size incorrect", 3, testComplexArrays.getSetsetString().size());

        Assert.assertNotNull("SetString is null", testComplexArrays.getListSetString());
        Assert.assertEquals("SetString size incorrect", 3, testComplexArrays.getListSetString().size());

        Assert.assertNotNull("ListObject is null", testComplexArrays.getListObject());
        Assert.assertEquals("ListObject size incorrect", 2, testComplexArrays.getListObject().size());

        Assert.assertNotNull("ListListObject is null", testComplexArrays.getListListObject());
        Assert.assertEquals("ListListObject size incorrect", 2, testComplexArrays.getListListObject().size());

        Assert.assertNotNull("In ListListObject [0] element is null ", testComplexArrays.getListListObject().get(0));
        Assert.assertEquals("In ListListObject [0] element size incorrect", 2, testComplexArrays.getListListObject().get(0).size());

        Assert.assertNotNull("In ListListObject [1] element is null", testComplexArrays.getListListObject().get(1));
        Assert.assertEquals("In ListListObject [1] element size incorrect", 2, testComplexArrays.getListListObject().get(1).size());
    }
}
