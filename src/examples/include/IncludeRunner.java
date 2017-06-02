package include;

import java.util.ArrayList;
import java.util.List;

import org.configureme.ConfigurationManager;
import org.configureme.Environment;
import org.configureme.GlobalEnvironment;
import org.configureme.environments.ApplicationEnvironment;

/**
 * This example shows how to work with includes in configured file from others configurable files
 *
 * @author ivanbatura
 * @since: 24.09.12
 */
public class IncludeRunner {

	private static final Environment[] TEST_ENVIRONMENTS = new Environment[]{
			GlobalEnvironment.INSTANCE,
			new ApplicationEnvironment("live", "", "", ""),
			new ApplicationEnvironment("test", "", "", ""),
	};

	public static void main(String a[]) throws InterruptedException {
		System.out.println("%%% This example shows how to work includes of one json into other");
		final List<include.IncludeConfig> configs = new ArrayList<include.IncludeConfig>();
		for (final Environment e : TEST_ENVIRONMENTS) {
			System.out.println("%% creating copy for " + e + ".");
			final include.IncludeConfig config = new include.IncludeConfig(e);
			configs.add(config);
			ConfigurationManager.INSTANCE.configure(config, e);
		}
		System.out.println("%%% Configuration finished");

		final long endTime = System.currentTimeMillis() + 1000 * 60 * 5;
		while (System.currentTimeMillis() < endTime) {
			System.out.println("Waiting further " + ((endTime - System.currentTimeMillis()) / 1000) + " seconds, edit 'include.json' or 'includefile.json' in classpath to force reconfiguration.");
			Thread.sleep(1000 * 15);
		}
		System.out.println("Exiting.");

	}
}
