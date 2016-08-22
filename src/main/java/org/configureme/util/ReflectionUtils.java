package org.configureme.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>ReflectionUtils class.</p>
 *
 * @author bvanchuhov
 * @version $Id: $Id
 */
public final class ReflectionUtils {

    private ReflectionUtils() {}

    /**
     * <p>getAllFields.</p>
     *
     * @param type a {@link java.lang.Class} object.
     * @return a {@link java.util.List} object.
     */
    public static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();

        for (Class<?> currentType = type; currentType != null; currentType = currentType.getSuperclass()) {
            fields.addAll(Arrays.asList(currentType.getDeclaredFields()));
        }

        return fields;
    }
}
