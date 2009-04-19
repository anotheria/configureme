package pricing;

import org.configureme.annotations.ConfigureMe;

@ConfigureMe (allfields=true) 
public class Pricing {
	/**
	 * The local currency.
	 */
	private String currency;
	/**
	 * The price of the BigPack in local currency.
	 */
	private float price;
	
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
