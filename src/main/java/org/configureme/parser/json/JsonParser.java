package org.configureme.parser.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.*;
import org.configureme.Environment;
import org.configureme.environments.DynamicEnvironment;
import org.configureme.parser.ArrayParsedAttribute;
import org.configureme.parser.CompositeParsedAttribute;
import org.configureme.parser.ConfigurationParser;
import org.configureme.parser.ConfigurationParserException;
import org.configureme.parser.IncludeParsedAttribute;
import org.configureme.parser.ParsedAttribute;
import org.configureme.parser.ParsedConfiguration;
import org.configureme.parser.PlainParsedAttribute;
import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.ConfigurationSourceRegistry;
import org.configureme.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ConfigurationParser implementation for JSON.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public class JsonParser implements ConfigurationParser {

    /**
     * The prefix string distinguishing names of environment JSON objects from composite JSON objects.
     * The names of last ones shall starts with this prefix.
     */
    private static final String COMPOSITE_ATTR_PREFIX = "@";
    /**
     * The prefix string for attribute inclusion.
     */
    private static final String INCLUDE_ATTR_PREFIX = "$<";

    /**
     * Parsed includes.
     */
    private static final Map<String, Set<String>> includes = new HashMap<>();

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(JsonParser.class);

    @Override
    public ParsedConfiguration parseConfiguration(final String name, final String content) throws ConfigurationParserException {
        String filteredContent = StringUtils.removeCComments(content);
        filteredContent = StringUtils.removeCPPComments(filteredContent);
        Set<String> include = includes.get(name);
        if (include == null)
            include = new HashSet<>();
        include.clear();
        include.add(name);
        //parse file includes, should be first than parsing tags
        filteredContent = includeExternalFiles(filteredContent, include);

        final List<String> tags = StringUtils.extractTags(filteredContent, '$', '}');
        //parse tags
        for (final String tag : tags) {
            //ensure wrong format is skipped
            if (tag.charAt(1) != '{')
                continue;
            try {
                final String propertyValue = getSystemProperty(tag.substring(2, tag.length() - 1));
                if (propertyValue == null) {
                    continue;
                }
                filteredContent = StringUtils.replaceOnce(filteredContent, tag, propertyValue);
            } catch (final Exception e) {
                log.warn("parseConfiguration: tag=" + tag + " can't be parsed", e);
            }
        }

        try {
            final Gson gson = new Gson();
            final JsonObject j = gson.fromJson(filteredContent, JsonObject.class);
            final ParsedConfiguration pa = new ParsedConfiguration(name);

            final DynamicEnvironment env = new DynamicEnvironment();

            final String[] names = j.keySet().toArray(new String[j.size()]);
            if (names.length != 0) {
                for (final String key : names) {
                    final List<? extends ParsedAttribute<?>> attList = parse(key, j.get(key), env);
                    for (final ParsedAttribute<?> att : attList)
                        pa.addAttribute(att);
                }
            }
            //remove current configuration from the externals
            include.remove(name);
            pa.setExternalConfigurations(include);
            includes.put(name, include);
            return pa;

        } catch (final JsonParseException e) {
            throw new ConfigurationParserException("JSON Error: unable to parse json file with name " + name );
        }
    }

    private String getSystemProperty(final String name) {
        String[] parts = name.split(":", 2);
        String propertyName = parts[0];
        String defaultValue = null;

        if (parts.length == 2) {
            defaultValue = parts[1];
        }

        String value = System.getProperty(propertyName);

        if (null == value) {
            return defaultValue;
        }

        return value;
    }

    private String includeExternalFiles(final String content, final Collection<String> configurationNames) throws ConfigurationParserException {
        final List<String> includes = StringUtils.extractTags(content, '$', '>');
        String result = content;
        for (final String include : includes) {
            //ensure wrong format is skipped
            if (include.charAt(1) != '<')
                continue;
            if (include.charAt(include.length() - 1) != '>')
                continue;

            // skip circles in includes
            final String includeName = include.substring(2, include.length() - 1);
            if (configurationNames.contains(includeName))
                throw new ConfigurationParserException("Circle detected: configuration=" + includeName + " was already included");
            // skip links to attributes
            if (include.contains("."))
                continue;
            try {
                //reading config
                configurationNames.add(includeName);
                final String includedContent = includeExternalFiles(readIncludedContent(includeName), configurationNames);
                result = StringUtils.replaceOnce(result, include, includedContent);
            } catch (final Exception e) {
                log.warn("includeExternalFiles: include=" + include + " can't be parsed", e);
            }
        }
        return result;
    }

    private String readIncludedContent(final String includeName) {
        final ConfigurationSourceKey configurationSourceKey = new ConfigurationSourceKey(ConfigurationSourceKey.Type.FILE, ConfigurationSourceKey.Format.JSON, includeName);
        String result = ConfigurationSourceRegistry.INSTANCE.readConfigurationSource(configurationSourceKey);
        result = result.substring(1, result.length() - 1);
        return result;
    }

    /**
     * <p>parse.</p>
     *
     * @param key         a {@link java.lang.String} object.
     * @param value       a {@link java.lang.Object} object.
     * @param environment a {@link org.configureme.environments.DynamicEnvironment} object.
     * @return a {@link java.util.List} object.
     * @throws com.google.gson.JsonParseException if any.
     */
    public static List<? extends ParsedAttribute<?>> parse(final String key, final JsonElement value, final DynamicEnvironment environment) throws JsonParseException {
        // an object value means a change in environment, let's see what it is
        if (value instanceof JsonObject && key.startsWith(COMPOSITE_ATTR_PREFIX)) {
            return Collections.singletonList(parseComposite(key, (JsonObject) value, environment));
        }
        if (value instanceof JsonArray && key.startsWith(COMPOSITE_ATTR_PREFIX)) {
            return Collections.singletonList(parseArray(key, (JsonArray) value, environment));
        }
        if (value instanceof JsonPrimitive && (value.getAsString()).startsWith(INCLUDE_ATTR_PREFIX)) {
            return Collections.singletonList(parseInclude(key, value.getAsString(), environment));
        }
        if (value instanceof JsonObject) {
            return parseObject(key, (JsonObject) value, environment);
        }
        if (value instanceof JsonArray) {
            return Collections.singletonList(parseArray(key, (JsonArray) value, environment));
        }
        return Collections.singletonList(new PlainParsedAttribute(key, (Environment) environment.clone(), JsonNull.INSTANCE.equals(value) ? null : value.getAsString()));
    }

    private static IncludeParsedAttribute parseInclude(final String key, final String value, final DynamicEnvironment environment) throws JsonParseException {
        return new IncludeParsedAttribute(key, (Environment) environment.clone(), value);
    }

    private static List<ParsedAttribute<?>> parseObject(final String key, final JsonObject value, final DynamicEnvironment environment) throws JsonParseException {
        final List<ParsedAttribute<?>> parsed = new ArrayList<>();

        environment.extendThis(key);
        try {
            final String[] names = value.keySet().toArray(new String[value.size()]);
            if (names.length != 0)
                for (String subKey : names)
                    parsed.addAll(parse(subKey, value.get(subKey), environment));
        } finally {
            environment.reduceThis();
        }

        return parsed;
    }

    private static CompositeParsedAttribute parseComposite(final String key, final JsonObject value, final DynamicEnvironment environment) throws JsonParseException {
        final String[] names = value.keySet().toArray(new String[value.size()]);
        if (names.length == 0)
            return new CompositeParsedAttribute(stripKey(key), (Environment) environment.clone(), Collections.<ParsedAttribute<?>>emptyList());

        final List<ParsedAttribute<?>> leafAttr = new ArrayList<>();
        for (final String subKey : names)
            leafAttr.addAll(parse(subKey, value.get(subKey), environment));

        return new CompositeParsedAttribute(stripKey(key), (Environment) environment.clone(), leafAttr);
    }

    private static ArrayParsedAttribute parseArray(final String key, final JsonArray value, final DynamicEnvironment environment) throws JsonParseException {
        final List<ParsedAttribute<?>> parsed = new ArrayList<>(value.size());
        for (int index = 0; index < value.size(); index++) {
            parsed.addAll(parse(key, value.get(index), environment));
        }

        return new ArrayParsedAttribute(stripKey(key), (Environment) environment.clone(), parsed);
    }

    private static String stripKey(final String key) {
        return key.startsWith(COMPOSITE_ATTR_PREFIX)
                ? key.substring(COMPOSITE_ATTR_PREFIX.length())
                : key;
    }
}
