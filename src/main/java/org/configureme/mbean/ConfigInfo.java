package org.configureme.mbean;

import org.configureme.Configuration;
import org.configureme.ConfigurationManager;
import org.configureme.Environment;
import org.configureme.environments.DynamicEnvironment;
import org.configureme.parser.ParsedAttribute;
import org.configureme.parser.json.JsonParser;
import org.configureme.repository.Artefact;
import org.configureme.repository.ConfigurationRepository;
import org.configureme.repository.Value;
import org.configureme.util.StringUtils;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Config info mBean.
 *
 * @author asamoilich
 * @version $Id: $Id
 */
public class ConfigInfo implements ConfigInfoMBean {
	/**
	 * Config name.
	 */
	private String configName;

	/**
	 * Constructor.
	 *
	 * @param configName provided config name
	 */
	public ConfigInfo(final String configName) {
		this.configName = configName;
	}

	/** {@inheritDoc} */
	@Override
	public String getConfigName() {
		return configName;
	}

	/** {@inheritDoc} */
	@Override
	public String showContent() {
		final Artefact artefact = ConfigurationRepository.INSTANCE.getArtefact(configName);
		if (artefact == null)
			throw new IllegalArgumentException("No such artefact: " + configName);
		Map<Environment, Map<String, Object>> contentMap = artefact.getContent();
		StringBuilder resultContent = new StringBuilder();
		resultContent.append("{\n");
		for (Map.Entry<Environment, Map<String, Object>> environmentMapEntry : contentMap.entrySet()) {
			String env = environmentMapEntry.getKey().expandedStringForm();
			resultContent.append("   ").append(env.isEmpty() ? "" : env + ": ").append("{\n");
			for (Map.Entry<String, Object> stringObjectEntry : environmentMapEntry.getValue().entrySet())
				resultContent.append("      ").append(stringObjectEntry.getKey()).append(" : ").append(stringObjectEntry.getValue()).append(",\n");
			resultContent.append("   },\n");
		}
		resultContent.append('}');
		return resultContent.toString();
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Object> getAttributes() {
		Map<String, Object> attributeMap = new HashMap<>();
		final Environment defaultEnvironment = ConfigurationManager.INSTANCE.getDefaultEnvironment();
		final Configuration configuration = ConfigurationRepository.INSTANCE.getConfiguration(configName, defaultEnvironment);

		for (String attrName : configuration.getAttributeNames()) {
			Value attrValue = configuration.getAttribute(attrName);
			attributeMap.put(attrName, attrValue == null ? "" : attrValue.getRaw());
		}
		return attributeMap;
	}

	/** {@inheritDoc} */
	@Override
	public void setAttributeValue(final String attrName, final String attrValue) throws JSONException {
		if (StringUtils.isEmpty(attrName))
			throw new IllegalArgumentException("Please enter attribute name!");
		final DynamicEnvironment dynamicEnvironment = (DynamicEnvironment) ConfigurationManager.INSTANCE.getDefaultEnvironment();
		final Artefact artefact = ConfigurationRepository.INSTANCE.getArtefact(configName);
		if (artefact == null)
			throw new IllegalArgumentException("No such artefact: " + configName);
		List<? extends ParsedAttribute<?>> attList = JsonParser.parse(attrName, attrValue, dynamicEnvironment);
		if (attList == null || attList.isEmpty())
			throw new JSONException("Nothing to parse. Please fill out attribute name and value.");
		ParsedAttribute<?> parsedAttribute = attList.get(0);
		artefact.addAttributeValue(attrName, parsedAttribute.getValue(), dynamicEnvironment);
		ConfigurationRepository.INSTANCE.updateArtefact(artefact);
	}
}
