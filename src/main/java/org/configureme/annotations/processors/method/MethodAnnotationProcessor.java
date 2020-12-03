package org.configureme.annotations.processors.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.configureme.Configuration;
import org.configureme.Environment;

/**
 * Annotation method processor.
 *
 * @author Ivan Batura
 */
public interface MethodAnnotationProcessor {

    /**
     * Detect if provided method should be process with current processor.
     *
     * @param method
     *         {@link Method}
     * @return {@code true} while current processor is applicable for provided {@code method}, otherwise {@code false}
     */
    boolean isApplicable(final Method method);

    /**
     * Process provided method.
     *
     * @param config
     *         {@link Configuration}
     * @param o
     *         Object to configure
     * @param callBefore
     *         annotations to call before
     * @param callAfter
     *         annotations to call after
     * @param configureAllFields
     *         configure All Fields or not
     * @param environment
     *         {@link Environment}
     * @param method
     *         {@link Method}
     */
    void run(final Configuration config, final Object o, Class<? extends Annotation>[] callBefore, final Class<? extends Annotation>[] callAfter, final boolean configureAllFields, final Environment environment, final Method method);
}
