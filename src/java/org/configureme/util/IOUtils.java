package org.configureme.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utilities for input output.
 * 
 * @author anotheria team
 */
public final class IOUtils {

	/**
	 * Reads the contents of the file at once and returns the byte array.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] readFileAtOnce(File file) throws IOException {
		FileInputStream fIn = new FileInputStream(file);
		return readFileAtOnce(fIn);
	}

	/**
	 * Reads the contents of the file at once and returns the byte array.
	 * 
	 * @param filename
	 *            name of the file.
	 * @return
	 * @throws IOException
	 */
	public static byte[] readFileAtOnce(String filename) throws IOException {
		FileInputStream fIn = new FileInputStream(filename);
		return readFileAtOnce(fIn);
	}

	/**
	 * Reads the contents of the file input stream. (Why not an InputStream btw?).
	 * 
	 * @param fIn
	 * @return
	 * @throws IOException
	 */
	private static byte[] readFileAtOnce(FileInputStream fIn) throws IOException {
		byte[] ret = new byte[fIn.available()];
		fIn.read(ret);
		fIn.close();
		return ret;
	}

	/**
	 * Reads a file and returns the contents as string.
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static String readFileAtOnceAsString(String filename) throws IOException {
		return new String(readFileAtOnce(filename));
	}

	/**
	 * Reads a file and returns the contents as string.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String readFileAtOnceAsString(File file) throws IOException {
		return new String(readFileAtOnce(file));
	}

	/**
	 * Instead of reading the file at once, reads the file by reading 2K blocks. Useful for reading special files, where the size of the file isn't
	 * determinable, for example /proc/xxx files on linux.
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static String readFileBufferedAsString(String filename) throws IOException {
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

	public static String readInputStreamBufferedAsString(InputStream in, String charset) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new UnicodeReader(in, charset));
			StringBuffer result = new StringBuffer();
			char[] cbuf = new char[2048];
			int read;
			while ((read = reader.read(cbuf)) > 0)
				result.append(cbuf, 0, read);
			return result.toString();
		} finally {
			closeIgnoringException(reader);
		}

	}

	public static String readFileBufferedAsString(File file, String charset) throws IOException {
		return readInputStreamBufferedAsString(new FileInputStream(file), charset);
	}

	/**
	 * Reads a line from standard input.
	 * 
	 * @return
	 * @throws IOException
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
	 * Closes {@link Closeable} instance ignoring IOException. Should be called from a finally block whenever {@link Closeable} is used.
	 * 
	 * @param closeable
	 *            to close
	 */
	public static void closeIgnoringException(Closeable closeable) {
		if (closeable != null)
			try {
				closeable.close();
			} catch (IOException ignored) {
				// We can do nothing if on close failure
			}
	}

	public static byte[] readBytes(InputStream in) throws IOException {
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
