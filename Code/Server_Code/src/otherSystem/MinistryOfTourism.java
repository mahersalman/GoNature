package otherSystem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import entities.FullPrice;
/**
 * The MinistryOfTourism class provides functionality for retrieving the full price
 * of a national park from an external source (Ministry of Tourism).
 *
 * @author Maher Salman

 */
public class MinistryOfTourism {

    /**
     * Retrieves the full price from a text file provided by the Ministry of Tourism.
     *
     * @throws Exception If an error occurs while reading the file.
     */
	public static void getFullPriceFromMinistryOfTourism() {
		String path = "MinistryOfTourismFullPrice.txt";
		String line;
		InputStream inputStream = UserManagementSystem.class.getResourceAsStream(path);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			while ((line = br.readLine()) != null) {
				FullPrice.setFull_price(Float.parseFloat(line));
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

}
