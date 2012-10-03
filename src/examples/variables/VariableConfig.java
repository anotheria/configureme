package variables;

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
 * Config for variable examle
 *
 * @author ivanbatura
 * @since: 24.09.12
 */
@ConfigureMe(name = "variables")
public class VariableConfig {
	@Configure
	private String variable;

	/**
	 * Usually you don't need to store the environment, its made here, to make the debug messages more verbose.
	 */
	private Environment environment;

	public VariableConfig(Environment anEnvironment) {
		environment = anEnvironment;
	}

	public String toString() {
		return "!!! Configurable " + environment + " variable: " + variable;
	}

	public void setVariable(String aValue) {
		variable = aValue;
		System.out.println(this + ": my property variable has been set to: " + variable);
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