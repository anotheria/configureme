package org.configureme.mbean;

import org.json.JSONException;

import java.util.Map;

/**
 * @author asamoilich
 */
public interface ConfigInfoMBean {
	/**
	 * Return config file name.
	 *
	 * @return config name
	 */
	String getConfigName();

	/**
	 * Return content of current config file.
	 *
	 * @return config content
	 */
	String showContent();

	/**
	 * Return map of attributes. Key - attribute name, Value - attribute value.
	 *
	 * @return map of attributes
	 */
	Map<String, Object> getAttributes();

	/**
	 * Set new value for provided attribute.
	 *
	 * @param attrName  attribute name
	 * @param attrValue attribute value
	 * @throws JSONException on errors
	 */
	void setAttributeValue(final String attrName, final String attrValue) throws JSONException;
}
