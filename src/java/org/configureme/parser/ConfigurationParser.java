package org.configureme.parser;

public interface ConfigurationParser {
	ParsedArtefact parseArtefact(String name, String content) throws ConfigurationParserException;
}
