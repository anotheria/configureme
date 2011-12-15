package org.configureme.parser.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.anotheria.util.StringUtils;

import org.configureme.Environment;
import org.configureme.environments.DynamicEnvironment;
import org.configureme.parser.ArrayParsedAttribute;
import org.configureme.parser.CompositeParsedAttribute;
import org.configureme.parser.ConfigurationParser;
import org.configureme.parser.ConfigurationParserException;
import org.configureme.parser.ParsedAttribute;
import org.configureme.parser.ParsedConfiguration;
import org.configureme.parser.PlainParsedAttribute;
import org.configureme.parser.StringArrayParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * ConfigurationParser implementation for JSON.
 * @author lrosenberg
 */
public class JsonParser implements ConfigurationParser {
	private static final String COMPOSITE_ATTR_PREFIX = "@";

	@Override
	public ParsedConfiguration parseConfiguration(String name, String content) throws ConfigurationParserException {

		content = StringUtils.removeCComments(content);
		content = StringUtils.removeCPPComments(content);

		try {
			JSONObject j = new JSONObject(content);
			ParsedConfiguration pa = new ParsedConfiguration(name);

			DynamicEnvironment env = new DynamicEnvironment();

			String[] names = JSONObject.getNames(j);
			if(names != null)
				for (String key : names) {
					List<? extends ParsedAttribute<?>> attList = parse(key, j, env);
					for (ParsedAttribute<?> att : attList)
						pa.addAttribute(att);
				}

			return pa;

		} catch (JSONException e) {
			throw new ConfigurationParserException("JSON Error", e);
		}
	}

	private static List<? extends ParsedAttribute<?>> parse(String key, JSONObject root, DynamicEnvironment environment) throws JSONException{
		Object value = root.get(key);
		// an object value means a change in environment, let's see what it is
		if (value instanceof JSONObject && key.startsWith(COMPOSITE_ATTR_PREFIX))
			return asList(parseComposite(key.substring(COMPOSITE_ATTR_PREFIX.length()), (JSONObject) value, environment));
        else if (value instanceof JSONArray && key.startsWith(COMPOSITE_ATTR_PREFIX))
        	return asList(parseCompositeArray(key.substring(COMPOSITE_ATTR_PREFIX.length()), (JSONArray) value, environment));
        else if (value instanceof JSONObject)
        	return parsePlain(key, (JSONObject) value, environment);
		else if (value instanceof JSONArray)
			return Arrays.asList(parsePlainArray(key, (JSONArray) value, environment));
		else
			return Arrays.asList(new PlainParsedAttribute(key, (Environment)environment.clone(), root.getString(key)));
	}

	@SuppressWarnings("unchecked")
	private static <T> List<T> asList(T item) {
		return (item != null) ? Arrays.<T>asList(item) : Collections.<T>emptyList();
	}

	private static List<ParsedAttribute<?>> parsePlain(String key, JSONObject value, DynamicEnvironment environment) throws JSONException {
		List<ParsedAttribute<?>> parsed = new ArrayList<ParsedAttribute<?>>();

		environment.extendThis(key);
		try {
			String[] names = JSONObject.getNames(value);
			if(names != null)
				for (String subKey : names)
					parsed.addAll(parse(subKey, value, environment));
		} finally {
			environment.reduceThis();
		}

		return parsed;
	}

	private static PlainParsedAttribute parsePlainArray(String key, JSONArray value, DynamicEnvironment environment) throws JSONException {
		List<String> list = new ArrayList<String>(value.length());
		for (int i = 0; i < value.length(); i++)
			list.add(value.getString(i));

		return new PlainParsedAttribute(key, (Environment) environment.clone(), StringArrayParser.toStringArray(list.toArray()));
	}

	private static CompositeParsedAttribute parseComposite(String key, JSONObject value, DynamicEnvironment environment) throws JSONException {
        String[] names = JSONObject.getNames(value);
        if (names == null)
        	return null;

        List<ParsedAttribute<?>> leafAttr = new ArrayList<ParsedAttribute<?>>();
        for (String subKey : names)
            leafAttr.addAll(parse(subKey, value, environment));

        return new CompositeParsedAttribute(key, (Environment) environment.clone(), leafAttr);
	}

	private static ArrayParsedAttribute parseCompositeArray(String key, JSONArray value, DynamicEnvironment environment) throws JSONException {
		List<CompositeParsedAttribute> parsed = new ArrayList<CompositeParsedAttribute>(value.length());
		for (int i = 0; i < value.length(); i++)
			parsed.add(parseComposite(key, value.getJSONObject(i), environment));

		return new ArrayParsedAttribute(key, (Environment) environment.clone(), parsed);
	}
}
