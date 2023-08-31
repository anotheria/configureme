package org.configureme.mbean;

import com.google.gson.JsonParseException;

import java.util.Map;

/**
 * <p>ConfigInfoMBean interface.</p>
 *
 * @author asamoilich
 * @version $Id: $Id
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
	 * @throws com.google.gson.JsonParseException on errors
	 */
	void setAttributeValue(final String attrName, final String attrValue) throws JsonParseException;
}
