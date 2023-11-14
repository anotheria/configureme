package org.configureme.moskitocontroltest;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Configuration holder class for MoSKito Control. The configuration of MoSKito control is located in moskitocontrol.json file in the classpath.
 *
 * @author lrosenberg
 * @since 26.02.13 18:50
 */
@ConfigureMe (name="moskitocontrol", allfields = true)
@SuppressFBWarnings(value = {"EI_EXPOSE_REP2", "EI_EXPOSE_REP"}, justification = "This is the way configureMe works, it provides beans for access")
public class MoskitoControlTestConfiguration {


	/**
	 * Charts.
	 */
	@Configure
	private ChartConfig[] charts;



	public ChartConfig[] getCharts() {
		return charts;
	}

	public void setCharts(ChartConfig[] charts) {
		this.charts = charts;
	}



	/**
	 * Returns the active configuration instance. The configuration object will update itself if the config is changed on disk.
	 * @return configuration instance
	 */
	public static final MoskitoControlTestConfiguration getConfiguration(){
		return MoskitoControlConfigurationHolder.instance;
	}


	/**
	 * Loads a new configuration object from disk. This method is for unit testing.
	 * @return configuration object
	 */
	public static final MoskitoControlTestConfiguration loadConfiguration(){
		MoskitoControlTestConfiguration config = new MoskitoControlTestConfiguration();
		try{
			ConfigurationManager.INSTANCE.configure(config);
		}catch(IllegalArgumentException e){
			//ignored
		}
		return config;
	}




	/**
	 * Holder class for singleton instance.
	 */
	private static class MoskitoControlConfigurationHolder{
		/**
		 * Singleton instance of the MoskitoControlConfiguration object.
		 */
		static final MoskitoControlTestConfiguration instance;
		static{
			instance = new MoskitoControlTestConfiguration();
			try {
				ConfigurationManager.INSTANCE.configure(instance);
			}catch(IllegalArgumentException e){
				e.printStackTrace();
			}
		}
	}

}
