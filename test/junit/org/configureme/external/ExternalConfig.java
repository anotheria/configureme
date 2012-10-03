package org.configureme.external;

import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;

/**
 * @author ivanbatura
 * @since: 01.10.12
 */
@ConfigureMe(name = "external")
public class ExternalConfig {
	@Configure
	String external;

	public void setExternal(String external) {
		this.external = external;
	}

	public String getExternal() {
		return external;
	}
}
