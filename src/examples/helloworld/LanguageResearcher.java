package helloworld;

import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.Set;

//By specifying the explicit artefact name we are telling the system how to look for the artefact. Watch = false means we don't want to be reconfigured.
@ConfigureMe(name="helloworld", watch=false)
public class LanguageResearcher {
	@Set("greeting") 
	public void research(String greeting){
		System.out.println("\tI found out that here people are greeting with \""+greeting+"\"");
	}
	
	@Set("world")
	public void translate(String world){
		System.out.println("\tand that the word \"world\" is translated as  \""+world+"\"");
	}
}
