package org.configureme.environments;

import org.configureme.Environment;
import org.configureme.GlobalEnvironment;

public class ApplicationEnvironment implements Environment{

	private String system;
	private String app;
	private String service;
	private String host;
	
	public ApplicationEnvironment(String aSystem, String anApp, String aService, String aHost){
		system = aSystem;
		app = anApp;
		service = aService;
		host = aHost;
	}
	
	private ApplicationEnvironment(Builder builder){
		system = builder.system;
		app = builder.app;
		service = builder.service;
		host = builder.host;
	}
	
	@Override
	public String expandedStringForm() {
		return toString();
	}

	@Override
	public boolean isReduceable() {
		return system!=null && system.length()>0;
	}

	@Override
	public Environment reduce() {
		if (host!=null && host.length()>0)
			return new ApplicationEnvironment(system, app, service, "");
		if (service!=null && service.length()>0)
			return new ApplicationEnvironment(system, app, "", "");
		if (app!=null && app.length()>0)
			return new ApplicationEnvironment(system, "", "", "");
		if (system!=null && system.length()>0)
			return GlobalEnvironment.INSTANCE;
		throw new AssertionError("Environment isn't reduceable, have you called isReduceable() prior to reduce()?");
	}


	public String toString(){
		StringBuilder ret = new StringBuilder();
		
		return ret.toString();
	}
	
	
	public static class Builder{
		private String system;
		private String app;
		private String service;
		private String host;
		
		public Builder(){
			system = app = service = host = "";
		}
		
		public Builder system(String value){ system=value; return this; }
		
		public Builder app(String value){ app=value; return this; }
		
		public Builder service(String value){ service=value; return this; }

		public Builder host(String value){ host=value; return this; }
		
		public ApplicationEnvironment build(){
			return new ApplicationEnvironment(this);
		}
	}
}
