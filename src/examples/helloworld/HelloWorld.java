package helloworld;

import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;

@ConfigureMe
public class HelloWorld {
	@Configure
	private String greeting;
	@Configure
	private String world;
	
	public void setGreeting(String greeting) {
		this.greeting = greeting;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public void greet(){
		System.out.println("\t"+greeting+" "+world+"!");
	}
}
