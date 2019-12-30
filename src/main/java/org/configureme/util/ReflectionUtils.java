package org.configureme.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>ReflectionUtils class.</p>
 *
 * @author bvanchuhov
 * @version $Id: $Id
 */
public final class ReflectionUtils {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtils.class);

    private ReflectionUtils() {
        throw new IllegalAccessError("Shouldn't be instantiated.");
    }

    /**
     * <p>getAllFields.</p>
     *
     * @param type
     *         a {@link java.lang.Class} object.
     * @return a {@link java.util.List} object.
     */
    public static List<Field> getAllFields(final Class<?> type) {
        final List<Field> fields = new ArrayList<>();
        for (Class<?> currentType = type; currentType != null; currentType = currentType.getSuperclass())
            fields.addAll(Arrays.asList(currentType.getDeclaredFields()));

        return fields;
    }

    public static boolean hasParameterlessPublicConstructor(Class<?> clazz) {
        for (final Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.getParameterTypes().length == 0)//TODO: when move to java 1.8 change it to constructor.getParameterCount()
                return true;
        }
        return false;
    }

    public static void invokeSetter(final Field f, final Class<?> clazz, final Object o, final Object value) {
        if (Modifier.isPublic(f.getModifiers())) {
            try {
                f.set(o, value);
            } catch (final Exception e) {
                LOGGER.warn(f + ".set(" + o + ", " + value + ')', e);
            }
            return;
        }

        final String methodName = "set" + f.getName().toUpperCase().charAt(0) + f.getName().substring(1);
        try {
            final Method toSet = clazz.getMethod(methodName, f.getType());
            toSet.invoke(o, value);
        } catch (final NoSuchMethodException e) {
            LOGGER.error("can't find method " + methodName + " (" + f.getType() + ')');
        } catch (final Exception e) {
            LOGGER.error("can't set " + f.getName() + " to " + value + ", because: ", e);
        }

    }

    public static Object invokeGetter(final Field f, final Class<?> clazz, final Object o) {
        if (Modifier.isPublic(f.getModifiers())) {
            try {
                return f.get(o);
            } catch (final Exception e) {
                LOGGER.warn(f + ".get(" + o + ')', e);
            }
        }

        final String methodName = "get" + f.getName().toUpperCase().charAt(0) + f.getName().substring(1);
        try {
            final Method toGet = clazz.getMethod(methodName, f.getType());
            toGet.invoke(o);
        } catch (final NoSuchMethodException e) {
            LOGGER.error("can't find method " + methodName + " (" + f.getType() + ')');
        } catch (final Exception e) {
            LOGGER.error("can't get " + f.getName() + ", because: ", e);
        }
        return null;
    }

    /**
     * Create new {@link Collection} object.
     *
     * @param valueClass class of the resulting value instance
     * @return instantiated {@link Collection}
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static  Collection<Object> newInstanceCollection(final Class<?> valueClass) throws IllegalAccessException, InstantiationException {
        if(!valueClass.isInterface())
            return (Collection<Object>) valueClass.newInstance();
        if(List.class.equals(valueClass))
            return new ArrayList<>();
        if(Set.class.equals(valueClass))
            return new HashSet<>();
        throw new IllegalArgumentException();
    }
}
