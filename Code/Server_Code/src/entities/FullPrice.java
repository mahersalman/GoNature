package entities;
/**
 * The FullPrice class represents the full price of a product or service. It
 * uses a static field to hold a single value that's accessible throughout
 * the application.
 *
 */
public class FullPrice {
    /**
     * The full price value, accessible globally.
     */
	private static float full_price ;

	
	public static float getFull_price() {
		return full_price;
	}

	public static void setFull_price(float full_price) {
		FullPrice.full_price = full_price;
	}
	
}
