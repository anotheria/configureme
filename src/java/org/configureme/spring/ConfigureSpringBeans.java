/**
 * 
 */
package org.configureme.spring;

import org.apache.log4j.Logger;
import org.configureme.ConfigurationManager;
import org.configureme.Environment;

/**
 * Spring integration utility. 
 * Missing example on usage!
 * @author matthiaskoch
 */
public class ConfigureSpringBeans {

	/**
	 * Logger.
	 */
	private Logger	log	= Logger.getLogger(ConfigureSpringBeans.class);

	/**
	 * Constructor driven configuration.
	 * @param beans
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
	 * @param beans
	 */
	public ConfigureSpringBeans(Environment environment, Object... beans) {
		for (Object bean : beans) {
			log.debug("ConfigureSpringBeans - try to configure bean: " + bean + " for environment: " + environment);
			ConfigurationManager.INSTANCE.configure(bean, environment);
			log.info("ConfigureSpringBeans - successfully configured bean: " + bean + " for environment: " + environment);
		}
	}
}
