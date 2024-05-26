package entities;

import java.io.Serializable;
import java.text.DecimalFormat; // For currency formatting
/** The Bill class represents a bill for park entry fees.
* It can be generated for both reservations and walk-in visitors.
*/
public class Bill  implements Serializable {

    /**
     * The base price for park entry (obtained from FullPrice class).
     */
    private float fullPrice = FullPrice.getFull_price();
    /**
     * The final price to be paid after applying discounts.
     */
    private float priceToPay;
    /**
     * The bill text containing reservation/visitor details, prices, and discounts.
     */
    private StringBuilder bill = new StringBuilder();
    /**
     * Formatter for displaying prices with two decimal places.
     */
    private final DecimalFormat priceFormatter = new DecimalFormat("0.00"); // Formatter for price

    /**
     * Constructs a Bill object for a reservation.
     *
     * @param reservation The reservation object containing details.
     * @throws NullPointerException if the reservation object is null.
     */
    public Bill(Reservation reservation) {
        priceToPay = fullPrice; // Set initial price

        bill.append("Reservation Bill \n");
        bill.append("Reservation Number :" + reservation.getReservationId() + "\n");
        bill.append("Park :" + reservation.getPark_name() + "\n");
        bill.append("Number Of Visitors : " + reservation.getNumberOfVisitors() + "\n");

        if (reservation.getIsGroup().equals("1")) { // Group Reservation
            priceToPay *= (reservation.getNumberOfVisitors() - 1);
            bill.append("Price : " + priceFormatter.format(priceToPay) + "\n"); // Formatted price
            bill.append("Discounts :\n");
            bill.append("* 25% Group Reservation\n");
            applyDiscount(25 / 100.0f); // Use float for percentage
            if (reservation.getIsPaid().equals("1")) {
                bill.append("* 12% For Prepayment\n");
                applyDiscount(12 / 100.0f);
            }
            bill.append("* Guide Not Paying * \n");
        } else {
            priceToPay *= reservation.getNumberOfVisitors();
            bill.append("Price : " + priceFormatter.format(priceToPay) + "\n"); // Formatted price
            bill.append("Discounts :\n");
            bill.append("* 15% Personal/Family Reservation\n");
            applyDiscount(15 / 100.0f);
        }

        bill.append("=> Price To Pay (Include Discount) : " + priceFormatter.format(priceToPay) + "\n");
    }
    /**
     * Constructs a Bill object for walk-in visitors.
     *
     * @param visitors The TravelerWithoutReservation object containing visitor details.
     * @throws NullPointerException if the visitors object is null.
     */
    public Bill(TravelerWithoutReservation visitors) {
        priceToPay = fullPrice; // Set initial price

        bill.append("Visitor Bill (No Reservation) \n");
        bill.append("Visitor ID  : " + visitors.getId() + "\n");
        bill.append("Park :" + visitors.getPark() + "\n");
        bill.append("Number Of Visitors : " + visitors.getNumberOfVisitors() + "\n");
        priceToPay *= visitors.getNumberOfVisitors();
        bill.append("Price : " + priceFormatter.format(priceToPay) + "\n"); // Formatted price

        if (visitors.getIsGroup().equals("1")) { // Group Reservation
            bill.append("Discounts :\n");
            bill.append("* 10% Group Reservation\n");
            bill.append("* Guide Must Paying * \n");
            applyDiscount(10 / 100.0f);
        }

        bill.append("=> Price To Pay (Include Discount) : " + priceFormatter.format(priceToPay) + "\n");
    }

    /**
     * Utility method that reduces the `priceToPay` by the provided discount percentage.
     *
     * @param discountPercentage The discount percentage (between 0.0 and 1.0).
     */
    private void applyDiscount(float discountPercentage) {
        priceToPay *= (1 - discountPercentage);
    }

    /**
     * Getter method to return the final price to be paid.
     *
     * @return The price to pay after discounts.
     */
    public float getPriceToPay() {
        return priceToPay;
    }


    /**
     * Overridden method that returns the complete bill text.
     *
     * @return The bill text as a String.
     */
    public String toString() {
        return bill.toString();
    }
}
