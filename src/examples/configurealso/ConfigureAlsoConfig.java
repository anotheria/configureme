package configurealso;

import org.configureme.Environment;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureAlso;
import org.configureme.annotations.ConfigureMe;

/**
 * @author ivanbatura
 * @since: 07.10.12
 */
@ConfigureMe(name = "configurealso")
public class ConfigureAlsoConfig {
	@Configure
	private String simple;
	@ConfigureAlso
	private InnerConfig also;

	/**
	 * Usually you don't need to store the environment, its made here, to make the debug messages more verbose.
	 */
	private Environment environment;

	public ConfigureAlsoConfig(Environment anEnvironment) {
		environment = anEnvironment;
	}

	public void setSimple(String simple) {
		this.simple = simple;
		System.out.println(this + ": property simple has been set to: " + simple);
	}

	public void setAlso(InnerConfig also) {
		this.also = also;
		System.out.println(this + ": property also has been set to: " + also);
	}

	public String getSimple() {
		return simple;
	}

	public InnerConfig getAlso() {
		return also;
	}

	@Override
	public String toString() {
		return "!!! Configurable " + environment + ", simple='" + simple + ", also=" + also + " ;";
	}
}
