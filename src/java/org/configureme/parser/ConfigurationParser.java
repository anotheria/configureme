package org.configureme.parser;

/**
 * The configuration parser interfaces defines a configuration parser for a special configuration format.
 * As of now only JSON format is supported. XML and property-files are planed in the near feature. 
 * @author lrosenberg
 */
public interface ConfigurationParser {
	/**
	 * Returns the parsed configuration. 
	 * @param name name of the configuration. Needed because its contained in the container name and not in the source content and hence isn't accessible by the parser.
	 * @param content the content of the configuration source (file or whatever).
	 * @return ParsedConfiguration object
	 * @throws ConfigurationParserException if the file is not parseable
	 */
	ParsedConfiguration parseConfiguration(String name, String content) throws ConfigurationParserException;
}
