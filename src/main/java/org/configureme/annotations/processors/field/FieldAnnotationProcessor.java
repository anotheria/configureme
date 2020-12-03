package org.configureme.annotations.processors.field;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.configureme.Configuration;
import org.configureme.Environment;

/**
 * Annotation Field processor.
 *
 * @author Ivan Batura
 */
public interface FieldAnnotationProcessor {
    /**
     * Detect if provided field should be process with current processor.
     *
     * @param f
     *         {@link Field}
     * @param configureAllFields
     *         configure All Fields or not
     * @return {@code true} while current processor is applicable for provided {@code f}, otherwise {@code false}
     */
    boolean isApplicable(Field f, boolean configureAllFields);

    /**
     * Process provided field.
     *
     * @param config
     *         {@link Configuration}
     * @param o
     *         object to configure
     * @param callBefore
     *         Annotations to call before
     * @param callAfter
     *         Annotations to call after
     * @param configureAllFields
     *         configure All Fields or not
     * @param environment
     *         {@link Environment}
     * @param clazz
     *         configuration class
     * @param f
     *         {@link Field}
     */
    void run(final Configuration config, final Object o, Class<? extends Annotation>[] callBefore, Class<? extends Annotation>[] callAfter, final boolean configureAllFields, final Environment environment, Class<?> clazz, final Field f);
}
