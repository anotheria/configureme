package org.configureme.linked;

import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;

/**
 * TODO comment this class
 *
 * @author lrosenberg
 * @since 28.04.16 16:00
 */
@ConfigureMe (watch = false, name = "lcwesource")
public class LinkedConfigWithEnvironment {
	@Configure
	private String linked;

	@Override
	public String toString() {
		return "LinkedConfigWithEnvironment{" +
				"linked='" + linked + '\'' +
				'}';
	}

	public String getLinked() {
		return linked;
	}

	public void setLinked(String linked) {
		this.linked = linked;
	}
}
