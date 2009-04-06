package speedlimit;

import org.configureme.ConfigurationManager;
import org.configureme.Environment;
import org.configureme.GlobalEnvironment;
import org.configureme.environments.DynamicEnvironment;

public class SpeedLimitChecker {
	public static void main(String a[]){
		System.out.println("Example for hello world (autoconfiguration of fields): ");
		
		
		
		showLimits(GlobalEnvironment.INSTANCE);
		showLimits(new DynamicEnvironment("Denmark"));
		showLimits(new DynamicEnvironment("Germany", "Berlin"));
		showLimits(new DynamicEnvironment("Belgium"));
		showLimits(new DynamicEnvironment("Spain"));
		showLimits(new DynamicEnvironment("Spain", "Madrid"));
	
	}
	
	private static void showLimits(Environment in){
		SpeedLimit limit = new SpeedLimit();
		ConfigurationManager.INSTANCE.configure(limit, in);
		System.out.println("In "+in);
		limit.tellLimit();
	}
	
}
