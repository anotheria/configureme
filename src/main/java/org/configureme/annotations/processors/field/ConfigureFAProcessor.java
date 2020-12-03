package org.configureme.annotations.processors.field;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.configureme.Configuration;
import org.configureme.Environment;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureAlso;
import org.configureme.annotations.DontConfigure;
import org.configureme.repository.Value;
import org.configureme.util.resolver.ValueResolveUtils;
import org.configureme.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Annotation processor for {@link Configure}.
 *
 * @author Ivan Batura
 */
public class ConfigureFAProcessor implements FieldAnnotationProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigureFAProcessor.class);

    @Override
    public boolean isApplicable(final Field f, final boolean configureAllFields) {
        return !f.isAnnotationPresent(ConfigureAlso.class) && (f.isAnnotationPresent(Configure.class) || (configureAllFields && !f.isAnnotationPresent(DontConfigure.class)));
    }

    @Override
    public void run(final Configuration config, final Object o, Class<? extends Annotation>[] callBefore, Class<? extends Annotation>[] callAfter, final boolean configureAllFields, final Environment environment, Class<?> clazz, final Field f) {
        final String attributeName = f.getName();
        final Value attributeValue = config.getAttribute(attributeName);
        if (attributeValue == null)
            return;
        try {
            ReflectionUtils.invokeSetter(f, clazz, o, ValueResolveUtils.resolveValue(f.getGenericType(), attributeValue, callBefore, callAfter, configureAllFields, environment));
        } catch (final Exception e) {
            LOGGER.error("can't set " + attributeName + " to " + attributeValue + ", because: ", e);
        }
    }
}
