package helloworld;

import org.configureme.sources.ConfigurationSourceKey.Format;

public class HelloWorldRunnerProperties extends BaseHelloWorldRunner{
	public static void main(String a[]){
		System.out.println("Testing with Properties file.");
		new HelloWorldRunnerProperties().runExample();
	}
	
	protected Format getTargetConfigFormat(){
		return Format.PROPERTIES;
	}
}
