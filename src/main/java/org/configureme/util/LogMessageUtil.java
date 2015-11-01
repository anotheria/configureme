package org.configureme.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * Utility class for preparing formalized log message from exception and current method with argument's.
 * 
 * @author anotheria team
 */
public final class LogMessageUtil {

	/**
	 * Element's delimiter.
	 */
	public static final String DELIMITER = ",";

	/**
	 * Size-to-collection/array/map delimiter.
	 */
	public static final String DELIMITER_LENGTH = "=";

	/**
	 * Map key-value delimiter.
	 */
	public static final String DELIMITER_MAP_ELEMENT = "=";

	/**
	 * Empty string.
	 */
	public static final String STR_EMPTY = "";

	/**
	 * Fail message postfix.
	 */
	public static final String STR_FAIL = " fail.";

	/**
	 * Null object string.
	 */
	public static final String STR_NULL = "null";

	/**
	 * Skipped string part marker.
	 */
	public static final String STR_SKIP = "...";

	/**
	 * Unknown method marker.
	 */
	public static final String STR_UNKNOWN_METHOD = "UNKNOWN";

	/**
	 * Maximum length of argument object string representation.
	 */
	public static final int MAX_ARGUMENT_LENGTH = 255;

	/**
	 * Default constructor for preventing illegal instantiations of this class.
	 */
	private LogMessageUtil() {
		throw new IllegalAccessError("Shouldn't be instantiated.");
	}

	/**
	 * Prepare fail message.
	 * 
	 * @param t
	 *            - exception, cause of this message
	 * @param objects
	 *            - method argument's
	 * @return {@link String} prepared message
	 */
	public static String failMsg(final Throwable t, final Object... objects) {
		String methodName = STR_UNKNOWN_METHOD;
		StackTraceElement[] stes = t.getStackTrace();
		if (t != null && stes != null && stes.length > 0 && stes[0] != null)
			methodName = stes[0].getMethodName();

		if (objects == null || objects.length == 0)
			return methodName + "()" + STR_FAIL;

		StringBuffer arguments = new StringBuffer("(");
		int processed = 0;
		for (Object obj : objects) {
			if (processed > 0)
				arguments.append(DELIMITER);

			processed++;
			if (obj == null) {
				arguments.append(STR_NULL);
				continue;
			}

			if (obj.toString().isEmpty()) {
				arguments.append(STR_EMPTY);
				continue;
			}

			// collection's support
			if (obj instanceof Collection<?>) {
				Collection<?> collection = (Collection<?>) obj;
				arguments.append(collection.size() + DELIMITER_LENGTH + "[");
				arguments.append(crop(collectionToString(collection, true), MAX_ARGUMENT_LENGTH));
				arguments.append("]");
				continue;
			}

			// map's support
			if (obj instanceof Map<?, ?>) {
				Map<?, ?> map = (Map<?, ?>) obj;
				arguments.append(map.size() + DELIMITER_LENGTH + "{");
				arguments.append(crop(mapToString(map, true), MAX_ARGUMENT_LENGTH));
				arguments.append("}");
				continue;
			}

			// arrays support
			if (obj.getClass() != null && obj.getClass().isArray()) {
				int length = Array.getLength(obj);
				arguments.append(length + DELIMITER_LENGTH + "[");
				arguments.append(crop(arrayToString(obj, true), MAX_ARGUMENT_LENGTH));
				arguments.append("]");
				continue;
			}

			arguments.append(crop(obj, MAX_ARGUMENT_LENGTH));

		}
		arguments.append(")");

		return methodName + arguments.toString() + STR_FAIL;
	}

	/**
	 * Get collection string representation.
	 * 
	 * @param collection
	 *            - collection
	 * @param isTop
	 *            - is top element in recursion
	 * @return {@link String} collection string representation
	 */
	private static String collectionToString(final Collection<?> collection, final boolean isTop) {
		if (collection == null)
			return STR_NULL;

		Object[] collectionArray = collection.toArray();
		int length = collectionArray.length;
		StringBuilder result = !isTop ? new StringBuilder("[") : new StringBuilder();
		for (int idx = 0; idx < length; idx++) {
			if (idx > 0)
				result.append(DELIMITER);

			final Object item = collectionArray[idx];
			result.append(objToString(item, false));
		}

		if (!isTop)
			result.append("]");

		return result.toString();
	}

	/**
	 * Get map string representation.
	 * 
	 * @param map
	 *            - map
	 * @param isTop
	 *            - is top element in recursion
	 * @return {@link String} map string representation
	 */
	private static String mapToString(final Map<?, ?> map, final boolean isTop) {
		if (map == null)
			return STR_NULL;

		Object[] keyset = map.keySet().toArray();
		int length = keyset.length;
		StringBuilder result = !isTop ? new StringBuilder("{") : new StringBuilder();
		for (int idx = 0; idx < length; idx++) {
			if (idx > 0)
				result.append(DELIMITER);

			final Object key = keyset[idx];
			final Object item = map.get(key);
			if (item == null) {
				result.append(key + DELIMITER_MAP_ELEMENT + STR_NULL);
				continue;
			}

			if (item.toString().isEmpty()) {
				result.append(key + DELIMITER_MAP_ELEMENT + STR_EMPTY);
				continue;
			}

			result.append(key + DELIMITER_MAP_ELEMENT + objToString(item, false));
		}

		if (!isTop)
			result.append("}");

		return result.toString();
	}

	/**
	 * Get array string representation.
	 * 
	 * @param array
	 *            - array
	 * @param isTop
	 *            - is top element in recursion
	 * @return {@link String} array string representation
	 */
	private static String arrayToString(final Object array, final boolean isTop) {
		if (array == null)
			return STR_NULL;

		int length = Array.getLength(array);
		StringBuilder result = !isTop ? new StringBuilder("[") : new StringBuilder();
		for (int idx = 0; idx < length; idx++) {
			if (idx > 0)
				result.append(DELIMITER);

			final Object item = Array.get(array, idx);
			result.append(objToString(item, false));
		}

		if (!isTop)
			result.append("]");

		return result.toString();
	}

	/**
	 * Convert object to string representation by it's type.
	 * 
	 * @param obj
	 *            - object
	 * @param isTop
	 *            - is top element
	 * @return {@link String} object representation
	 */
	private static String objToString(final Object obj, final boolean isTop) {
		if (obj == null)
			return STR_NULL;

		if (obj.toString().isEmpty())
			return STR_EMPTY;

		if (obj.getClass().isArray())
			return arrayToString(obj, isTop);

		if (obj instanceof Collection<?>)
			return collectionToString((Collection<?>) obj, isTop);

		if (obj instanceof Map<?, ?>)
			return mapToString((Map<?, ?>) obj, isTop);

		return obj.toString();
	}

	/**
	 * Crop object string representation to maximum length.
	 * 
	 * @param obj
	 *            - object
	 * @param maxLength
	 *            - maximum length
	 * @return {@link String} cropped text
	 */
	private static final String crop(final Object obj, final int maxLength) {
		if (obj == null)
			return STR_NULL;

		String text = obj.toString();
		if (text.length() <= maxLength)
			return text;

		return text.substring(0, maxLength) + STR_SKIP;
	}

}
