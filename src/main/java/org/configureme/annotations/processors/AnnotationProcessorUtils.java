package org.configureme.annotations.processors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.configureme.Configuration;
import org.configureme.Environment;
import org.configureme.annotations.processors.field.ConfigureAlsoFAProcessor;
import org.configureme.annotations.processors.field.ConfigureFAProcessor;
import org.configureme.annotations.processors.field.FieldAnnotationProcessor;
import org.configureme.annotations.processors.method.MethodAnnotationProcessor;
import org.configureme.annotations.processors.method.SetAllMAProcessor;
import org.configureme.annotations.processors.method.SetIfMAProcessor;
import org.configureme.annotations.processors.method.SetMAProcessor;

/**
 * Annotation process manager.
 * To process fields and methods.
 *
 * @author Ivan Batura
 */
public final class AnnotationProcessorUtils {
    /**
     * {@link List} with available {@link MethodAnnotationProcessor}.
     */
    private static final List<MethodAnnotationProcessor> METHOD_ANNOTATION_PROCESSORS = new ArrayList<>();

    /**
     * {@link List} with available {@link FieldAnnotationProcessor}.
     */
    private static final List<FieldAnnotationProcessor> FIELD_ANNOTATION_PROCESSORS = new ArrayList<>();

    //Init
    static {
        METHOD_ANNOTATION_PROCESSORS.add(new SetAllMAProcessor());
        METHOD_ANNOTATION_PROCESSORS.add(new SetIfMAProcessor());
        METHOD_ANNOTATION_PROCESSORS.add(new SetMAProcessor());

        FIELD_ANNOTATION_PROCESSORS.add(new ConfigureAlsoFAProcessor());
        FIELD_ANNOTATION_PROCESSORS.add(new ConfigureFAProcessor());
    }

    /**
     * Private constructor.
     */
    private AnnotationProcessorUtils() {
        throw new UnsupportedOperationException("Cannot be initiated");
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
    public static void processFields(final Configuration config, final Object o, Class<? extends Annotation>[] callBefore, Class<? extends Annotation>[] callAfter, final boolean configureAllFields, final Environment environment, final Class<?> clazz, final Field f) {
        for (final FieldAnnotationProcessor processor : FIELD_ANNOTATION_PROCESSORS) {
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
    public static void processMethods(final Configuration config, final Object o, Class<? extends Annotation>[] callBefore, final Class<? extends Annotation>[] callAfter, final boolean configureAllFields, final Environment environment, final Method method) {
        for (final MethodAnnotationProcessor processor : METHOD_ANNOTATION_PROCESSORS) {
            if (processor.isApplicable(method))
                processor.run(config, o, callBefore, callAfter, configureAllFields, environment, method);

        }
    }
}
