package org.configureme.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author bvanchuhov
 */
public final class ReflectionUtils {

    private ReflectionUtils() {}

    public static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();

        for (Class<?> currentType = type; currentType != null; currentType = currentType.getSuperclass()) {
            fields.addAll(Arrays.asList(currentType.getDeclaredFields()));
        }

        return fields;
    }
}
