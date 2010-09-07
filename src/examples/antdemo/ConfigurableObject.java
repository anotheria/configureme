package antdemo;

import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;

@ConfigureMe(name="forant")
public class ConfigurableObject {
	@Configure private String value ;
	
	public void setValue(String aValue){
		value = aValue;
	}
	
	@Override public String toString(){
		return value;
	}
}
