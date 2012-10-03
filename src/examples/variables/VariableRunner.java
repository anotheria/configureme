package variables;

import org.configureme.ConfigurationManager;
import org.configureme.Environment;
import org.configureme.GlobalEnvironment;
import org.configureme.environments.ApplicationEnvironment;

/**
 * This example shows how to work with system (environment) variables, that configured in json
 *
 * @author ivanbatura
 * @since: 24.09.12
 */
public class VariableRunner {
	private static Environment TEST_ENVIRONMENTS[] = new Environment[]{
			GlobalEnvironment.INSTANCE,
			new ApplicationEnvironment("live", "", "", ""),
			new ApplicationEnvironment("test", "", "", ""),
	};

	public static void main(String a[]) throws InterruptedException {
		System.out.println("%%% This example shows how to work with system (environment) variables, that configured in json");
		System.setProperty("testVariable", "environment value") ;
		for (Environment e : TEST_ENVIRONMENTS) {
			System.out.println("%% creating copy for " + e + ".");
			ConfigurationManager.INSTANCE.configure(new VariableConfig(e), e);
		}
		System.out.println("%%% Configuration finished");
		System.clearProperty("testVariable");
		System.out.println("Exiting.");
	}
}
