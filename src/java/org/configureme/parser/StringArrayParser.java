package org.configureme.parser;

import net.anotheria.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This is a parser for the string representation of the arrays.
 * For example array "12,24,123" - store int/Integer or string, "str1,str2" - store strings, etc.
 * 
 * @author vkazhdan
 */
public final class StringArrayParser {

	/**
	 * Delimiter for the arrays string representation.
	 */
	public static final String STRING_ARRAY_DELIM = ",";
	
	/**
	 * The encoded version of the delimiter for the arrays string representation.
	 */
	public static final String STRING_ARRAY_DELIM_CODE = "{COMMA}";
	
	/**
	 * Hidden constructor.
	 */
	private StringArrayParser() {
	}
	
	/**
	 * Create string array from the array of objects.
	 * NOTE: STRING_ARRAY_DELIM will be encoded to STRING_ARRAY_DELIM_CODE, use only parseXxxArray to decode correctly.
	 * 
	 * @param array array of objects
	 * @return string representation of the array
	 */
	public static String toStringArray(final Object[] array) {
		if (array == null) {
			return "";
		}
		
		StringBuilder builder = new StringBuilder("");
		if(array.length > 0) {
			// Add first element value with delimiter encoding
			builder.append(array[0].toString().replace(STRING_ARRAY_DELIM, STRING_ARRAY_DELIM_CODE));
			// Add all other elements with delimiter encoding
			for (int i = 1; i < array.length; i++) {
				builder.append(STRING_ARRAY_DELIM).append(array[i].toString().replace(STRING_ARRAY_DELIM, STRING_ARRAY_DELIM_CODE));
			}
		}
		
		return builder.toString(); 
	}
	
	/**
	 * Parse string array to array of strings. 
	 * NOTE: STRING_ARRAY_DELIM will be decoded from STRING_ARRAY_DELIM_CODE.
	 * 
	 * @param source string array. SHOULD BE the result of the toStringArray() call.
	 * @return parsed array of strings
	 */
	public static String[] parseStringArray(final String source) {
		List<String> list = new ArrayList<String>();
		if(StringUtils.isEmpty(source)) {
			return (String[]) list.toArray(new String[]{});
		}
		
		// Parse string array source, decoding delimiters
		StringTokenizer tokenizer = new StringTokenizer(source, STRING_ARRAY_DELIM);
		
		while(tokenizer.hasMoreTokens()) {
			list.add(tokenizer.nextToken().replace(STRING_ARRAY_DELIM_CODE, STRING_ARRAY_DELIM));
		}
		
		return list.toArray(new String[]{});
	}
	
	/**
	 * Parse int array. 
	 * See also {@link #parseStringArray(String)}.
	 * 
	 * @param source array source
	 * @return int array
	 */
	public static int[] parseIntArray(final String source) {
		String[] strings = parseStringArray(source);
		int[] result = new int[strings.length];
		
		for (int i = 0; i < strings.length; i++) {
			result[i] = Integer.valueOf(strings[i].trim());
		}
		
		return result;
	}
	
	/**
	 * Parse boolean array. 
	 * See also {@link #parseStringArray(String)}.
	 * 
	 * @param source array source
	 * @return boolean array
	 */
	public static boolean[] parseBooleanArray(final String source) {
		String[] strings = parseStringArray(source);
		boolean[] result = new boolean[strings.length];
		
		for (int i = 0; i < strings.length; i++) {
			result[i] = Boolean.valueOf(strings[i].trim());
		}
		
		return result;
	}
	
	/**
	 * Parse short array. 
	 * See also {@link #parseStringArray(String)}.
	 * 
	 * @param source array source
	 * @return short array
	 */
	public static short[] parseShortArray(final String source) {
		String[] strings = parseStringArray(source);
		short[] result = new short[strings.length];
		
		for (int i = 0; i < strings.length; i++) {
			result[i] = Short.valueOf(strings[i].trim());
		}
		
		return result;
	}
	
	/**
	 * Parse long array. 
	 * See also {@link #parseStringArray(String)}.
	 * 
	 * @param source array source
	 * @return long array
	 */
	public static long[] parseLongArray(final String source) {
		String[] strings = parseStringArray(source);
		long[] result = new long[strings.length];
		
		for (int i = 0; i < strings.length; i++) {
			result[i] = Long.valueOf(strings[i].trim());
		}
		
		return result;
	}
	
	/**
	 * Parse byte array. 
	 * See also {@link #parseStringArray(String)}.
	 * 
	 * @param source array source
	 * @return byte array
	 */
	public static byte[] parseByteArray(final String source) {
		String[] strings = parseStringArray(source);
		byte[] result = new byte[strings.length];
		
		for (int i = 0; i < strings.length; i++) {
			result[i] = Byte.valueOf(strings[i].trim());
		}
		
		return result;
	}
	
	/**
	 * Parse float array. 
	 * See also {@link #parseStringArray(String)}.
	 * 
	 * @param source array source
	 * @return float array
	 */
	public static float[] parseFloatArray(final String source) {
		String[] strings = parseStringArray(source);
		float[] result = new float[strings.length];
		
		for (int i = 0; i < strings.length; i++) {
			result[i] = Float.valueOf(strings[i].trim());
		}
		
		return result;
	}
	
	/**
	 * Parse double array. 
	 * See also {@link #parseStringArray(String)}.
	 * 
	 * @param source array source
	 * @return double array
	 */
	public static double[] parseDoubleArray(final String source) {
		String[] strings = parseStringArray(source);
		double[] result = new double[strings.length];
		
		for (int i = 0; i < strings.length; i++) {
			result[i] = Double.valueOf(strings[i].trim());
		}
		
		return result;
	}
}
