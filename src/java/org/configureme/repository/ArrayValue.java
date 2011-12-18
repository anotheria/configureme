package org.configureme.repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Sequence of values representing an array attribute.
 * Contains arbitrary sequence child attribute values of the same type.
 */
public class ArrayValue implements Value {
	/**
	 * List of child attribute values
	 */
	private final List<Value> list;

	/**
	 * Constructs new array attribute value.
	 * @param value list of child attribute values.
	 */
	public ArrayValue(List<Value> value) {
		list = value;
	}

	/**
	 * Gets list of child attribute values
	 * @return list of child attribute values
	 */
	public List<Value> get() {
		return list;
	}

	@Override
	public Object getRaw() {
		List<Object> raw = new ArrayList<Object>(list.size());
		for (Value val : list)
			raw.add(val.getRaw());

		return raw;
	}

	@Override
	public String toString() {
		return String.valueOf(list);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((list == null) ? 0 : list.hashCode());
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
		if (!(obj instanceof ArrayValue)) {
			return false;
		}
		ArrayValue other = (ArrayValue) obj;
		if (list == null) {
			if (other.list != null) {
				return false;
			}
		} else if (!list.equals(other.list)) {
			return false;
		}
		return true;
	}
}
