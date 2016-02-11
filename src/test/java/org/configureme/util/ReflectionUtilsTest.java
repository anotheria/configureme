package org.configureme.util;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

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

    private static List<String> fields2names(List<Field> fields) {
        List<String> names = new ArrayList<>(fields.size());

        for (Field field : fields) {
            names.add(field.getName());
        }

        return names;
    }
}