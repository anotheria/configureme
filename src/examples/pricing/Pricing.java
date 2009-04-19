package pricing;

import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;

@ConfigureMe 
public class Pricing {
	/**
	 * The local currency.
	 */
	@Configure private String currency;
	/**
	 * The price of the BigPack in local currency.
	 */
	@Configure private float price;
	
	public void setCurrency(String aCurrency){
		currency = aCurrency;
	}
	
	public void setPrice(float aPrice){
		price = aPrice;
	}
	
	/**
	 * Returns the price for the customer.
	 */
	public String getProductPrice(){
		return price+" "+currency;
	}
}
