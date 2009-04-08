package org.configureme.parser;

public interface ConfigurationParser {
	ParsedConfiguration parseArtefact(String name, String content) throws ConfigurationParserException;
}
