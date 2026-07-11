package org.configureme.configwithmap;

import org.configureme.ConfigurationManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigWithMapTest {
    @Test public void test(){
        ParentConfiguration configuration = new ParentConfiguration();
        ConfigurationManager.INSTANCE.configureAs(configuration, "configwithmap");


        Assertions.assertEquals(2, configuration.getChildren().length);
        Assertions.assertEquals("d", configuration.getChildren()[0].getData().get("c"));
        Assertions.assertEquals("b", configuration.getChildren()[0].getData().get("a"));
    }
}
