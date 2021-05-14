package configuremerepository;

import org.configureme.annotations.ConfigureMe;

@ConfigureMe(allfields = true)
public class Example{
	private String a;

	public String getA() {
		return a;
	}

	public void setA(String a) {
		this.a = a;
	}

	public String toString(){
		return "a: "+getA();
	}
}