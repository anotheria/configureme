package org.configureme.mbean.util;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.security.AccessControlException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Util for register mBeans in {@link javax.management.MBeanServer}.
 *
 * @author asamoilich
 * @version $Id: $Id
 */
public final class MBeanRegisterUtil {
	/**
	 * Logger util.
	 */
	private static final Logger log = LoggerFactory.getLogger(MBeanRegisterUtil.class);
	/**
	 * {@link MBeanServer} server.
	 */
	private static final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

	/**
	 * Register mBean object in {@link javax.management.MBeanServer}.
	 *
	 * @param object     provided object
	 * @param parameters additional parameters
	 */
	public static void regMBean(final Object object, final String... parameters) {
		try {
			final String name = buildObjectName(object, parameters);
			final ObjectName objectName = new ObjectName(name);
			if (mbs.isRegistered(objectName))
				return;
			mbs.registerMBean(object, objectName);
		} catch (final MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException e) {
			log.error("can't register mbean regMBean("+object+", "+ Arrays.toString(parameters)+ ')', e);
		} catch (final AccessControlException e){
			log.error("Access denied, can no register mbean add permission javax.management.MBeanTrustPermission \"register\"; to java.policy file", e);
		}
	}

	/**
	 * Return object name with which will be register in {@link MBeanServer}.
	 *
	 * @param object     provided object
	 * @param parameters additional parameters
	 * @return object name
	 */
	private static String buildObjectName(final Object object, final String... parameters) {
		final StringBuilder objectName = new StringBuilder();
		objectName.append(object.getClass().getPackage().getName());
		objectName.append(":type=");
		objectName.append(object.getClass().getName());
		if (parameters.length > 0) {
			objectName.append('(');
			for (final String parameter : parameters)
				objectName.append(parameter).append(',');
			objectName.deleteCharAt(objectName.length() - 1);
			objectName.append(')');
		}
		return objectName.toString();
	}

	/**
	 * Private constructor.
	 */
	private MBeanRegisterUtil() {
		throw new IllegalAccessError("Can't instantiate.");
	}
}
