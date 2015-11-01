package org.configureme.embeddedobjects;

import org.configureme.annotations.Configure;

/**
 * TODO comment this class
 *
 * @author lrosenberg
 * @since 22.10.12 11:27
 */
public class InnerObject {
	@Configure private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "InnerObject{" +
				"name='" + name + '\'' +
				'}';
	}
}
