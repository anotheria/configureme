package org.configureme.sources;

import org.configureme.sources.ConfigurationSourceKey.Format;
import org.configureme.sources.ConfigurationSourceKey.Type;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConfigurationSourceTest {
	@Test public void testRobustness(){
		final ConfigurationSource source = new ConfigurationSource(new ConfigurationSourceKey(Type.FIXTURE, Format.JSON, "not-existent"));
		source.addListener(new ConfigurationSourceListener(){
			public void configurationSourceUpdated(ConfigurationSource target){
				assertSame(source, target);
				throw new RuntimeException("ups");
			}
		});
		
		source.fireUpdateEvent(System.currentTimeMillis());
	}
}
