package include;

import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;

/**
 * Example of inner configurable class with help of includes
 * Example for ExternalConfiguration
 *
 * @author ivanbatura
 * @since: 26.09.12
 */
@ConfigureMe(name="externalConfig")
public class InnerConfig {
	@Configure
	private String externalAttribute;

	public InnerConfig() {
	}

	public void setExternalAttribute(String externalAttribute) {
		this.externalAttribute = externalAttribute;
		System.out.println(this + ": property externalAttribute has been set to: " + externalAttribute);
	}

	public String getExternalAttribute() {

		return externalAttribute;
	}

	@Override
	public String toString() {
		return "InnerConfig{" +
				"externalAttribute='" + externalAttribute + '\'' +
				'}';
	}
}



