package org.configureme.annotations.processors.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.configureme.Configuration;
import org.configureme.Environment;
import org.configureme.annotations.SetIf;
import org.configureme.repository.Value;
import org.configureme.util.resolver.ValueResolveUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Annotation processor for {@link SetIf}.
 *
 * @author Ivan Batura
 */
public class SetIfMAProcessor implements MethodAnnotationProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetIfMAProcessor.class);

    @Override
    public boolean isApplicable(final Method method) {
        return method.isAnnotationPresent(SetIf.class);
    }

    @Override
    public void run(final Configuration config, final Object o, Class<? extends Annotation>[] callBefore, final Class<? extends Annotation>[] callAfter, final boolean configureAllFields, final Environment environment, final Method method) {
        final Collection<Map.Entry<String, Value>> entries = config.getEntries();
        final SetIf setIfAnnotation = method.getAnnotation(SetIf.class);
        for (final Map.Entry<String, Value> entry : entries) {
            if (!SetIf.ConditionChecker.satisfyCondition(setIfAnnotation, entry.getKey()))
                continue;

            LOGGER.debug("Calling method " + method + " with parameters : \"" + entry.getKey() + "\", \"" + entry.getValue() + '"');
            try {
                method.invoke(o, entry.getKey(), ValueResolveUtils.resolveValue(method.getParameterTypes()[1], entry.getValue(), callBefore, callAfter, configureAllFields, environment));
            } catch (final Exception e) {
                LOGGER.warn(method.getName() + "invoke(" + o + ", " + entry.getKey() + ", " + entry.getValue() + ')', e);
            }

        }
    }
}
