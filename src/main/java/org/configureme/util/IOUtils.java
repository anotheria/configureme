package org.configureme.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Utilities for input output.
 *
 * @author anotheria team
 * @version $Id: $Id
 */
@SuppressFBWarnings("DM_DEFAULT_ENCODING")
public final class IOUtils {

	/**
	 * Default constructor with preventing instantiations of this class.
	 */
	private IOUtils() {
		throw new IllegalAccessError("Shouldn't be instantiated.");
	}

	/**
	 * Reads the contents of the file at once and returns the byte array.
	 *
	 * @param file a {@link java.io.File} object.
	 * @throws java.io.IOException if any.
	 * @return an array of byte.
	 */
	public static byte[] readFileAtOnce(final File file) throws IOException {
		final FileInputStream fIn = new FileInputStream(file);
		return readFileAtOnce(fIn);
	}

	/**
	 * Reads the contents of the file at once and returns the byte array.
	 *
	 * @param filename
	 *            name of the file.
	 * @throws java.io.IOException if any.
	 * @return an array of byte.
	 */
	public static byte[] readFileAtOnce(final String filename) throws IOException {
		final FileInputStream fIn = new FileInputStream(filename);
		return readFileAtOnce(fIn);
	}

	/**
	 * Reads the contents of the file input stream. (Why not an InputStream btw?).
	 * 
	 * @param fIn FileStream.
	 * @return
	 * @throws IOException
	 */
	private static byte[] readFileAtOnce(final FileInputStream fIn) throws IOException {
		final byte[] ret = new byte[fIn.available()];
		fIn.read(ret);
		fIn.close();
		return ret;
	}

	/**
	 * Reads a file and returns the contents as string.
	 *
	 * @param filename a {@link java.lang.String} object.
	 * @throws java.io.IOException if any.
	 * @return a {@link java.lang.String} object.
	 */
	public static String readFileAtOnceAsString(final String filename) throws IOException {
		return new String(readFileAtOnce(filename));
	}

	/**
	 * Reads a file and returns the contents as string.
	 *
	 * @param file a {@link java.io.File} object.
	 * @throws java.io.IOException if any.
	 * @return a {@link java.lang.String} object.
	 */
	public static String readFileAtOnceAsString(final File file) throws IOException {
		return new String(readFileAtOnce(file));
	}

	/**
	 * Instead of reading the file at once, reads the file by reading 2K blocks. Useful for reading special files, where the size of the file isn't
	 * determinable, for example /proc/xxx files on linux.
	 *
	 * @param filename a {@link java.lang.String} object.
	 * @return file content.
	 * @throws java.io.IOException if any.
	 */
	public static String readFileBufferedAsString(final String filename) throws IOException {
		FileReader in = null;
		try {
			StringBuilder result = new StringBuilder();
			char[] buffer = new char[2048];
			in = new FileReader(filename);
			int len = 0;
			do {
				len = in.read(buffer);
				if (len > 0)
					result.append(buffer, 0, len);
			} while (len > 0);
			return result.toString();
		} finally {
			closeIgnoringException(in);
		}
	}

	/**
	 * <p>readInputStreamBufferedAsString.</p>
	 *
	 * @param in a {@link java.io.InputStream} object.
	 * @param charset a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 * @throws java.io.IOException if any.
	 */
	public static String readInputStreamBufferedAsString(final InputStream in, final String charset) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new UnicodeReader(in, charset));
			StringBuilder result = new StringBuilder();
			char[] cbuf = new char[2048];
			int read;
			while ((read = reader.read(cbuf)) > 0)
				result.append(cbuf, 0, read);
			return result.toString();
		} finally {
			closeIgnoringException(reader);
		}

	}

	/**
	 * <p>readFileBufferedAsString.</p>
	 *
	 * @param file a {@link java.io.File} object.
	 * @param charset a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 * @throws java.io.IOException if any.
	 */
	public static String readFileBufferedAsString(final File file, final String charset) throws IOException {
		return readInputStreamBufferedAsString(new FileInputStream(file), charset);
	}

	/**
	 * Reads a line from standard input.
	 *
	 * @throws java.io.IOException if any.
	 * @return a {@link java.lang.String} object.
	 */
	public static String readlineFromStdIn() throws IOException {
		StringBuilder ret = new StringBuilder();
		int c;
		while ((c = System.in.read()) != '\n' && c != -1) {
			if (c != '\r')
				ret.append((char) c);
		}
		return ret.toString();
	}

	/**
	 * Closes {@link java.io.Closeable} instance ignoring IOException. Should be called from a finally block whenever {@link java.io.Closeable} is used.
	 *
	 * @param closeable
	 *            to close
	 */
	public static void closeIgnoringException(final Closeable closeable) {
		if (closeable == null)
			return;

		try {
			closeable.close();
		} catch (IOException ignored) {
			// We can do nothing if on close failure
		}
	}

	/**
	 * <p>readBytes.</p>
	 *
	 * @param in a {@link java.io.InputStream} object.
	 * @return an array of byte.
	 * @throws java.io.IOException if any.
	 */
	public static byte[] readBytes(final InputStream in) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384];
		try {
			while ((nRead = in.read(data, 0, data.length)) != -1)
				buffer.write(data, 0, nRead);

			buffer.flush();
			return buffer.toByteArray();
		} finally {
			closeIgnoringException(buffer);
		}
	}
}
