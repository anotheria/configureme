package configurealso;

import org.configureme.ConfigurationManager;
import org.configureme.Environment;
import org.configureme.GlobalEnvironment;
import org.configureme.environments.ApplicationEnvironment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ivanbatura
 * @since: 07.10.12
 */
public class ConfigureAlsoRunner {
	private static Environment TEST_ENVIRONMENTS[] = new Environment[]{
			GlobalEnvironment.INSTANCE,
			new ApplicationEnvironment("live", "", "", ""),
			new ApplicationEnvironment("test", "", "", ""),
	};

	public static void main(String a[]) throws InterruptedException {
		System.out.println("%%% This example shows how to work with config that is a part of the configurable config (annotation @ConfigureAlso)");
		List<ConfigureAlsoConfig> configs = new ArrayList<ConfigureAlsoConfig>();
		for (Environment e : TEST_ENVIRONMENTS) {
			System.out.println("%% creating copy for " + e + ".");
			ConfigureAlsoConfig config = new ConfigureAlsoConfig(e);
			configs.add(config);
			ConfigurationManager.INSTANCE.configure(config, e);
		}
		System.out.println("%%% Configuration finished");

		long endTime = System.currentTimeMillis() + 1000 * 60 * 5;
		while (System.currentTimeMillis() < endTime) {
			System.out.println("Waiting further " + ((endTime - System.currentTimeMillis()) / 1000) + " seconds, edit 'configurealso' or 'externalConfig'  in classpath to force reconfiguration.");
			Thread.sleep(1000 * 15);
		}
		System.out.println("Exiting.");

	}
}
