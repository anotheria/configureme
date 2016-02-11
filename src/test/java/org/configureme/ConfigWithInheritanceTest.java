package org.configureme;

import org.configureme.annotations.ConfigureMe;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author bvanchuhov
 */
public class ConfigWithInheritanceTest {

    @Test
    public void testConfigureMe_configWithInheritance() throws Exception {
        ChildConfig config = new ChildConfig();

        assertThat(config.getParentField(), is("Parent"));
        assertThat(config.getChildField(), is("Child"));
    }

    public static class ParentConfig {

        protected String parentField;

        public String getParentField() {
            return parentField;
        }

        public void setParentField(String parentField) {
            this.parentField = parentField;
        }
    }

    @ConfigureMe(name = "inheritance", allfields = true)
    public static class ChildConfig extends ParentConfig {

        private String childField;

        public ChildConfig() {
            ConfigurationManager.INSTANCE.configure(this);
        }

        public String getChildField() {
            return childField;
        }

        public void setChildField(String childField) {
            this.childField = childField;
        }
    }
}
