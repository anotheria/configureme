package org.configureme.environments;

import org.configureme.Environment;
import org.configureme.GlobalEnvironment;

/**
 * This is a typical application environment. It contains of the system the application is running in (prod, test, dev, integration etc),
 * the kind of application, the concrete service and the host the application has been deployed to.
 *
 * @author lrosenberg
 * @version $Id: $Id
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
	 *
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
	
	/** {@inheritDoc} */
	@Override
	public String expandedStringForm() {
		return toString();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isReduceable() {
		return system!=null && !system.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public Environment reduce() {
		if (host!=null && !host.isEmpty())
			return new ApplicationEnvironment(system, app, service, "");
		if (service!=null && !service.isEmpty())
			return new ApplicationEnvironment(system, app, "", "");
		if (app!=null && !app.isEmpty())
			return new ApplicationEnvironment(system, "", "", "");
		if (system!=null && !system.isEmpty())
			return GlobalEnvironment.INSTANCE;
		throw new AssertionError("Environment isn't reduceable, have you called isReduceable() prior to reduce()?");
	}


	/** {@inheritDoc} */
	@Override public String toString(){
		StringBuilder ret = new StringBuilder();
		
		if (system!=null && !system.isEmpty()){
			ret.append(system);
			if (app!=null && !app.isEmpty()){
				ret.append('_').append(app);
				if (service!=null && !service.isEmpty()){
					ret.append('_').append(service);
					if (host!=null && !host.isEmpty()){
						ret.append('_').append(host);
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
		/**
		 * system.
		 */
		private String system;
		/**
		 * app.
		 */
		private String app;
		/**
		 * service.
		 */
		private String service;
		/**
		 * host.
		 */
		private String host;
		
		/**
		 * Creates new builder.
		 */
		public Builder(){
			system = "";
			app = "";
			service = "";
			host = "";
		}
		
		/**
		 * Sets the property system. 
		 * @return self for chaining
		 */
		public Builder system(String value){
			system=value; return this;
		}
		
		/**
		 * Sets the property app.
		 * @return self for chaining
		 */
		public Builder app(String value){
			app=value; return this;
		}
		
		/**
		 * Sets the property service.
		 * @return self for chaining
		 */
		public Builder service(String value){
			service=value; return this;
		}

		/**
		 * Sets the property host.
		 * @return self for chaining
		 */
		public Builder host(String value){
			host=value; return this;
		}
		
		/**
		 * Creates a new ApplicationEnvironment from this builder.
		 * @return ApplicationEnvironment instance
		 */
		public ApplicationEnvironment build(){
			return new ApplicationEnvironment(this);
		}
	}

	/**
	 * Returns the value of the system property.
	 *
	 * @return value of the system property
	 */
	public String getSystem() {
		return system;
	}

	/**
	 * Returns the value of the app property.
	 *
	 * @return value of the app property.
	 */
	public String getApp() {
		return app;
	}

	/**
	 * Returns the service value.
	 *
	 * @return value of the service property
	 */
	public String getService() {
		return service;
	}

	/**
	 * Returns the value of the host property.
	 *
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
	
	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((app == null) ? 0 : app.hashCode());
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((service == null) ? 0 : service.hashCode());
		result = prime * result + ((system == null) ? 0 : system.hashCode());
		return result;
	}

	/** {@inheritDoc} */
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
