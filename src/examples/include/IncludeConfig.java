package include;

import org.configureme.Environment;
import org.configureme.annotations.AfterConfiguration;
import org.configureme.annotations.AfterInitialConfiguration;
import org.configureme.annotations.AfterReConfiguration;
import org.configureme.annotations.BeforeConfiguration;
import org.configureme.annotations.BeforeInitialConfiguration;
import org.configureme.annotations.BeforeReConfiguration;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;

/**
 * Config for include file example
 *
 * @author ivanbatura
 * @since: 24.09.12
 */
@ConfigureMe(name = "include")
public class IncludeConfig {
	@Configure
	private String country;
	@Configure
	private String city;
	/**
	 * Usually you don't need to store the environment, its made here, to make the debug messages more verbose.
	 */
	private final Environment environment;

	public IncludeConfig(Environment anEnvironment) {
		environment = anEnvironment;
	}

	public String toString() {
		return "!!! Configurable " + environment + " country: " + country + " city:"+ city;
	}


	public void setCountry(String country) {
		this.country = country;
		System.out.println(this + ": property country has been set to: " + country);
	}

	public void setCity(String city) {
		this.city = city;
		System.out.println(this + ": property city has been set to: " + city);
	}

	@BeforeConfiguration
	public void callBeforeEachConfiguration() {
		System.out.println(this + " will be configured now");
	}

	@BeforeInitialConfiguration
	public void callBeforeInitialConfigurationOnly() {
		System.out.println(this + " will be INITIALY configured now");
	}

	@BeforeReConfiguration
	public void callBeforeReConfigurationOnly() {
		System.out.println(this + " will be RE-configured now");
	}

	@AfterConfiguration
	public void callAfterEachConfiguration() {
		System.out.println(this + " has been configured");
	}

	@AfterInitialConfiguration
	public void callAfterInitialConfigurationOnly() {
		System.out.println(this + " has been INITIALY-configured");
	}

	@AfterReConfiguration
	public void callAfterReConfigurationOnly() {
		System.out.println(this + " has been RE-configured");
	}
}