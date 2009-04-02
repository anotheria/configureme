package org.configureme.parser;

public class ConfigurationParserException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public ConfigurationParserException(String message) {
		super(message);
	}

	public ConfigurationParserException() {
		super();
	}

	public ConfigurationParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigurationParserException(Throwable cause) {
		super(cause);
	}

}
