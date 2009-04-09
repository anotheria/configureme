package lifecycle;

import org.configureme.ConfigurationManager;
import org.configureme.Environment;
import org.configureme.GlobalEnvironment;
import org.configureme.environments.ApplicationEnvironment;

public class ExampleRunner {
	
	private static Environment TEST_ENVIRONMENTS[] = new Environment[]{
		GlobalEnvironment.INSTANCE,
		new ApplicationEnvironment("live", "", "", ""),
		new ApplicationEnvironment("test", "", "", ""),
		new ApplicationEnvironment("dev", "", "", ""),
	};
	
	public static void main(String a[]) throws InterruptedException{
		
		System.out.println("%%% Creating and initially configuring objects ...");
		
		for (Environment e : TEST_ENVIRONMENTS){
			System.out.println("%% creating copy for "+e+".");
			ConfigurationManager.INSTANCE.configure(new Configurable(e), e);
		}
		
		System.out.println("%%% Configuration finished");
		
		long endTime = System.currentTimeMillis()+1000*60*5;
		while(System.currentTimeMillis()<endTime){
			System.out.println("Waiting further "+((endTime-System.currentTimeMillis())/1000)+" seconds, edit lifecycleexamples.json in classpath to force reconfiguration." );
			Thread.currentThread().sleep(1000*15);
		}
		
		System.out.println("Exiting.");
	}
}
