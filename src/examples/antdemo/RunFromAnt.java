package antdemo;

import org.configureme.ConfigurationManager;


public class RunFromAnt {
	public static void main(String a[]){
		ConfigurableObject o = new ConfigurableObject();
		ConfigurationManager.INSTANCE.configure(o);
		System.out.println("Configured object: "+o);
	}
}
