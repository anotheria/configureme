package org.configureme.annotations.processors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.configureme.Configuration;
import org.configureme.Environment;
import org.configureme.annotations.processors.field.FieldAnnotationProcessor;
import org.configureme.annotations.processors.field.FieldAnnotationProcessorConfigure;
import org.configureme.annotations.processors.field.FieldAnnotationProcessorConfigureAlso;
import org.configureme.annotations.processors.method.MethodAnnotationProcessor;
import org.configureme.annotations.processors.method.MethodAnnotationProcessorSet;
import org.configureme.annotations.processors.method.MethodAnnotationProcessorSetAll;
import org.configureme.annotations.processors.method.MethodAnnotationProcessorSetIf;
import org.configureme.parser.ConfigurationParserManager;
import org.configureme.sources.ConfigurationSourceKey;

/**
 * Annotation process manager.
 * To process fields and methods.
 *
 * @author Ivan Batura
 */
public class AnnotationProcessorManager {
    /**
     * Lock object for singleton creation.
     */
    private static final Object LOCK = new Object();

    /**
     * {@link ConfigurationParserManager} instance,
     */
    private static AnnotationProcessorManager instance;

    /**
     * {@link List} with available {@link MethodAnnotationProcessor}.
     */
    private final List<MethodAnnotationProcessor> methodProcessors = new ArrayList<>();

    /**
     * {@link List} with available {@link FieldAnnotationProcessor}.
     */
    private final List<FieldAnnotationProcessor> fieldProcessors = new ArrayList<>();

    /**
     * Private constructor.
     */
    private AnnotationProcessorManager() {
        methodProcessors.add(new MethodAnnotationProcessorSetAll());
        methodProcessors.add(new MethodAnnotationProcessorSetIf());
        methodProcessors.add(new MethodAnnotationProcessorSet());

        fieldProcessors.add(new FieldAnnotationProcessorConfigureAlso());
        fieldProcessors.add(new FieldAnnotationProcessorConfigure());
    }

    /**
     * Get singleton instance of {@link ConfigurationParserManager}.
     *
     * @return {@link ConfigurationSourceKey}
     */
    public static AnnotationProcessorManager instance() {
        if (instance != null)
            return instance;
        synchronized (LOCK) {
            if (instance != null)
                return instance;

            instance = new AnnotationProcessorManager();
            return instance;
        }
    }

    /**
     * Process fields with all available field processors, if its applicable for provided field.
     *
     * @param config {@link Configuration}
     * @param o configurable object
     * @param callBefore annotations to call before
     * @param callAfter annotations to call after
     * @param configureAllFields configure all fields
     * @param environment {@link Environment}
     * @param clazz class
     * @param f {@link Field} to process
     */
    public void processFields(final Configuration config, final Object o, Class<? extends Annotation>[] callBefore, Class<? extends Annotation>[] callAfter, final boolean configureAllFields, final Environment environment, final Class<?> clazz, final Field f) {
        for (final FieldAnnotationProcessor processor : fieldProcessors) {
            if (processor.isApplicable(f, configureAllFields))
                processor.run(config, o, callBefore, callAfter, configureAllFields, environment, clazz, f);
        }
    }

    /**
     * Process methods with all available moethod processors, if its applicable for provided method.
     *
     * @param config {@link Configuration}
     * @param o configurable object
     * @param callBefore annotations to call before
     * @param callAfter annotations to call after
     * @param configureAllFields configure all fields
     * @param environment {@link Environment}
     * @param method {@link Method} to process
     */
    public void processMethods(final Configuration config, final Object o, Class<? extends Annotation>[] callBefore, final Class<? extends Annotation>[] callAfter, final boolean configureAllFields, final Environment environment, final Method method) {
        for (final MethodAnnotationProcessor processor : methodProcessors) {
            if (processor.isApplicable(method))
                processor.run(config, o, callBefore, callAfter, configureAllFields, environment, method);

        }
    }
}
