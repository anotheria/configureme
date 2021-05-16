package configuremerepository;

import org.configureme.ConfigurationManager;

/**
 * TODO comment this class
 *
 * @author lrosenberg
 * @since 14.05.21 22:58
 */
public class RepositoryRunner {
	public static void main(String a[]) throws Exception{
		Example e = new Example();
		System.out.println("Before config: "+e);
		System.setProperty(ConfigurationManager.PROP_NAME_REPOSITORY_URL, "http://localhost:8085/");
		System.setProperty(ConfigurationManager.PROP_NAME_DEFAULT_PROFILE, "profilea_ab");
		ConfigurationManager.INSTANCE.configure(e);
		System.out.println("after config: "+e);

		while(true){
			System.out.println("TICK "+e);
			Thread.currentThread().sleep(10000);
		}

		
	}
}
