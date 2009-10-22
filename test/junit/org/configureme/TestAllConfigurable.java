package org.configureme;

import net.anotheria.util.StringUtils;

import org.configureme.annotations.ConfigureMe;
import org.configureme.sources.ConfigurationSourceKey;

@ConfigureMe(name="fixture", type=ConfigurationSourceKey.Type.FIXTURE, watch=false, allfields=true)
public class TestAllConfigurable {
	private short shortValue;
	private long longValue;
	private int intValue;
	private boolean booleanValue;
	private String stringValue;
	private byte byteValue;
	private float floatValue;
	private double doubleValue ;
	private String[] stringArrayValue;
	
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
}
