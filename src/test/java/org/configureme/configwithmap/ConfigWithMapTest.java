package org.configureme.configwithmap;

import org.configureme.ConfigurationManager;
import org.junit.Assert;
import org.junit.Test;

public class ConfigWithMapTest {
    @Test public void test(){
        ParentConfiguration configuration = new ParentConfiguration();
        ConfigurationManager.INSTANCE.configureAs(configuration, "configwithmap");


        Assert.assertEquals(2, configuration.getChildren().length);
        Assert.assertEquals("d", configuration.getChildren()[0].getData().get("c"));
        Assert.assertEquals("b", configuration.getChildren()[0].getData().get("a"));
    }
}
