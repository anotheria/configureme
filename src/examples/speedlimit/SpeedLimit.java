package speedlimit;

import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;

@ConfigureMe
public class SpeedLimit {
	
	@Configure private int motorway;

	@Configure private int buildUpAreas;
	
	public void setMotorway(int aMotorway){
		motorway = aMotorway;
	}
	
	public void tellLimit(){
		System.out.println("You are allowed to drive "+buildUpAreas+" km/h in built-up areas and "+motorway+" km/h on motorways.");
	}

	public void setBuildUpAreas(int buildUpAreas) {
		this.buildUpAreas = buildUpAreas;
	}
}
