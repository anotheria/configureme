package org.configureme.parser;

/**
 * The base exception class for exceptions which can be thrown by the configuration parser.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public class ConfigurationParserException extends Exception {

	/**
	 * Unneeded value.
	 */
	private static final long serialVersionUID = 2424976193145404622L;

	/**
	 * Creates a new ConfigurationParserException with the given message.
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public ConfigurationParserException(String message) {
		super(message);
	}

	/**
	 * Creates a new ConfigurationParserException with given message and cause.
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param cause a {@link java.lang.Throwable} object.
	 */
	public ConfigurationParserException(String message, Throwable cause) {
		super(message, cause);
	}
}
