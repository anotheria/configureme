package helloworld;

import java.util.Locale;

import org.apache.log4j.BasicConfigurator;
import org.configureme.Configuration;
import org.configureme.ConfigurationManager;
import org.configureme.environments.LocaleBasedEnvironment;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReadConfig {
	
	@BeforeClass public static void initlog4j(){
		BasicConfigurator.configure();
	}
	
	@Test public void readHelloWorldConfig(){
		Configuration c = ConfigurationManager.INSTANCE.getConfiguration("helloworld");
		System.out.println("general: "+c);

		c = ConfigurationManager.INSTANCE.getConfiguration("helloworld", new LocaleBasedEnvironment(Locale.GERMAN));
		System.out.println("In german: "+c);
		
		c = ConfigurationManager.INSTANCE.getConfiguration("helloworld", new LocaleBasedEnvironment(new Locale("es", "ES", "madrid")));
		System.out.println("In madrid: "+c);
		
		c = ConfigurationManager.INSTANCE.getConfiguration("helloworld", new LocaleBasedEnvironment(new Locale("es", "ES", "barcelona")));
		System.out.println("In barcelona: "+c);

		c = ConfigurationManager.INSTANCE.getConfiguration("helloworld", new LocaleBasedEnvironment(new Locale("de", "DE", "bayern")));
		System.out.println("In bayern: "+c);
		c = ConfigurationManager.INSTANCE.getConfiguration("helloworld", new LocaleBasedEnvironment(new Locale("de", "DE", "berlin")));
		System.out.println("In berlin: "+c);
		c = ConfigurationManager.INSTANCE.getConfiguration("helloworld", new LocaleBasedEnvironment(new Locale("de", "DE", "sachsen")));
		System.out.println("In sachsen: "+c);
	}
}
