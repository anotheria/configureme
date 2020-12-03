package org.configureme.util;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ReflectionUtilsTest {

    @Test
    public void testGetAllFields() throws Exception {
        class Parent {
            private String parentField;
        }

        class Child extends Parent {
            private String childField;
        }

        List<Field> allFields = ReflectionUtils.getAllFields(Child.class);
        List<String> fieldNames = fields2names(allFields);

        assertThat(fieldNames, hasItems("parentField", "childField"));
    }

    private static List<String> fields2names(final List<Field> fields) {
        final List<String> names = new ArrayList<>(fields.size());

        for (final Field field : fields) {
            names.add(field.getName());
        }

        return names;
    }
}