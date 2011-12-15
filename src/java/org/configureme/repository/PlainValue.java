package org.configureme.repository;

/**
 * Bare value of plain attributes in a string representation.
 * Suitable for attributes representing atomic values such as strings, booleans and all kind of numbers.
 */
public class PlainValue implements Value {
	/**
	 * String representation of the value.
	 */
	private final String str;

	/**
	 * Constructs new plain attribute value.
	 * @param value string representation of the value.
	 */
	public PlainValue(String value) {
		str = value;
	}

	/**
	 * Gets the value
	 * @return string representation of the value.
	 */
	public String get() {
		return str;
	}

	@Override
	public String toString() {
		return String.valueOf(str);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((str == null) ? 0 : str.hashCode());
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
		if (!(obj instanceof PlainValue)) {
			return false;
		}
		PlainValue other = (PlainValue) obj;
		if (str == null) {
			if (other.str != null) {
				return false;
			}
		} else if (!str.equals(other.str)) {
			return false;
		}
		return true;
	}
}
