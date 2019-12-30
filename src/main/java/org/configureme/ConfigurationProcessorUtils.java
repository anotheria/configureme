package org.configureme;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.configureme.annotations.AfterConfiguration;
import org.configureme.annotations.AfterInitialConfiguration;
import org.configureme.annotations.AfterReConfiguration;
import org.configureme.annotations.BeforeConfiguration;
import org.configureme.annotations.BeforeInitialConfiguration;
import org.configureme.annotations.BeforeReConfiguration;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.processors.AnnotationProcessorUtils;
import org.configureme.mbean.ConfigInfo;
import org.configureme.mbean.util.MBeanRegisterUtil;
import org.configureme.parser.ConfigurationParserManager;
import org.configureme.parser.ParsedConfiguration;
import org.configureme.repository.ConfigurationRepository;
import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.ConfigurationSourceRegistry;
import org.configureme.util.LocalCacheUtils;
import org.configureme.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Should be in this package. Needed to decrease number of code in ConfigurationManager.
 *
 * @author Ivan Batura
 */
public final class ConfigurationProcessorUtils {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationProcessorUtils.class);

    /**
     * Annotations to call before initial configuration.
     */
    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] CALL_BEFORE_INITIAL_CONFIGURATION = (Class<? extends Annotation>[]) new Class<?>[]{
            BeforeInitialConfiguration.class, BeforeConfiguration.class
    };

    /**
     * Annotations to call after initial configuration.
     */
    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] CALL_AFTER_INITIAL_CONFIGURATION = (Class<? extends Annotation>[]) new Class<?>[]{
            AfterConfiguration.class, AfterInitialConfiguration.class
    };

    /**
     * Annotations to call before reconfiguration.
     */
    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] CALL_BEFORE_RE_CONFIGURATION = (Class<? extends Annotation>[]) new Class<?>[]{
            BeforeReConfiguration.class, BeforeConfiguration.class
    };

    /**
     * Annotations to call before after reconfiguration.
     */
    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] CALL_AFTER_RE_CONFIGURATION = (Class<? extends Annotation>[]) new Class<?>[]{
            AfterConfiguration.class, AfterReConfiguration.class
    };

    /**
     * Private constructor.
     */
    private ConfigurationProcessorUtils() {
        throw new UnsupportedOperationException("Cannot be initiated");
    }
    

    /**
     * Internal method used at initial, user triggered configuration.
     *
     * @param key the source key
     * @param o   the object to configure
     * @param in  the environment
     * @param ann the configureme annotation instance with which o.getClass() was annotated.
     */
    public static void configureInitially(final ConfigurationSourceKey key, final Object o, final Environment in, final ConfigureMe ann) {

        configure(key, o, in, CALL_BEFORE_INITIAL_CONFIGURATION, CALL_AFTER_INITIAL_CONFIGURATION, ann);

        if (ann.watch()) {
            final ConfigurableWrapper wrapper = new ConfigurableWrapper(key, o, in);
            ConfigurationSourceRegistry.INSTANCE.addWatchedConfigurable(wrapper);
            MBeanRegisterUtil.regMBean(new ConfigInfo(key.getName()), key.getName());
        }

    }

    /**
     * This method executes the configuration.
     *
     * @param key        the key for the configuration source
     * @param o          the object to configure
     * @param in         environment in which the object runs
     * @param callBefore annotations, methods annotated with those will be called prior to the configuration
     * @param callAfter  annotations, methods annotated with those will be called after the configuration
     */
    public static void configure(final ConfigurationSourceKey key, final Object o, final Environment in, final Class<? extends Annotation>[] callBefore, final Class<? extends Annotation>[] callAfter, ConfigureMe ann) {
        final Class<?> clazz = o.getClass();
        final ConfigureMe annInternal = ann != null ? ann : clazz.getAnnotation(ConfigureMe.class);
        if (annInternal == null)
            throw new AssertionError("An unannotated class shouldn't make it so far, obj: " + o + " class " + o.getClass());

        final boolean configureAllFields = annInternal.allfields();

        final Configuration configuration = getConfiguration(key, in);
        configure(configuration, o, callBefore, callAfter, configureAllFields, in);

        // added all external configuration watchers
        if (annInternal.watch()) {
            for (ConfigurationSourceKey sourceKey : configuration.getExternalConfigurations()) {
                ConfigurableWrapper wrapper = new ConfigurableWrapper(sourceKey, o, in);
                ConfigurationSourceRegistry.INSTANCE.addWatchedConfigurable(wrapper);
                MBeanRegisterUtil.regMBean(new ConfigInfo(sourceKey.getName()), sourceKey.getName());
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Finished configuration of {} as {}", o, key);
        }
    }


    /**
     * Applies specified configuration to specified object.
     *
     * @param config             the configuration to be applied
     * @param o                  the object to be configured
     * @param callBefore         annotations, methods annotated with those will be called prior to the configuration
     * @param callAfter          annotations, methods annotated with those will be called after the configuration
     * @param configureAllFields specifies whether to set all fields regardless if they are marked configured or not
     */
    public static void configure(final Configuration config, final Object o, final Class<? extends Annotation>[] callBefore, final Class<? extends Annotation>[] callAfter, final boolean configureAllFields, final Environment environment) {
        LocalCacheUtils.setCachedObject(config.getName(), environment, o);
        final Class<?> clazz = o.getClass();
        final Method[] methods = clazz.getDeclaredMethods();
        callAnnotations(o, methods, callBefore);

        final List<Field> fields = ReflectionUtils.getAllFields(clazz);
        for (final Field f : fields) {
            AnnotationProcessorUtils.processFields(config, o, callBefore, callAfter, configureAllFields, environment, clazz, f);
        }

        for (final Method method : methods) {
            AnnotationProcessorUtils.processMethods(config, o, callBefore, callAfter, configureAllFields, environment, method);
        }

        callAnnotations(o, methods, callAfter);
    }

    /**
     * Called by ConfigurationSource monitors/listeners to trigger a reconfiguration of a component.
     *
     * @param key {@link ConfigurationSourceKey}
     * @param o object to configure
     * @param in {@link Environment}
     */
    public static void reconfigure(final ConfigurationSourceKey key, final Object o, final Environment in) {
        configure(key, o, in, CALL_BEFORE_RE_CONFIGURATION, CALL_AFTER_RE_CONFIGURATION, null);
    }

    /**
     * Internal method for configuration retrieval.
     *
     * @param configSourceKey {@link ConfigurationSourceKey}
     * @param in the environment
     * @return {@link Configuration}
     */
    public static Configuration getConfiguration(final ConfigurationSourceKey configSourceKey, final Environment in) {
        //for the first we will hardcode file as config source and json as config format.
        final String configurationName = configSourceKey.getName();
        if (ConfigurationRepository.INSTANCE.hasConfiguration(configurationName))
            return ConfigurationRepository.INSTANCE.getConfiguration(configurationName, in);

        if (!ConfigurationSourceRegistry.INSTANCE.isConfigurationAvailable(configSourceKey))
            throw new IllegalArgumentException("No such configuration: " + configurationName + " (" + configSourceKey + ')');

        //reading config
        final String content = ConfigurationSourceRegistry.INSTANCE.readConfigurationSource(configSourceKey);
        // parse config
        final ParsedConfiguration pa = ConfigurationParserManager.instance().parse(configSourceKey, content);
        // create and store configuration
        return ConfigurationRepository.INSTANCE.createConfiguration(pa, configurationName, in, ConfigurationSourceKey.Type.DEFAULT, ConfigurationSourceKey.Format.DEFAULT);
    }

    /**
     * This method is used internally for calls to annotations at the start and the end of each configuration.
     *
     * @param configurable object that is configuring
     * @param methods methods to call
     * @param annotationClasses annotations of the configurable class
     */
    private static void callAnnotations(final Object configurable, final Method[] methods, final Class<? extends Annotation>[] annotationClasses) {
        //check for annotations to call and call 'before' annotations
        for (final Method m : methods) {
            //System.out.println("Checking methid "+m);
            for (final Class<? extends Annotation> anAnnotationClass : annotationClasses) {
                //System.out.println("\tChecking annotation "+anAnnotationClass);
                final Annotation anAnnotation = m.getAnnotation(anAnnotationClass);
                //System.out.println("\t\t-->"+anAnnotation);
                if (anAnnotation == null)
                    continue;

                try {
                    m.invoke(configurable);
                } catch (final IllegalAccessException e) {
                    LOGGER.error("callAnnotations(" + Arrays.toString(methods) + ", " + Arrays.toString(annotationClasses) + ')', e);
                    throw new AssertionError("Error declaration in method " + m + ", wrong declaration (public void " + m.getName() + " expected)? - " + e.getMessage());
                } catch (final InvocationTargetException e) {
                    LOGGER.error("callAnnotations(Exception in annotated method: " + m + ')', e);
                    throw new RuntimeException("Exception in annotated method: " + e.getMessage(), e);
                }
            }
        }
    }
}
