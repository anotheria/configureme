package org.configureme.resolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.configureme.Configuration;
import org.configureme.ConfigurationManager;
import org.configureme.Environment;
import org.configureme.parser.ConfigurationParserManager;
import org.configureme.repository.ArrayValue;
import org.configureme.repository.CompositeValue;
import org.configureme.repository.IncludeValue;
import org.configureme.repository.PlainValue;
import org.configureme.repository.Value;
import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.util.ReflectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

/**
 * @author Ivan Batura
 */
public class ResolveManager {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ResolveManager.class);

    /**
     * Set of classes specifying plain attribute types.
     * Allows quickly check whether an attribute is plain or not.
     */
    private static final Collection<Class<?>> PLAIN_TYPES = new HashSet<Class<?>>(
            Arrays.asList(
                    String.class,
                    Boolean.class, boolean.class,
                    Short.class, short.class,
                    Integer.class, int.class,
                    Long.class, long.class,
                    Byte.class, byte.class,
                    Float.class, float.class,
                    Double.class, double.class));

    /**
     * Lock object for singleton creation.
     */
    private static final Object LOCK = new Object();

    /**
     * {@link ResolveManager} instance,
     */
    private static ResolveManager instance;

    /**
     * Private constructor.
     */
    private ResolveManager() {
    }

    /**
     * Get singleton instance of {@link ConfigurationParserManager}.
     *
     * @return {@link ConfigurationSourceKey}
     */
    public static ResolveManager instance() {
        if (instance != null)
            return instance;
        synchronized (LOCK) {
            if (instance != null)
                return instance;

            instance = new ResolveManager();
            return instance;
        }
    }

    /**
     * Resolves attribute value to an instance of specified value class.
     *
     * @param valueType      class of the resulting value instance
     * @param attributeValue array attribute value specifying the configuration of the resulting instance
     * @param callBefore     annotations, methods annotated with those will be called prior to the configuration
     * @param callAfter      annotations, methods annotated with those will be called after the configuration
     * @return an instance of the specified value class which is configured according to the specified attribute value.
     */
    public Object resolveValue(final java.lang.reflect.Type valueType, Value attributeValue,
                               final Class<? extends Annotation>[] callBefore,
                               final Class<? extends Annotation>[] callAfter,
                               final boolean configureAllFields,
                               final Environment environment,
                               final ResolveCallback resolveCallback) throws InstantiationException, IllegalAccessException {
        final boolean parameterized = valueType instanceof ParameterizedType;
        final Class<?> valueClass = parameterized ? ((ParameterizedTypeImpl)valueType).getRawType() : (Class<?>)valueType;
        final java.lang.reflect.Type paramClass = parameterized ? ((ParameterizedType) valueType).getActualTypeArguments()[0] : null;
        while (true) {
            final boolean isValueClassPlain = isPlain(valueClass);
            final boolean isValueClassDummy = valueClass.equals(Object.class) || valueClass.equals(String.class);

            if (attributeValue instanceof PlainValue && !valueClass.isArray() && (isValueClassPlain || isValueClassDummy))
                return resolvePlainValue(valueClass, (PlainValue) attributeValue);
            if (attributeValue instanceof CompositeValue && !valueClass.isArray() && (!isValueClassPlain || isValueClassDummy))
                return resolveCompositeValue(valueClass, (CompositeValue) attributeValue, callBefore, callAfter, configureAllFields, resolveCallback);
            if (attributeValue instanceof ArrayValue && (valueClass.isArray() || isValueClassDummy))
                return resolveArrayValue(valueClass, (ArrayValue) attributeValue, callBefore, callAfter, configureAllFields, environment, resolveCallback);
            if (attributeValue instanceof IncludeValue && (!valueClass.isArray() || !isValueClassDummy)) {
                attributeValue = ((IncludeValue) attributeValue).getIncludedValue(environment);
                continue;
            }
            if (attributeValue instanceof ArrayValue && Collection.class.isAssignableFrom(valueClass))
                return resolveCollectionValue(valueClass, paramClass, (ArrayValue) attributeValue, callBefore, callAfter, configureAllFields, environment, resolveCallback);

            throw new IllegalArgumentException("Can't resolve attribute value " + attributeValue + " to type: " + valueClass.getCanonicalName());
        }
    }

    /**
     * Checks whether the class specifies a plain type or array (with arbitrary number of dimensions) of plain types.
     *
     * @param type the type to be checked
     * @return true if the type is plain, false otherwise
     */
    private static boolean isPlain(final Class<?> type) {
        return (type.isArray())
                ? isPlain(type.getComponentType())
                : PLAIN_TYPES.contains(type) || Enum.class.isAssignableFrom(type);
    }

    private static Object resolvePlainValue(final Class<?> type, final PlainValue value) {
        if (type == null)
            throw new IllegalArgumentException("Checkstyle forced me to do this, apparently type is null which can't happen in resolveValue(null, " + value + ')');
        if (type.equals(String.class) || type.equals(Object.class))
            return value.get();
        if (type.equals(Boolean.class) || type.equals(boolean.class))
            return Boolean.valueOf(value.get());
        if (type.equals(Short.class) || type.equals(short.class))
            return Short.valueOf(value.get());
        if (type.equals(Integer.class) || type.equals(int.class))
            return Integer.valueOf(value.get());
        if (type.equals(Long.class) || type.equals(long.class))
            return Long.valueOf(value.get());
        if (type.equals(Byte.class) || type.equals(byte.class))
            return Byte.valueOf(value.get());
        if (type.equals(Float.class) || type.equals(float.class))
            return Float.valueOf(value.get());
        if (type.equals(Double.class) || type.equals(double.class))
            return Double.valueOf(value.get());

        if (Enum.class.isAssignableFrom(type))
            try {
                return type.cast(type.getMethod("valueOf", String.class).invoke(null, value.get()));
            } catch (final SecurityException | IllegalArgumentException | ClassCastException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new IllegalArgumentException("Can not resolve '" + value + "' to " + type.getCanonicalName(), e);
            }

        throw new IllegalArgumentException("Can not resolve '" + value + "' to " + type.getCanonicalName());
    }

    /**
     * Resolves array attribute value to an instance of specified array value class.
     *
     * @param valueClass     class of the resulting value instance
     * @param attributeValue array attribute value specifying the configuration of the resulting instance
     * @param callBefore     annotations, methods annotated with those will be called prior to the configuration
     * @param callAfter      annotations, methods annotated with those will be called after the configuration
     * @return an array instance of the specified value class which is configured according to the specified array attribute value.
     */
    private Object resolveCollectionValue(final Class<?> valueClass, final java.lang.reflect.Type paramClass,
                                          final ArrayValue attributeValue,
                                          final Class<? extends Annotation>[] callBefore,
                                          final Class<? extends Annotation>[] callAfter,
                                          final boolean configureAllFields,
                                          final Environment environment,
                                          final ResolveCallback resolveCallback) throws InstantiationException, IllegalAccessException {
        if (valueClass.equals(Object.class))
            return attributeValue.getRaw();
        if (valueClass.equals(String.class))
            return new JSONArray((Collection<?>) attributeValue.getRaw()).toString();

        final Collection<Object> resolvedValue = ReflectionUtils.newInstanceCollection(valueClass);
        for (int i = 0; i < attributeValue.get().size(); ++i)
            resolvedValue.add(resolveValue(paramClass, attributeValue.get().get(i), callBefore, callAfter, configureAllFields, environment, resolveCallback));
        return resolvedValue;
    }

    /**
     * Resolves array attribute value to an instance of specified array value class.
     *
     * @param valueClass     class of the resulting value instance
     * @param attributeValue array attribute value specifying the configuration of the resulting instance
     * @param callBefore     annotations, methods annotated with those will be called prior to the configuration
     * @param callAfter      annotations, methods annotated with those will be called after the configuration
     * @return an array instance of the specified value class which is configured according to the specified array attribute value.
     */
    private Object resolveArrayValue(final Class<?> valueClass, final ArrayValue attributeValue,
                                     final Class<? extends Annotation>[] callBefore,
                                     final Class<? extends Annotation>[] callAfter,
                                     final boolean configureAllFields,
                                     final Environment environment,
                                     final ResolveCallback resolveCallback) throws InstantiationException, IllegalAccessException {
        if (valueClass.equals(Object.class))
            return attributeValue.getRaw();
        if (valueClass.equals(String.class))
            return new JSONArray((Collection<?>) attributeValue.getRaw()).toString();

        final Object resolvedValue = Array.newInstance(valueClass.getComponentType(), attributeValue.get().size());
        for (int i = 0; i < attributeValue.get().size(); ++i)
            Array.set(resolvedValue, i, resolveValue(valueClass.getComponentType(), attributeValue.get().get(i), callBefore, callAfter, configureAllFields, environment, resolveCallback));

        return resolvedValue;
    }

    /**
     * Resolves composite attribute value to an instance of specified value class.
     *
     * @param valueClass     class of the resulting value instance
     * @param attributeValue composite attribute value specifying the configuration of the resulting instance
     * @param callBefore     annotations, methods annotated with those will be called prior to the configuration
     * @param callAfter      annotations, methods annotated with those will be called after the configuration
     * @return an instance of the specified value class which is configured according to the specified composite attribute value.
     */
    private Object resolveCompositeValue(final Class<?> valueClass, final CompositeValue attributeValue,
                                         final Class<? extends Annotation>[] callBefore,
                                         final Class<? extends Annotation>[] callAfter,
                                         final boolean configureAllFields,
                                         final ResolveCallback resolveCallback) throws InstantiationException, IllegalAccessException {
        if (valueClass.equals(Object.class))
            return attributeValue.getRaw();
        if (valueClass.equals(String.class))
            return new JSONObject((Map<?, ?>) attributeValue.getRaw()).toString();

        final Object resolvedValue = valueClass.newInstance();
        resolveCallback.configure(attributeValue.get(), resolvedValue, callBefore, callAfter, configureAllFields, ConfigurationManager.INSTANCE.getDefaultEnvironment());
        return resolvedValue;
    }

    public interface ResolveCallback{
        void configure(final Configuration config, final Object o, final Class<? extends Annotation>[] callBefore, final Class<? extends Annotation>[] callAfter, final boolean configureAllFields, final Environment environment);
    }
}
