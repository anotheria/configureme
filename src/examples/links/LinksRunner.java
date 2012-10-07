package links;

import org.configureme.ConfigurationManager;
import org.configureme.Environment;
import org.configureme.GlobalEnvironment;
import org.configureme.environments.ApplicationEnvironment;

import java.util.ArrayList;
import java.util.List;

/**
 * This example shows how to work links to attributes that located in the other file
 *
 * @author ivanbatura
 * @since: 24.09.12
 */
public class LinksRunner {
	private static Environment TEST_ENVIRONMENTS[] = new Environment[]{
			GlobalEnvironment.INSTANCE,
			new ApplicationEnvironment("live", "", "", ""),
			new ApplicationEnvironment("test", "", "", ""),
	};

	public static void main(String a[]) throws InterruptedException {
		System.out.println("%%% This example shows how to work links to attributes that located in the other file");
		List<LinksConfig> configs = new ArrayList<LinksConfig>();
		for (Environment e : TEST_ENVIRONMENTS) {
			System.out.println("%% creating copy for " + e + ".");
			LinksConfig config = new LinksConfig(e);
			configs.add(config);
			ConfigurationManager.INSTANCE.configure(config, e);
		}
		System.out.println("%%% Configuration finished");

		long endTime = System.currentTimeMillis() + 1000 * 60 * 5;
		while (System.currentTimeMillis() < endTime) {
			System.out.println("Waiting further " + ((endTime - System.currentTimeMillis()) / 1000) + " seconds, edit 'linkedattributes' or 'links' in classpath to force reconfiguration.");
			Thread.sleep(1000 * 15);
		}
		System.out.println("Exiting.");

	}
}
