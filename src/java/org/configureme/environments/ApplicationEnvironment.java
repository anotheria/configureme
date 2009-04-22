package org.configureme.environments;

import org.configureme.Environment;
import org.configureme.GlobalEnvironment;

/**
 * This is a typical application environment. It contains of the system the application is running in (prod, test, dev, integration etc), 
 * the kind of application, the concrete service and the host the application has been deployed to.
 * @author lrosenberg
 */
public class ApplicationEnvironment implements Environment{

	/**
	 * The system the application is running in, usually its something like prod, test, integration, dev etc.
	 */
	private String system;
	/**
	 * The 'kind' of application, usually its something like web/www, wap, xml, soa, admin etc.
	 */
	private String app;
	/**
	 * The concrete service inside the application, something like userservice, accountingservice etc.
	 */
	private String service;
	/**
	 * The host the application has been deployed too, gives you the possibility to configure differently for failover machines.
	 */
	private String host;
	
	/**
	 * Creates a new ApplicationEnvironment with explicit parameters.
	 * @param aSystem the system
	 * @param anApp the app
	 * @param aService the service 
	 * @param aHost the host
	 */
	public ApplicationEnvironment(String aSystem, String anApp, String aService, String aHost){
		system = aSystem;
		app = anApp;
		service = aService;
		host = aHost;
	}
	
	/**
	 * Creates a new ApplicationEnvironment from an ApplicationEnvironment.Builder (pattern by Bloch, effective Java).
	 * @param builder
	 */
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
		
		if (system!=null && system.length()>0){
			ret.append(system);
			if (app!=null && app.length()>0){
				ret.append("_").append(app);
				if (service!=null && service.length()>0){
					ret.append("_").append(service);
					if (host!=null && host.length()>0){
						ret.append("_").append(host);
					}
				}
			}
		}
		
		return ret.toString();
	}
	
	
	/**
	 * The builder for the ApplicationEnvironment. See Bloch's Effective Java Sec. Edition for the pattern. Usage scenario:
	 * ApplicationEnvironment env = new ApplicationEnvironment.Builder().system("live").app("xml").host("xml01").build(); 
	 * @author lrosenberg
	 */
	public static class Builder{
		private String system;
		private String app;
		private String service;
		private String host;
		
		/**
		 * Creates new builder.
		 */
		public Builder(){
			system = app = service = host = "";
		}
		
		/**
		 * Sets the property system. Returns itself for chaining.
		 */
		public Builder system(String value){ system=value; return this; }
		
		/**
		 * Sets the property app. Returns itself for chaining.
		 */
		public Builder app(String value){ app=value; return this; }
		
		/**
		 * Sets the property service. Returns itself for chaining.
		 */
		public Builder service(String value){ service=value; return this; }

		/**
		 * Sets the property host. Returns itself for chaining.
		 */
		public Builder host(String value){ host=value; return this; }
		
		/**
		 * Creates a new ApplicationEnvironment from this builder.
		 */
		public ApplicationEnvironment build(){
			return new ApplicationEnvironment(this);
		}
	}

	/**
	 * Returns the value of the system property
	 * @return value of the system property
	 */
	public String getSystem() {
		return system;
	}

	/**
	 * Returns the value of the app property
	 * @return value of the app property.
	 */
	public String getApp() {
		return app;
	}

	/**
	 * Returns the service value.
	 * @return value of the service property
	 */
	public String getService() {
		return service;
	}

	/**
	 * Returns the value of the host property
	 * @return value for the host property
	 */
	public String getHost() {
		return host;
	}
	 
	/**
	 * Used internally to compare 2 strings for the purpose of the equals function. Return true if both strings are null or (not null and equal).
	 * @param a
	 * @param b
	 * @return
	 */
	private static boolean stringEquals(String a, String b){
		return a == b ||
			(a!=null && b!=null && a.equals(b));
	}
	
	@Override
	public boolean equals(Object o){
		if (!(o instanceof ApplicationEnvironment))
			return false;
		ApplicationEnvironment anotherEnvironment = (ApplicationEnvironment)o;
		return stringEquals(system, anotherEnvironment.system) && 
			stringEquals(app, anotherEnvironment.app) && 
			stringEquals(host, anotherEnvironment.host) &&	
			stringEquals(service, anotherEnvironment.service);
	}

}
