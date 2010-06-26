package helloworld;

import org.configureme.sources.ConfigurationSourceKey.Format;

public class HelloWorldRunnerJson  extends BaseHelloWorldRunner{
	public static void main(String a[]){
		System.out.println("Testing for JSON file.");
		new HelloWorldRunnerJson().runExample();
	}
	
	protected Format getTargetConfigFormat(){
		return Format.JSON;
	}
}
