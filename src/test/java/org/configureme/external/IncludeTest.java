package org.configureme.external;

import org.configureme.ConfigurationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author ivanbatura
 * @since: 01.10.12
 */
public class IncludeTest {
	IncludeConfig includeConfig = null;

	@BeforeEach
	public void setUp() throws Exception {
		includeConfig = new IncludeConfig();
		ConfigurationManager.INSTANCE.configure(includeConfig);
	}

	@Test
	public void includeTest() {
		//verification
		assertEquals(1, includeConfig.getNormal().intValue(), "Simple value not correct");
		assertEquals("included", includeConfig.getInclude(), "Included value not correct");
	}

	@Test
	public void linkedTest() {
		//verification
		assertEquals(1, includeConfig.getNormal().intValue(), "Simple value not correct");
		assertEquals("linked", includeConfig.getLinked(), "Linked value not correct");
	}

	@Test
	public void externalConfigTest() {
		//verification
		ExternalConfig externalConfig = new ExternalConfig();
		ConfigurationManager.INSTANCE.configure(externalConfig);
		assertEquals(externalConfig.getExternal(), includeConfig.getExternalConfig().getExternal(), "External config value not correct");
		assertEquals(externalConfig.getExternal(), includeConfig.getCircleConfig().getExternalConfig().getExternal(), "Loop handled successfully");
	}
}
