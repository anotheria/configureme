package org.configureme.parser;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

/**
 * StringArrayParser Test.
 * 
 * @author vkazhdan
 */
public class StringArrayParserTest {

	@Test
	public void testToStringArray() {
		assertEquals("s1,s2", StringArrayParser.toStringArray(new String[] {"s1", "s2"}));
		assertEquals("s1.1 " + StringArrayParser.STRING_ARRAY_DELIM_CODE + " s1.2,s2", StringArrayParser.toStringArray(new String[] {"s1.1 , s1.2", "s2"}));
	}

	@Test
	public void testParseArray() {
		// String array
		assertTrue(Arrays.equals(new String[] {"s1", "s2"}, StringArrayParser.parseStringArray("s1,s2")));
		assertTrue(Arrays.equals(new String[] {"s1.1 , s1.2", "s2"}, StringArrayParser.parseStringArray("s1.1 " + StringArrayParser.STRING_ARRAY_DELIM_CODE + " s1.2,s2")));
		// Int array
		assertTrue(Arrays.equals(new int[]{}, StringArrayParser.parseIntArray("")));
		assertTrue(Arrays.equals(new int[] {1, -3, 4}, StringArrayParser.parseIntArray("1,-3, 4")));
		// Boolean array
		assertTrue(Arrays.equals(new boolean[] {true, false}, StringArrayParser.parseBooleanArray("true,false")));
		// Short array
		assertTrue(Arrays.equals(new short[] {1, 3, 4}, StringArrayParser.parseShortArray("1,3, 4")));
		// Long array
		assertTrue(Arrays.equals(new long[] {1, 3, 4}, StringArrayParser.parseLongArray("1,3, 4")));
		// Byte array
		assertTrue(Arrays.equals(new byte[] {1, 3, 4}, StringArrayParser.parseByteArray("1,3, 4")));
		// Float array
		assertTrue(Arrays.equals(new float[] {1, 3, 4.5F}, StringArrayParser.parseFloatArray("1,3, 4.5")));
		// Double array
		assertTrue(Arrays.equals(new double[] {1, 3, 4.5D}, StringArrayParser.parseDoubleArray("1,3, 4.5")));
	}

}
