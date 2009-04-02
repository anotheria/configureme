package helloworld;

import org.configureme.annotations.ConfigureMe;

@ConfigureMe
public class HelloWorld {
	private String message;
	
	public void setMessage(String aMessage){
		message = aMessage;
	}
	
	public void greet(){
		System.out.println(message);
	}
}
