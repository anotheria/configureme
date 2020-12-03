package org.configureme.githubissues.issue16;

import static org.junit.Assert.assertEquals;

import org.configureme.ConfigurationManager;
import org.configureme.environments.DynamicEnvironment;
import org.configureme.sources.ConfigurationSourceKey;
import org.junit.Test;

/**
 * Test for https://github.com/anotheria/configureme/issues/16
 *
 * @author lrosenberg
 * @since 2019-05-10 09:52
 */
public class Issue16Test {
	@Test
	public void loadAndTestConfig(){
		ConfigObject configObject = new ConfigObject();
		ConfigurationManager.INSTANCE.configure(configObject, DynamicEnvironment.parse(""), ConfigurationSourceKey.Format.PROPERTIES);

		assertEquals("6xeaC}*{n%S5OW4Xi{/wT&F(A>!0kjMuc(w#UB9KB;1;mU1(YW.77f_[2KO[V.r;", configObject.getSecret());
	}
}
