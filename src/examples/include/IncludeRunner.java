package include;

import org.configureme.ConfigurationManager;
import org.configureme.Environment;
import org.configureme.GlobalEnvironment;
import org.configureme.environments.ApplicationEnvironment;

import java.util.ArrayList;
import java.util.List;

/**
 * This example shows how to work with includes in configured file and variables include from others configurable files
 *
 * @author ivanbatura
 * @since: 24.09.12
 */
public class IncludeRunner {
	private static Environment TEST_ENVIRONMENTS[] = new Environment[]{
			GlobalEnvironment.INSTANCE,
			new ApplicationEnvironment("live", "", "", ""),
			new ApplicationEnvironment("test", "", "", ""),
	};

	public static void main(String a[]) throws InterruptedException {
		System.out.println("%%% This example shows how to work with system (environment) variables, that configured in json");
		System.setProperty("testVariable", "environment value");
		List<IncludeConfig> configs = new ArrayList<IncludeConfig>();
		for (Environment e : TEST_ENVIRONMENTS) {
			System.out.println("%% creating copy for " + e + ".");
			IncludeConfig config = new IncludeConfig(e);
			configs.add(config);
			ConfigurationManager.INSTANCE.configure(config, e);
		}
		System.out.println("%%% Configuration finished");
		System.clearProperty("testVariable");


		long endTime = System.currentTimeMillis() + 1000 * 60 * 5;
		while (System.currentTimeMillis() < endTime) {
			System.out.println("Waiting further " + ((endTime - System.currentTimeMillis()) / 1000) + " seconds, edit include.json and includefile.json and includedattributes.json in classpath to force reconfiguration.");
			Thread.sleep(1000 * 15);
		}
		System.out.println("Exiting.");

	}
}
