package org.configureme.parser;

/**
 * The base exception class for exceptions which can be thrown by the configuration parser. 
 * @author lrosenberg
 */
public class ConfigurationParserException extends Exception {

	/**
	 * Creates a new ConfigurationParserException with the given message.
	 * @param message
	 */
	public ConfigurationParserException(String message) {
		super(message);
	}

	/**
	 * Creates a new ConfigurationParserException with given message and cause.
	 * @param message 
	 * @param cause
	 */
	public ConfigurationParserException(String message, Throwable cause) {
		super(message, cause);
	}
}
