package org.configureme.parser;

public class ConfigurationParserException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public ConfigurationParserException(String message) {
		super(message);
	}

	public ConfigurationParserException(String message, Throwable cause) {
		super(message, cause);
	}
}
