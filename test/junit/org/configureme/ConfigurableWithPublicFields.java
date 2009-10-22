package org.configureme;

import net.anotheria.util.StringUtils;

import org.configureme.annotations.AfterConfiguration;
import org.configureme.annotations.AfterInitialConfiguration;
import org.configureme.annotations.AfterReConfiguration;
import org.configureme.annotations.BeforeConfiguration;
import org.configureme.annotations.BeforeInitialConfiguration;
import org.configureme.annotations.BeforeReConfiguration;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.sources.ConfigurationSourceKey;

@ConfigureMe(name="fixture", type=ConfigurationSourceKey.Type.FIXTURE)
public class ConfigurableWithPublicFields {
	@Configure public short shortValue;
	@Configure public long longValue;
	@Configure public int intValue;
	@Configure public boolean booleanValue;
	@Configure public String stringValue;
	@Configure public byte byteValue;
	@Configure public float floatValue;
	@Configure public double doubleValue ;
	@Configure public String[] stringArrayValue;
	@Configure public int onlyInA;
	@Configure public int onlyInB;
	
	private boolean beforeConfigCalled, afterConfigCalled, beforeInitialConfigCalled, afterInitialConfigCalled, beforeReConfigCalled, afterReConfigCalled;
	
	public String toString(){
		String ret = "";
		ret += "short: "+shortValue;
		ret += " long: "+longValue;
		ret += " int: "+intValue;
		ret += " boolean: "+booleanValue;
		ret += " string: "+stringValue;
		ret += " byte: "+byteValue;
		ret += " float: "+floatValue;
		ret += " double: "+doubleValue;
		ret += " stringArray: " + StringUtils.concatenateTokens(",",stringArrayValue);
		ret += " onlyInA: "+onlyInA;
		ret += " onlyInB: "+onlyInB;
		return ret;
	}
	
	public boolean isBeforeConfigCalled() {
		return beforeConfigCalled;
	}

	public boolean isAfterConfigCalled() {
		return afterConfigCalled;
	}

	public boolean isBeforeInitialConfigCalled() {
		return beforeInitialConfigCalled;
	}

	public boolean isAfterInitialConfigCalled() {
		return afterInitialConfigCalled;
	}

	public boolean isBeforeReConfigCalled() {
		return beforeReConfigCalled;
	}

	public boolean isAfterReConfigCalled() {
		return afterReConfigCalled;
	}
	
	@BeforeConfiguration public void before(){ beforeConfigCalled = true; }
	@BeforeInitialConfiguration public void beforeInitial(){ beforeInitialConfigCalled = true; }
	@BeforeReConfiguration public void beforeRe(){ beforeReConfigCalled = true; }
	
	@AfterConfiguration public void after(){ afterConfigCalled = true; }
	@AfterInitialConfiguration public void afterInitial(){ afterInitialConfigCalled = true; }
	@AfterReConfiguration public void afterRe(){ afterReConfigCalled = true; }
}