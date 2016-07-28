package org.configureme.parser.json;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ConfigurationParser implementation for JSON.
 *
 * @author lrosenberg
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
	private static Map<String, Set<String>> includes = new HashMap<>();

	/**
	 * Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(JsonParser.class);

	@Override
	public ParsedConfiguration parseConfiguration(String name, String content) throws ConfigurationParserException {

		content = StringUtils.removeCComments(content);
		content = StringUtils.removeCPPComments(content);
		Set<String> include = includes.get(name);
		if (include == null)
			include = new HashSet<>();
		include.clear();
		include.add(name);
		//parse file includes, should be first than parsing tags
		content = includeExternalFiles(content, include);

		List<String> tags = StringUtils.extractTags(content, '$', '}');
		//parse tags
		for (String tag : tags) {
			//ensure wrong format is skipped
			if (tag.charAt(1) != '{')
				continue;
			try {
				String propertyName = tag.substring(2, tag.length() - 1);
				String propertyValue = System.getProperty(propertyName);
				if (propertyValue == null) {
					continue;
				}
				content = StringUtils.replaceOnce(content, tag, propertyValue);
			} catch (Exception e) {
				log.warn("parseConfiguration: tag=" + tag + " can't be parsed", e);
			}

		}

		try {
			JSONObject j = new JSONObject(content);
			ParsedConfiguration pa = new ParsedConfiguration(name);

			DynamicEnvironment env = new DynamicEnvironment();

			String[] names = JSONObject.getNames(j);
			if (names != null){
				for (String key : names) {
					List<? extends ParsedAttribute<?>> attList = parse(key, j.get(key), env);
					for (ParsedAttribute<?> att : attList)
						pa.addAttribute(att);
				}
			}
			//remove current configuration from the externals
			include.remove(name);
			pa.setExternalConfigurations(include);
			includes.put(name, include);
			return pa;

		} catch (JSONException e) {
			throw new ConfigurationParserException("JSON Error", e);
		}
	}

	private String includeExternalFiles(String content, final Collection<String> configurationNames) throws ConfigurationParserException {
		List<String> includes = StringUtils.extractTags(content, '$', '>');
		for (String include : includes) {
			//ensure wrong format is skipped
			if (include.charAt(1) != '<')
				continue;
			if (include.charAt(include.length() - 1) != '>')
				continue;

			// skip circles in includes
			String includeName = include.substring(2, include.length() - 1);
			if (configurationNames.contains(includeName))
				throw new ConfigurationParserException("Circle detected: configuration=" + includeName + " was already included");
			// skip links to attributes
			if (include.contains("."))
				continue;
			try {
				//reading config
				configurationNames.add(includeName);
				String includedContent = includeExternalFiles(readIncludedContent(includeName), configurationNames);
				content = StringUtils.replaceOnce(content, include, includedContent);
			} catch (Exception e) {
				log.warn("includeExternalFiles: include=" + include + " can't be parsed", e);
			}
		}
		return content;
	}

	private String readIncludedContent(String includeName) {
		ConfigurationSourceKey configurationSourceKey = new ConfigurationSourceKey(ConfigurationSourceKey.Type.FILE, ConfigurationSourceKey.Format.JSON, includeName);
		String result = ConfigurationSourceRegistry.INSTANCE.readConfigurationSource(configurationSourceKey);
		result = result.substring(1, result.length()-1);		
		return result;
	}

	public static List<? extends ParsedAttribute<?>> parse(String key, Object value, DynamicEnvironment environment) throws JSONException {
		// an object value means a change in environment, let's see what it is
		if (value instanceof JSONObject && key.startsWith(COMPOSITE_ATTR_PREFIX))
			return Collections.singletonList(parseComposite(key, (JSONObject) value, environment));
		else if (value instanceof JSONArray && key.startsWith(COMPOSITE_ATTR_PREFIX))
			return Collections.singletonList(parseArray(key, (JSONArray) value, environment));
		else if (value instanceof String && ((String) value).startsWith(INCLUDE_ATTR_PREFIX))
			return Collections.singletonList(parseInclude(key, (String) value, environment));
		else if (value instanceof JSONObject)
			return parseObject(key, (JSONObject) value, environment);
		else if (value instanceof JSONArray)
			return Collections.singletonList(parseArray(key, (JSONArray) value, environment));
		else
			return Collections.singletonList(new PlainParsedAttribute(key, (Environment) environment.clone(), JSONObject.NULL.equals(value) ? null : value.toString()));
	}

	private static IncludeParsedAttribute parseInclude(String key, String value, DynamicEnvironment environment) throws JSONException {
		return new IncludeParsedAttribute(key, (Environment) environment.clone(), value);
	}

	private static List<ParsedAttribute<?>> parseObject(String key, JSONObject value, DynamicEnvironment environment) throws JSONException {
		List<ParsedAttribute<?>> parsed = new ArrayList<>();

		environment.extendThis(key);
		try {
			String[] names = JSONObject.getNames(value);
			if (names != null)
				for (String subKey : names)
					parsed.addAll(parse(subKey, value.get(subKey), environment));
		} finally {
			environment.reduceThis();
		}

		return parsed;
	}

	private static CompositeParsedAttribute parseComposite(String key, JSONObject value, DynamicEnvironment environment) throws JSONException {
		String[] names = JSONObject.getNames(value);
		if (names == null)
			return new CompositeParsedAttribute(stripKey(key), (Environment) environment.clone(), Collections.<ParsedAttribute<?>>emptyList());

		List<ParsedAttribute<?>> leafAttr = new ArrayList<>();
		for (String subKey : names)
			leafAttr.addAll(parse(subKey, value.get(subKey), environment));

		return new CompositeParsedAttribute(stripKey(key), (Environment) environment.clone(), leafAttr);
	}

	private static ArrayParsedAttribute parseArray(String key, JSONArray value, DynamicEnvironment environment) throws JSONException {
		List<ParsedAttribute<?>> parsed = new ArrayList<>(value.length());
		for (int index = 0; index < value.length(); index++){
			parsed.addAll(parse(key, value.get(index), environment));
		}

		return new ArrayParsedAttribute(stripKey(key), (Environment) environment.clone(), parsed);
	}

	private static String stripKey(String key) {
		return key.startsWith(COMPOSITE_ATTR_PREFIX)
				? key.substring(COMPOSITE_ATTR_PREFIX.length())
				: key;
	}
}
