package org.configureme.annotations;

import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.ConfigurationSourceKey.Type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class configurable.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
@Retention (RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigureMe {
	/**
	 * Artifact name. If skipped the class name (without package) is used.
	 */
	String name() default "";
	
	/**
	 * If true the configuration for the artifact will be watched and the artifact reconfigured as soon as the config changes. It implicitly means that the instance
	 * to the artifact will be stored in the configuration management. Don't use on objects which are supposed to die soon after usage (at the end of a request or similar,
	 * cause it could lead to memory leaks.
	 */
	boolean watch() default true;
	
	/**
	 * Source file type. See {@link ConfigurationSourceKey.Type}.
	 */
	ConfigurationSourceKey.Type type() default Type.FILE;
	
	/**
	 * If set the configuration manager will try to set all fields regardless if they are marked configured or not. Fields which are annotated DontConfigure will be ignored.
	 */
	boolean allfields() default false;
}
