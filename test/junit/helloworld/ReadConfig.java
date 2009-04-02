package helloworld;

import org.configureme.Configuration;
import org.configureme.ConfigurationManager;
import org.junit.Test;

public class ReadConfig {
	@Test public void readHelloWorldConfig(){
		Configuration c = ConfigurationManager.INSTANCE.getConfiguration("helloworld");
		System.out.println(c);
	}
}
