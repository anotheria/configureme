package org.configureme.annotations.processors.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.configureme.Configuration;
import org.configureme.Environment;
import org.configureme.annotations.SetAll;
import org.configureme.repository.Value;
import org.configureme.resolver.ResolveManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Annotation processor for {@link SetAll}.
 *
 * @author Ivan Batura
 */
public class SetAllMAProcessor implements MethodAnnotationProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetAllMAProcessor.class);

    @Override
    public boolean isApplicable(final Method method) {
        return method.isAnnotationPresent(SetAll.class);
    }

    @Override
    public void run(final Configuration config, final Object o, Class<? extends Annotation>[] callBefore, final Class<? extends Annotation>[] callAfter, final boolean configureAllFields, final Environment environment, final Method method) {
        final Collection<Map.Entry<String, Value>> entries = config.getEntries();
        LOGGER.debug("Calling method " + method + " with " + entries);
        for (final Map.Entry<String, Value> entry : entries) {
            try {
                method.invoke(o, entry.getKey(), ResolveManager.instance().resolveValue(method.getParameterTypes()[1], entry.getValue(), callBefore, callAfter, configureAllFields, environment));
            } catch (final Exception e) {
                LOGGER.warn(method.getName() + "invoke(" + o + ", " + entry.getKey() + ", " + entry.getValue() + ')', e);
            }
        }
    }
}
