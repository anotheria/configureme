package org.configureme.annotations.processors.field;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.configureme.Configuration;
import org.configureme.ConfigurationManager;
import org.configureme.Environment;
import org.configureme.util.LocalCacheUtils;
import org.configureme.annotations.ConfigureAlso;
import org.configureme.annotations.ConfigureMe;
import org.configureme.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Annotation processor for {@link ConfigureAlso}.
 *
 * @author Ivan Batura
 */
public class ConfigureAlsoFAProcessor implements FieldAnnotationProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigureAlsoFAProcessor.class);

    @Override
    public boolean isApplicable(final Field f, final boolean configureAllFields) {
        return f.isAnnotationPresent(ConfigureAlso.class);
    }

    @Override
    public void run(final Configuration config, final Object o, Class<? extends Annotation>[] callBefore, Class<? extends Annotation>[] callAfter, final boolean configureAllFields, final Environment environment, Class<?> clazz, final Field f) {
        Object externalConfig = null;
        try {
            final Class<?> externalConfigClass = f.getType();
            if (!externalConfigClass.isAnnotationPresent(ConfigureMe.class))
                return;
            if (!ReflectionUtils.hasParameterlessPublicConstructor(externalConfigClass)) {
                LOGGER.error("Can't instantiate external config for class name=" + f.getType().getName() + ", as there is no default constructor for class = " + externalConfigClass);
                return;
            }
            externalConfig = externalConfigClass.newInstance();
            final ConfigureMe ann = externalConfigClass.getAnnotation(ConfigureMe.class);
            final Object cachedObject = LocalCacheUtils.getCachedObject(ann.name(), environment);
            if (cachedObject == null) {
                ConfigurationManager.INSTANCE.configure(externalConfig, environment);
                LocalCacheUtils.setCachedObject(ann.name(), environment, externalConfig);
            } else {
                externalConfig = cachedObject;
            }
        } catch (final Exception e) {
            LOGGER.error("Can't create external config task for class name=" + f.getType().getName());
        }
        ReflectionUtils.invokeSetter(f, clazz, o, externalConfig);
    }
}
