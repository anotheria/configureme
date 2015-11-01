package org.configureme.pojo;

import org.configureme.annotations.Configure;

/**
 * Created by IntelliJ IDEA.
 *
 * @author <a href="mailto:vzhovtiuk@anotheria.net">Vitaliy Zhovtiuk</a>
 *         Date: 12/8/11
 *         Time: 6:07 PM
 *         To change this template use File | Settings | File Templates.
 */
public class InnerTestPojo {
	@Configure
    private String stringValue;

	@Configure
    private boolean booleanValue;

    public InnerTestPojo() {
    }

    public InnerTestPojo(String stringValue, boolean booleanValue) {
		this.stringValue = stringValue;
		this.booleanValue = booleanValue;
	}

	public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public boolean isBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

	@Override
	public String toString() {
		return "InnerTestPojo [stringValue=" + stringValue
				+ ", booleanValue=" + booleanValue + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (booleanValue ? 1231 : 1237);
		result = prime * result + ((stringValue == null)
				? 0
				: stringValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof InnerTestPojo)) {
			return false;
		}
		InnerTestPojo other = (InnerTestPojo) obj;
		if (booleanValue != other.booleanValue) {
			return false;
		}
		if (stringValue == null) {
			if (other.stringValue != null) {
				return false;
			}
		} else if (!stringValue.equals(other.stringValue)) {
			return false;
		}
		return true;
	}
}
