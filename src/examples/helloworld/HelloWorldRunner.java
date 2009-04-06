package helloworld;

import java.util.Locale;

import org.configureme.ConfigurationManager;
import org.configureme.Environment;
import org.configureme.GlobalEnvironment;
import org.configureme.environments.DynamicEnvironment;
import org.configureme.environments.LocaleBasedEnvironment;

public class HelloWorldRunner {
	public static void main(String a[]){
		System.out.println("Example for hello world (autoconfiguration of fields): ");
		sayHello(GlobalEnvironment.INSTANCE, null);
		sayHello(new LocaleBasedEnvironment.Builder().language("de").country("DE").variant("bayern").build(),"Bavaria");
		sayHello(new LocaleBasedEnvironment.Builder().language("de").country("DE").variant("berlin").build(),"Berlin");
		sayHello(new LocaleBasedEnvironment(new Locale("es")), "Spanish speaking countries");
		sayHello(new LocaleBasedEnvironment.Builder().language("es").country("ES").variant("barcelona").build(),"Barcelona (no separate config == spain)");
		sayHello(new DynamicEnvironment().add("en").add("US").add("california").add("sunnyside").add("dublin"),"Dublin CA");

		System.out.println("Example for language researcher (autoconfiguration of methods): ");
		research(GlobalEnvironment.INSTANCE, null);
		research(new LocaleBasedEnvironment.Builder().language("de").country("DE").variant("bayern").build(),"Bavaria");
		research(new LocaleBasedEnvironment.Builder().language("de").country("DE").variant("berlin").build(),"Berlin");
		research(new LocaleBasedEnvironment(new Locale("es")), "Spanish speaking countries");
		research(new LocaleBasedEnvironment.Builder().language("es").country("ES").variant("barcelona").build(),"Barcelona (no separate config == spain)");
		research(new DynamicEnvironment().add("en").add("US").add("california").add("sunnyside").add("dublin"),"Dublin CA");

	}
	
	private static void sayHello(Environment in, String description){
		if (description==null)
			description = in.toString();
		HelloWorld hello = new HelloWorld();
		ConfigurationManager.INSTANCE.configure(hello, in);
		System.out.println("Greeting people in "+description+" ("+in+")"+": ");
		hello.greet();
	}
	
	private static void research(Environment in, String description){
		if (description==null)
			description = in.toString();
		LanguageResearcher researcher = new LanguageResearcher();
		System.out.println("Researching "+description+" ("+in+")"+": ");
		ConfigurationManager.INSTANCE.configure(researcher, in);
	}
}
