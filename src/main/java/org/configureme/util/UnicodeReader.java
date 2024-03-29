package org.configureme.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 * Generic unicode text reader, which will use BOM mark to identify the encoding to be used.<br>
 * If BOM is not found then use a given default or system encoding.
 *
 * @author another
 * @version $Id: $Id
 */
@SuppressWarnings("PMD.UselessParentheses")
public class UnicodeReader extends Reader {
	/**
	 * Byte order mark size.
	 */
	private static final int BOM_SIZE = 4;

	private final PushbackInputStream internalIn;
	private InputStreamReader internalIn2 = null;
	/**
	 * Charset of this instance.
	 */
	private final Charset charset;

	/**
	 * Constructor for UnicodeReader.
	 *
	 * @param in a {@link java.io.InputStream} object.
	 */
	public UnicodeReader(InputStream in) {
		this(in, Charset.defaultCharset());
	}

	/**
	 * <p>Constructor for UnicodeReader.</p>
	 *
	 * @param in a {@link java.io.InputStream} object.
	 * @param charsetName a {@link java.lang.String} object.
	 */
	public UnicodeReader(InputStream in, String charsetName) {
		this(in, Charset.forName(charsetName));
	}

	/**
	 * Constructor for UnicodeReader.
	 *
	 * @param aIn a {@link java.io.InputStream} object.
	 * @param aCharset a {@link java.nio.charset.Charset} object.
	 */
	public UnicodeReader(InputStream aIn, Charset aCharset) {
		this.internalIn = new PushbackInputStream(aIn, BOM_SIZE);
		this.charset = aCharset;
	}

	/**
	 * <p>getEncoding.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getEncoding() {
		if (internalIn2 == null)
			return null;
		return internalIn2.getEncoding();
	}

	/**
	 * <p>init.</p>
	 *
	 * @throws java.io.IOException if any.
	 */

	protected void init() throws IOException {
		if (internalIn2 != null)
			return;

		Charset preciseCharset;
		byte bom[] = new byte[BOM_SIZE];
		int n, unread;
		n = internalIn.read(bom, 0, bom.length);

		if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF)) {
			preciseCharset = Charset.forName("UTF-32BE");
			unread = n - 4;
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00)) {
			preciseCharset = Charset.forName("UTF-32LE");
			unread = n - 4;
		} else if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF)) {
			preciseCharset = Charset.forName("UTF-8");
			unread = n - 3;
		} else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
			preciseCharset = Charset.forName("UTF-16BE");
			unread = n - 2;
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
			preciseCharset = Charset.forName("UTF-16LE");
			unread = n - 2;
		} else {
			// Unicode BOM mark not found, unread all bytes
			preciseCharset = charset;
			unread = n;
		}
		if (unread > 0)
			internalIn.unread(bom, (n - unread), unread);

		// Use given encoding
		internalIn2 = preciseCharset == null ? new InputStreamReader(internalIn) : new InputStreamReader(internalIn, preciseCharset);
	}

	/**
	 * <p>close.</p>
	 *
	 * @throws java.io.IOException if any.
	 */
	@Override
	public void close() throws IOException {
		init();
		internalIn2.close();
	}

	@Override
	public int read(final char[] cbuf, final int off, final int len) throws IOException {
		init();
		return internalIn2.read(cbuf, off, len);
	}

}
