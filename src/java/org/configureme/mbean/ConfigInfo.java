package org.configureme.mbean;

import org.configureme.ConfigurationManager;
import org.configureme.Environment;
import org.configureme.repository.Artefact;
import org.configureme.repository.ConfigurationRepository;
import org.configureme.repository.Value;
import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.ConfigurationSourceRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * Config info mBean.
 *
 * @author asamoilich
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

	@Override
	public String getConfigName() {
		return configName;
	}

	@Override
	public String showContent() {
		return ConfigurationSourceRegistry.INSTANCE.readConfigurationSource(ConfigurationSourceKey.jsonFile(getConfigName()));
	}

	@Override
	public Map<String, Object> getAttributes() {
		Map<String, Object> attributeMap = new HashMap<String, Object>();
		final Environment defaultEnvironment = ConfigurationManager.INSTANCE.getDefaultEnvironment();
		final Artefact artefact = ConfigurationRepository.INSTANCE.getArtefact(configName);
		if (artefact == null)
			return attributeMap;

		for (String attrName : artefact.getAttributeNames()) {
			Value attrValue = artefact.getAttribute(attrName).getValue(defaultEnvironment);
			attributeMap.put(attrName, attrValue == null ? "" : attrValue.getRaw());
		}
		return attributeMap;
	}
}
