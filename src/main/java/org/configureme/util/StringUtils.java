package org.configureme.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Small but useful utilities for {@link String} handling.
 * 
 * @author anotheria team
 */
public final class StringUtils {

	/**
	 * Empty {@link String} constant.
	 */
	public static final String EMPTY = "";

	/**
	 * Default constructor with preventing instantiations of this class.
	 */
	private StringUtils() {
		throw new IllegalAccessError("Shouldn't be instantiated.");
	}

	/**
	 * Is {@link String} value <code>null</code> or empty (length is '0' after trim).
	 * 
	 * @param value
	 *            {@link String} value
	 * @return <code>true</code> if empty or <code>false</code>
	 */
	public static boolean isEmpty(final String value) {
		return value == null || value.trim().isEmpty();
	}

	/**
	 * Is {@link String} value not <code>null</code> and not empty (length not '0' after trim).
	 * 
	 * @param value
	 *            {@link String} value
	 * @return <code>true</code> if not empty or <code>false</code>
	 */
	public static boolean isNotEmpty(final String value) {
		return !isEmpty(value);
	}

	/**
	 * Return a Vector with tokens from the source string tokenized using the delimiter char.
	 * 
	 * @param source
	 *            source string
	 * @param delimiter
	 *            token delimiter
	 * @return {@link Vector} with {@link String}
	 */
	public static final Vector<String> tokenize2vector(final String source, final char delimiter) {
		Vector<String> v;
		v = new Vector<String>();
		StringBuilder currentS = new StringBuilder();
		char c;
		for (int i = 0; i < source.length(); i++) {
			c = source.charAt(i);
			if (c == delimiter) {
				if (currentS.length() > 0) {
					v.addElement(currentS.toString());
				} else {
					v.addElement("");
				}
				currentS = new StringBuilder();
			} else {
				currentS.append(c);
			}
		}
		if (currentS != null && currentS.length() > 0)
			v.addElement(currentS.toString());
		return v;
	}

	/**
	 * Returns an array of string tokens from the source string.<br>
	 * The String "Leon Power Tools" with delimiter ' ' will return {"Leon","Power","Tools"}.
	 * 
	 * @param source
	 *            source string
	 * @param delimiter
	 *            token delimiter
	 * @return array of {@link String}
	 */
	public static final String[] tokenize(final String source, final char delimiter) {
		String[] ret;
		Vector<String> v = tokenize2vector(source, delimiter);
		ret = new String[v.size()];
		for (int i = 0; i < v.size(); i++) {
			ret[i] = (String) v.elementAt(i);
		}
		return ret;
	}

	/**
	 * Get {@link String} before given search {@link String} from given start search index.
	 * 
	 * @param src
	 *            source string
	 * @param toSearch
	 *            search string
	 * @param start
	 *            start search index
	 * @return {@link String}
	 */
	public static String getStringBefore(final String src, final String toSearch, final int start) {
		int ind = src.indexOf(toSearch, start);
		if (ind == -1)
			return "";

		return src.substring(start, ind);
	}

	/**
	 * Get {@link String} before given search {@link String}.
	 * 
	 * @param src
	 *            source string
	 * @param toSearch
	 *            search string
	 * @return {@link String}
	 */
	public static String getStringBefore(final String src, final String toSearch) {
		return getStringBefore(src, toSearch, 0);
	}

	/**
	 * Get {@link String} after given search {@link String} from given start search index.
	 * 
	 * @param src
	 *            source string
	 * @param toSearch
	 *            search string
	 * @param start
	 *            start search index
	 * @return {@link String}
	 */
	public static String getStringAfter(final String src, final String toSearch, final int start) {
		int ind = src.indexOf(toSearch, start);
		if (ind == -1)
			return "";
		return src.substring(ind + toSearch.length());
	}

	/**
	 * Get {@link String} after given search {@link String}.
	 * 
	 * @param src
	 *            source string
	 * @param toSearch
	 *            search string
	 * @return {@link String}
	 */
	public static String getStringAfter(final String src, final String toSearch) {
		return getStringAfter(src, toSearch, 0);
	}

	/**
	 * Remove 'C' commentaries.
	 * 
	 * @param src
	 *            source string
	 * @return processed {@link String}
	 */
	public static String removeCComments(final String src) {
		final StringBuilder ret = new StringBuilder();
		boolean inComments = false;
		for (int i = 0; i < src.length(); i++) {
			char c = src.charAt(i);
			if (inComments) {
				if (c == '*' && src.charAt(i + 1) == '/') {
					inComments = false;
					i++;
				}
			} else {
				if (c == '/') {
					if (src.charAt(i + 1) == '*') {
						inComments = true;
						i++;
					} else {
						ret.append(c);
					}
				} else
					ret.append(c);
			}
		}

		return ret.toString();
	}

	/**
	 * Remove 'CPP' commentaries.
	 * 
	 * @param src
	 *            source string
	 * @return processed {@link String}
	 */
	public static String removeCPPComments(final String src) {
		final StringBuilder ret = new StringBuilder();
		boolean inComments = false;
		boolean inQuotes = false;
		for (int i = 0; i < src.length(); i++) {
			char c = src.charAt(i);
			if (inComments) {
				if (c == '\n') {
					inComments = false;
					inQuotes = false;
				}
			} else {
				if (c == '"')
					inQuotes = !inQuotes;
				if (!inQuotes && c == '/') {
					if (src.charAt(i + 1) == '/') {
						inComments = true;
						i++;
					} else {
						ret.append(c);
					}
				} else
					ret.append(c);
			}
		}

		return ret.toString();
	}

	/**
	 * Remove 'Bash' commentaries.
	 * 
	 * @param src
	 *            source string
	 * @return processed {@link String}
	 */
	public static final String removeBashComments(final String src) {
		final StringBuilder ret = new StringBuilder();
		boolean inComments = false;
		boolean inQuotes = false;
		for (int i = 0; i < src.length(); i++) {
			char c = src.charAt(i);
			if (inComments) {
				if (c == '\n') {
					inComments = false;
					inQuotes = false;
				}
			} else {
				if (c == '"')
					inQuotes = !inQuotes;
				if (!inQuotes && c == '#') {
					inComments = true;
				} else {
					ret.append(c);
				}
			}
		}

		return ret.toString();
	}

	/**
	 * Extract tags from source {@link String}.
	 * 
	 * @param source
	 *            source string
	 * @param tagStart
	 *            start tag
	 * @param tagEnd
	 *            end tag
	 * @return {@link List} of {@link String}
	 */
	public static List<String> extractTags(final String source, final char tagStart, final char tagEnd) {
		final ArrayList<String> ret = new ArrayList<String>();
		String currentTag = null;
		boolean inTag = false;
		char c;
		for (int i = 0, l = source.length(); i < l; i++) {
			c = source.charAt(i);
			if (!inTag) {
				if (c == tagStart) {
					currentTag = "" + c;
					inTag = true;
				}
			} else {
				currentTag += c;
				if (c == tagEnd) {
					inTag = false;
					ret.add(currentTag);
					currentTag = null;
				}
			}
		}

		return ret;
	}

	/**
	 * Replace once in source {@link String} some {@link String} with new {@link String}.
	 * 
	 * @param src
	 *            source string
	 * @param toReplace
	 *            string to replace
	 * @param with
	 *            new string
	 * @return {@link String} after replacement
	 */
	public static String replaceOnce(final String src, final String toReplace, final String with) {
		int index = src.indexOf(toReplace);
		if (index == -1)
			return src;

		String s = src.substring(0, index);
		s += with;
		s += src.substring(index + toReplace.length(), src.length());
		return s;
	}

}
