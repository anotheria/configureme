package org.configureme.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.ConfigurationSourceKey.Type;

@Retention (RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigureMe {
	/**
	 * Artefact name. If skipped the class name (without package) is used.
	 */
	String name() default "";
	
	/**
	 * If true the configuration for the artefact will be watched and the artefact reconfigured as soon as the config changes. It implicitely means that the instance  
	 * to the artefact will be stored in the configuration management. Don't use on objects which are supposed to die soon after usage (at the end of a request or similar,
	 * cause it could lead to memory leaks.
	 * @return
	 */
	boolean watch() default true;
	
	ConfigurationSourceKey.Type type() default Type.FILE;
	
	/**
	 * If set the configurationmanager will try to set all fields regardless if they are marked configured or not. Fields which are annotated DontConfigure will be ignored.  
	 */
	boolean allfields() default false;
}
