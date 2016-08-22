package org.configureme.parser.properties;

import org.configureme.environments.DynamicEnvironment;
import org.configureme.parser.ConfigurationParser;
import org.configureme.parser.ConfigurationParserException;
import org.configureme.parser.ParsedAttribute;
import org.configureme.parser.ParsedConfiguration;
import org.configureme.parser.PlainParsedAttribute;
import org.configureme.util.StringUtils;

/**
 * COnfigurationparser implementation for Property files.
 *
 * @author another
 * @version $Id: $Id
 */
public class PropertiesParser implements ConfigurationParser {

	/** {@inheritDoc} */
	@Override
	public ParsedConfiguration parseConfiguration(String name, String content)
			throws ConfigurationParserException {

		content = StringUtils.removeBashComments(content);

		String[] lines = StringUtils.tokenize(content, '\n');

		ParsedConfiguration configuration = new ParsedConfiguration(name);

		for (String line : lines){
			if (line==null || line.trim().isEmpty())
				continue;
			String[] tokensQL =  StringUtils.tokenize(line, '=');
			if (tokensQL.length!=2){
				throw new IllegalArgumentException("Unparseable content, can't find = in line: "+line);
			}
			String propertyNameLine = tokensQL[0];
			String[] tokensDot = StringUtils.tokenize(propertyNameLine, '.');
			if (tokensDot.length==0){
				throw new IllegalArgumentException("Unparseable content, can't find property name in line: "+line);
			}
			DynamicEnvironment env = new DynamicEnvironment();
			String propertyName = tokensDot[tokensDot.length-1];
			String propertyValue = tokensQL[1];
			for (int i=0; i<tokensDot.length-1; i++)
				env.add(tokensDot[i]);

			ParsedAttribute<?> pa = new PlainParsedAttribute(propertyName, env, propertyValue);
			configuration.addAttribute(pa);
		}

		return configuration;
	}

}
