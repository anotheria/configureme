package links;

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
 * Config for link example
 *
 * @author ivanbatura
 * @since: 24.09.12
 */
@ConfigureMe(name = "links")
public class LinksConfig {
	@Configure
	Inner inner;
	@Configure
	private String street;
	@Configure
	private int[] blockNumbers;
	/**
	 * Usually you don't need to store the environment, its made here, to make the debug messages more verbose.
	 */
	private Environment environment;

	public LinksConfig(Environment anEnvironment) {
		environment = anEnvironment;
	}

	public String toString() {
		String blockNumbersArray = "";
		if (blockNumbers != null)
			for (int i : blockNumbers)
				blockNumbersArray += i + " ,";
		return "!!! Configurable " + environment + " street:" + street + " blockNumbers=" + blockNumbersArray + " inner =" + inner;
	}

	public void setStreet(String street) {
		this.street = street;
		System.out.println(this + ": property street has been set to: " + street);
	}

	public void setBlockNumbers(int[] blockNumbers) {
		this.blockNumbers = blockNumbers;
		System.out.println(this + ": property blockNumbers has been set to: " + blockNumbers);
	}

	public void setInner(Inner inner) {
		this.inner = inner;
		System.out.println(this + ": inner blockNumbers has been set to: " + inner);
	}

	//	public void setInner(InnerConfig inner) {
//		this.inner = inner;
//		System.out.println(this + ": property inner has been set to: " + inner);
//	}
//
//	public InnerConfig getInner() {
//		return inner;
//	}

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

	public class Inner {
		String innerString;

		public String getInnerString() {
			return innerString;
		}

		public void setInnerString(String innerString) {
			this.innerString = innerString;
		}

		@Override
		public String toString() {
			return "Inner{" +
					"innerString='" + innerString + '\'' +
					'}';
		}
	}
}
