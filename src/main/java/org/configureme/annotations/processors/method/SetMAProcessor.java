package org.configureme.annotations.processors.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.configureme.Configuration;
import org.configureme.Environment;
import org.configureme.annotations.Set;
import org.configureme.repository.Value;
import org.configureme.resolver.ResolveManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Annotation processor for {@link Set}.
 *
 * @author Ivan Batura
 */
public class SetMAProcessor implements MethodAnnotationProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodAnnotationProcessor.class);


    @Override
    public boolean isApplicable(final Method method) {
        return method.isAnnotationPresent(Set.class);
    }

    @Override
    public void run(final Configuration config, final Object o, Class<? extends Annotation>[] callBefore, final Class<? extends Annotation>[] callAfter, final boolean configureAllFields, final Environment environment, final Method method) {
        LOGGER.debug("method " + method + " is annotated");
        final Set setAnnotation = method.getAnnotation(Set.class);
        final String attributeName = setAnnotation.value();
        final Value attributeValue = config.getAttribute(attributeName);
        if (attributeValue != null) {
            LOGGER.debug("setting " + method.getName() + " to " + attributeValue + " configured by " + attributeName);
            try {
                method.invoke(o, ResolveManager.instance().resolveValue(method.getParameterTypes()[0], attributeValue, callBefore, callAfter, configureAllFields, environment));
            } catch (final Exception e) {
                LOGGER.warn(method.getName() + "invoke(" + o + ", " + attributeValue + ')', e);
            }
        }
    }
}
