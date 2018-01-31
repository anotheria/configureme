package org.configureme.parser.properties;

import org.configureme.environments.DynamicEnvironment;
import org.configureme.parser.ConfigurationParser;
import org.configureme.parser.ConfigurationParserException;
import org.configureme.parser.ParsedAttribute;
import org.configureme.parser.ParsedConfiguration;
import org.configureme.parser.PlainParsedAttribute;
import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.util.StringUtils;

/**
 * Configuration parser implementation for Property files.
 *
 * @author another
 * @version $Id: $Id
 */
public class PropertiesParser implements ConfigurationParser {

    @Override
    public ParsedConfiguration parseConfiguration(final String name, final String content)
            throws ConfigurationParserException {

        final String filteredContent = StringUtils.removeBashComments(content);
        final String[] lines = StringUtils.tokenize(filteredContent, '\n');
        final ParsedConfiguration configuration = new ParsedConfiguration(name);

        for (final String line : lines) {
            if (line == null || line.trim().isEmpty())
                continue;
            final String[] tokensQL = StringUtils.tokenize(line, '=');
            if (tokensQL.length != 2)
                throw new IllegalArgumentException("Unparseable content, can't find = in line: " + line);
            final String propertyNameLine = tokensQL[0];
            final String[] tokensDot = StringUtils.tokenize(propertyNameLine, '.');
            if (tokensDot.length == 0)
                throw new IllegalArgumentException("Unparseable content, can't find property name in line: " + line);
            final DynamicEnvironment env = new DynamicEnvironment();
            final String propertyName = tokensDot[tokensDot.length - 1];
            final String propertyValue = tokensQL[1];
            for (int i = 0; i < tokensDot.length - 1; i++)
                env.add(tokensDot[i]);

            final ParsedAttribute<?> pa = new PlainParsedAttribute(propertyName, env, propertyValue);
            configuration.addAttribute(pa);
        }

        return configuration;
    }

    @Override
    public ConfigurationSourceKey.Format getFormat() {
        return ConfigurationSourceKey.Format.PROPERTIES;
    }

}
