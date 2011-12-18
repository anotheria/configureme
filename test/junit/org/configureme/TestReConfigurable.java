package org.configureme;

import org.configureme.annotations.AfterConfiguration;
import org.configureme.annotations.AfterInitialConfiguration;
import org.configureme.annotations.AfterReConfiguration;
import org.configureme.annotations.BeforeConfiguration;
import org.configureme.annotations.BeforeInitialConfiguration;
import org.configureme.annotations.BeforeReConfiguration;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.sources.ConfigurationSourceKey;

@ConfigureMe(name="fixture", type=ConfigurationSourceKey.Type.FIXTURE, watch=true)
public class TestReConfigurable extends TestConfigurable{
	@Configure private short shortValue;
	@Configure private long longValue;
	@Configure private int intValue;
	@Configure private boolean booleanValue;
	@Configure private String stringValue;
	@Configure private byte byteValue;
	@Configure private float floatValue;
	@Configure private double doubleValue ;
	@Configure private int onlyInA;
	@Configure private int onlyInB;

	private boolean beforeConfigCalled, afterConfigCalled, beforeInitialConfigCalled, afterInitialConfigCalled, beforeReConfigCalled, afterReConfigCalled;

	@Override
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
		ret += " onlyInA: "+onlyInA;
		ret += " onlyInB: "+onlyInB;
		return ret;
	}

	@Override
	public short getShortValue() {
		return shortValue;
	}
	@Override
	public void setShortValue(short shortValue) {
		this.shortValue = shortValue;
	}
	@Override
	public long getLongValue() {
		return longValue;
	}
	@Override
	public void setLongValue(long longValue) {
		this.longValue = longValue;
	}
	@Override
	public int getIntValue() {
		return intValue;
	}
	@Override
	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}
	@Override
	public boolean getBooleanValue() {
		return booleanValue;
	}
	@Override
	public void setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}
	@Override
	public String getStringValue() {
		return stringValue;
	}
	@Override
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
	@Override
	public byte getByteValue() {
		return byteValue;
	}
	@Override
	public void setByteValue(byte byteValue) {
		this.byteValue = byteValue;
	}
	@Override
	public float getFloatValue() {
		return floatValue;
	}
	@Override
	public void setFloatValue(float floatValue) {
		this.floatValue = floatValue;
	}
	@Override
	public double getDoubleValue() {
		return doubleValue;
	}
	@Override
	public void setDoubleValue(double doubleValue) {
		this.doubleValue = doubleValue;
	}
	@Override
	public int getOnlyInA() {
		return onlyInA;
	}
	@Override
	public void setOnlyInA(int onlyInA) {
		this.onlyInA = onlyInA;
	}
	@Override
	public int getOnlyInB() {
		return onlyInB;
	}
	@Override
	public void setOnlyInB(int onlyInB) {
		this.onlyInB = onlyInB;
	}

	@Override
	public boolean isBeforeConfigCalled() {
		return beforeConfigCalled;
	}

	@Override
	public boolean isAfterConfigCalled() {
		return afterConfigCalled;
	}

	@Override
	public boolean isBeforeInitialConfigCalled() {
		return beforeInitialConfigCalled;
	}

	@Override
	public boolean isAfterInitialConfigCalled() {
		return afterInitialConfigCalled;
	}

	@Override
	public boolean isBeforeReConfigCalled() {
		return beforeReConfigCalled;
	}

	@Override
	public boolean isAfterReConfigCalled() {
		return afterReConfigCalled;
	}

	@Override
	@BeforeConfiguration public void before(){ beforeConfigCalled = true; }
	@Override
	@BeforeInitialConfiguration public void beforeInitial(){ beforeInitialConfigCalled = true; }
	@Override
	@BeforeReConfiguration public void beforeRe(){ beforeReConfigCalled = true; }

	@Override
	@AfterConfiguration public void after(){ afterConfigCalled = true; }
	@Override
	@AfterInitialConfiguration public void afterInitial(){ afterInitialConfigCalled = true; }
	@Override
	@AfterReConfiguration public void afterRe(){ afterReConfigCalled = true; }

}