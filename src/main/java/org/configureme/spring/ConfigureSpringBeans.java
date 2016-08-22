/**
 * 
 */
package org.configureme.spring;

import org.configureme.ConfigurationManager;
import org.configureme.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spring integration utility.
 * Missing example on usage!
 *
 * @author matthiaskoch
 * @version $Id: $Id
 */
public class ConfigureSpringBeans {

	/**
	 * Logger.
	 */
	private static Logger log = LoggerFactory.getLogger(ConfigureSpringBeans.class);

	/**
	 * Constructor driven configuration.
	 *
	 * @param beans a {@link java.lang.Object} object.
	 */
	public ConfigureSpringBeans(Object... beans) {
		for (Object bean : beans) {
			log.debug("ConfigureSpringBeans - try to configure bean: " + bean + " for default environment ");
			ConfigurationManager.INSTANCE.configure(bean);
			log.info("ConfigureSpringBeans - successfully configured bean: " + bean);
		}
	}

	/**
	 * Constructor driven configuration.
	 *
	 * @param beans a {@link java.lang.Object} object.
	 * @param environment a {@link org.configureme.Environment} object.
	 */
	public ConfigureSpringBeans(Environment environment, Object... beans) {
		for (Object bean : beans) {
			log.debug("ConfigureSpringBeans - try to configure bean: " + bean + " for environment: " + environment);
			ConfigurationManager.INSTANCE.configure(bean, environment);
			log.info("ConfigureSpringBeans - successfully configured bean: " + bean + " for environment: " + environment);
		}
	}
}
