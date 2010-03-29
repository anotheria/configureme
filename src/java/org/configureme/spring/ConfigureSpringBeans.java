/**
 * 
 */
package org.configureme.spring;

import org.apache.log4j.Logger;
import org.configureme.ConfigurationManager;
import org.configureme.Environment;

/**
 * @author matthiaskoch
 * 
 */
public class ConfigureSpringBeans {

	private Logger	log	= Logger.getLogger(ConfigureSpringBeans.class);

	public ConfigureSpringBeans(Object... beans) {
		for (Object bean : beans) {
			log.debug("ConfigureSpringBeans - try to configure bean: " + bean + " for default environment ");
			ConfigurationManager.INSTANCE.configure(bean);
			log.info("ConfigureSpringBeans - successfully configured bean: " + bean);
		}
	}

	public ConfigureSpringBeans(Environment environment, Object... beans) {
		for (Object bean : beans) {
			log.debug("ConfigureSpringBeans - try to configure bean: " + bean + " for environment: " + environment);
			ConfigurationManager.INSTANCE.configure(bean, environment);
			log.info("ConfigureSpringBeans - successfully configured bean: " + bean + " for environment: " + environment);
		}
	}
}
