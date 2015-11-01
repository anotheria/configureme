package org.configureme;

import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;

@ConfigureMe(name="systemproperties")
public class SystemPropertiesConfigurable {
	@Configure private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String aValue) {
		this.value = aValue;
	}
	
	@Override public String toString(){
		return value;
	}
}
