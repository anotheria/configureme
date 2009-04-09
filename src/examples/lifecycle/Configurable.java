package lifecycle;

import org.configureme.Environment;
import org.configureme.annotations.AfterConfiguration;
import org.configureme.annotations.AfterInitialConfiguration;
import org.configureme.annotations.AfterReConfiguration;
import org.configureme.annotations.BeforeConfiguration;
import org.configureme.annotations.BeforeInitialConfiguration;
import org.configureme.annotations.BeforeReConfiguration;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;

@ConfigureMe(name="lifecycleexample")
public class Configurable {
	@Configure private String somevalue;
	
	/**
	 * Usually you don't need to store the environment, its made here, to make the debug messages more verbose.
	 */
	private Environment environment;
	
	public Configurable(Environment anEnvironment){
		environment = anEnvironment;
	}
	
	public String toString(){
		return "!!! Configurable "+environment+" somevalue: "+somevalue;
	}
	
	public void setSomevalue(String aValue){
		somevalue = aValue;
		System.out.println(this+": my property somevalue has been set to: "+somevalue);
	}
	
	@BeforeConfiguration public void callBeforeEachConfiguration(){
		System.out.println(this+" will be configured now");
	}
	@BeforeInitialConfiguration public void callBeforeInitialConfigurationOnly(){
		System.out.println(this+" will be INITIALY configured now");
	}
	@BeforeReConfiguration public void callBeforeReConfigurationOnly(){
		System.out.println(this+" will be RE-configured now");
	}

	@AfterConfiguration public void callAfterEachConfiguration(){
		System.out.println(this+" has been configured");
	}
	@AfterInitialConfiguration public void callAfterInitialConfigurationOnly(){
		System.out.println(this+" has been INITIALY-configured");
	}
	@AfterReConfiguration public void callAfterReConfigurationOnly(){
		System.out.println(this+" has been RE-configured");
	}
}
