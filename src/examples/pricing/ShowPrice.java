package pricing;

import org.configureme.ConfigurationManager;
import org.configureme.Environment;
import org.configureme.GlobalEnvironment;
import org.configureme.environments.DynamicEnvironment;

public class ShowPrice {
	public static void main(String a[]){
		showPrice();
		showPriceIn("USA", GlobalEnvironment.INSTANCE);
		showPriceIn("United Kingdom", new DynamicEnvironment("europe", "uk"));
		showPriceIn("Germany", new DynamicEnvironment("europe", "de"));
		showPriceIn("Austria", new DynamicEnvironment("europe", "at"));
	}
	
	private static void showPriceIn(String description, Environment environment){
		Pricing pricing = new Pricing();
		ConfigurationManager.INSTANCE.configure(pricing, environment);
		System.out.println("Price in "+description+" is "+pricing.getProductPrice());
	}
	
	private static void showPrice(){
		Pricing pricing = new Pricing();
		ConfigurationManager.INSTANCE.configure(pricing);
		System.out.println("Please pay "+pricing.getProductPrice());
	}
}
