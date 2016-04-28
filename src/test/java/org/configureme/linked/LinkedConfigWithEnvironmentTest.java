package org.configureme.linked;

import org.configureme.ConfigurationManager;
import org.configureme.environments.DynamicEnvironment;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * TODO comment this class
 *
 * @author lrosenberg
 * @since 28.04.16 15:56
 */
public class LinkedConfigWithEnvironmentTest {
	@Test public void testWithoutEnvironment(){
		LinkedConfigWithEnvironment config = new LinkedConfigWithEnvironment();
		ConfigurationManager.INSTANCE.configure(config);
		assertEquals("normal", config.getLinked());
	}

	@Test public void testWithSingleEnvironment(){
		ConfigurationManager.INSTANCE.setDefaultEnvironment(new DynamicEnvironment("first"));
		LinkedConfigWithEnvironment config = new LinkedConfigWithEnvironment();
		ConfigurationManager.INSTANCE.configure(config);
		assertEquals("first", config.getLinked());
	}

	@Test public void testWithDoubleEnvironment(){
		ConfigurationManager.INSTANCE.setDefaultEnvironment(new DynamicEnvironment("first_second"));
		LinkedConfigWithEnvironment config = new LinkedConfigWithEnvironment();
		ConfigurationManager.INSTANCE.configure(config);
		assertEquals("second", config.getLinked());
	}

	@Test public void testWithOtherEnvironment(){
		ConfigurationManager.INSTANCE.setDefaultEnvironment(new DynamicEnvironment("other"));
		LinkedConfigWithEnvironment config = new LinkedConfigWithEnvironment();
		ConfigurationManager.INSTANCE.configure(config);
		assertEquals("other", config.getLinked());
	}
}
