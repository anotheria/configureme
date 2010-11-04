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
import org.configureme.annotations.SetAll;
import org.configureme.sources.ConfigurationSourceKey;

@ConfigureMe(name="notusedname", type=ConfigurationSourceKey.Type.FIXTURE, watch=false)
public class TestConfigurableAs {
	@Configure private short shortValue;
	@Configure private long longValue;
	@Configure private int intValue;
	@Configure private boolean booleanValue;
	@Configure private String stringValue;
	@Configure private byte byteValue;
	@Configure private float floatValue;
	@Configure private double doubleValue ;
	@Configure private String[] stringArrayValue;
	@Configure private int onlyInA;
	@Configure private int onlyInB;
	
	private boolean beforeConfigCalled, afterConfigCalled, beforeInitialConfigCalled, afterInitialConfigCalled, beforeReConfigCalled, afterReConfigCalled;
	
	@Override public String toString(){
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
	
	public short getShortValue() {
		return shortValue;
	}
	public void setShortValue(short shortValue) {
		this.shortValue = shortValue;
	}
	public long getLongValue() {
		return longValue;
	}
	public void setLongValue(long longValue) {
		this.longValue = longValue;
	}
	public int getIntValue() {
		return intValue;
	}
	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}
	public boolean getBooleanValue() {
		return booleanValue;
	}
	public void setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}
	public String getStringValue() {
		return stringValue;
	}
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
	public byte getByteValue() {
		return byteValue;
	}
	public void setByteValue(byte byteValue) {
		this.byteValue = byteValue;
	}
	public float getFloatValue() {
		return floatValue;
	}
	public void setFloatValue(float floatValue) {
		this.floatValue = floatValue;
	}
	public double getDoubleValue() {
		return doubleValue;
	}
	public void setDoubleValue(double doubleValue) {
		this.doubleValue = doubleValue;
	}
	public String[] getStringArrayValue() {
		return stringArrayValue;
	}
	
	public void setStringArrayValue(String[] stringArrayValue) {
		this.stringArrayValue = stringArrayValue;
	}
	public int getOnlyInA() {
		return onlyInA;
	}
	public void setOnlyInA(int onlyInA) {
		this.onlyInA = onlyInA;
	}
	public int getOnlyInB() {
		return onlyInB;
	}
	public void setOnlyInB(int onlyInB) {
		this.onlyInB = onlyInB;
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
	
	@SetAll public void debugOutConfig(String name, String property){
		System.out.println("Config property "+name+" = "+property);
	}


}
