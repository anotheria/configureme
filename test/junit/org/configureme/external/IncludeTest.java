package org.configureme.external;

import org.configureme.ConfigurationManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author ivanbatura
 * @since: 01.10.12
 */
public class IncludeTest {
	IncludeConfig includeConfig = null;

	@Before
	public void setUp() throws Exception {
		includeConfig = new IncludeConfig();
		ConfigurationManager.INSTANCE.configure(includeConfig);
	}

	@Test
	public void includeTest() {
		//verification
		assertEquals("Simple value not correct", 1, includeConfig.getNormal().intValue());
		assertEquals("Included value not correct", "included", includeConfig.getInclude());
	}

	@Test
	public void linkedTest() {
		//verification
		assertEquals("Simple value not correct", 1, includeConfig.getNormal().intValue());
		assertEquals("Linked value not correct", "linked", includeConfig.getLinked());
	}

	@Test
	public void externalConfigTest() {
		//verification
		ExternalConfig externalConfig = new ExternalConfig();
		ConfigurationManager.INSTANCE.configure(externalConfig);
		assertEquals("External config value not correct", externalConfig.getExternal(), includeConfig.getExternalConfig().getExternal());
	}
}
