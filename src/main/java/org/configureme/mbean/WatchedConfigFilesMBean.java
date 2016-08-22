package org.configureme.mbean;

import java.util.Set;

/**
 * Common info mBean interface.
 *
 * @author asamoilich
 * @version $Id: $Id
 */
public interface WatchedConfigFilesMBean {
	/**
	 * Return set of all configurations names.
	 *
	 * @return set of all configurations names
	 */
	Set<String> getConfigNames();
}
