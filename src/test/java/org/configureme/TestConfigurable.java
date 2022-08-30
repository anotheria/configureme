package org.configureme;

import java.util.Arrays;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ConfigureMe(name="fixture", type=ConfigurationSourceKey.Type.FIXTURE, watch=false)
public class TestConfigurable {
	@Configure private short shortValue;
	@Configure private long longValue;
	@Configure private int intValue;
	@Configure private boolean booleanValue;
	@Configure private String stringValue;
	@Configure private byte byteValue;
	@Configure private float floatValue;
	@Configure private double doubleValue;
	@Configure private int onlyInA;
	@Configure private int onlyInB;
	@Configure private String[] stringArray;
	@Configure private float[] floatArray;
	@Configure private int[] intArray;
	@Configure private boolean[] booleanArray;
	@Configure private int[] emptyIntArray;

	private static Logger log = LoggerFactory.getLogger(TestConfigurable.class);

	private boolean beforeConfigCalled, afterConfigCalled, beforeInitialConfigCalled, afterInitialConfigCalled, beforeReConfigCalled, afterReConfigCalled;

	@Override
	public String toString() {
		return "TestConfigurable [shortValue=" + shortValue + ", longValue=" + longValue + ", intValue=" + intValue + ", booleanValue=" + booleanValue + ", stringValue=" + stringValue
				+ ", byteValue=" + byteValue + ", floatValue=" + floatValue + ", doubleValue=" + doubleValue + ", onlyInA=" + onlyInA + ", onlyInB=" + onlyInB
				+ ", stringArray=" + Arrays.toString(stringArray) + ", floatArray=" + Arrays.toString(floatArray) + ", intArray=" + Arrays.toString(intArray)
				+ ", booleanArray=" + Arrays.toString(booleanArray) + ", emptyIntArray=" + Arrays.toString(emptyIntArray) + "]";
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
	public String[] getStringArray() {
		return stringArray;
	}

	public void setStringArray(String[] stringArray) {
		this.stringArray = stringArray;
	}

	public float[] getFloatArray() {
		return floatArray;
	}

	public void setFloatArray(float[] floatArray) {
		this.floatArray = floatArray;
	}

	public int[] getIntArray() {
		return intArray;
	}

	public void setIntArray(int[] intArray) {
		this.intArray = intArray;
	}

	public boolean[] getBooleanArray() {
		return booleanArray;
	}

	public void setBooleanArray(boolean[] booleanArray) {
		this.booleanArray = booleanArray;
	}

	public int[] getEmptyIntArray() {
		return emptyIntArray;
	}

	public void setEmptyIntArray(int[] emptyIntArray) {
		this.emptyIntArray = emptyIntArray;
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
		log.debug("Config property "+name+" = "+property);
		//System.out.println("Config property "+name+" = "+property);
	}
}
