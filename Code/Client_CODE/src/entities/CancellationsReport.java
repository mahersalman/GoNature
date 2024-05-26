package entities;

import java.io.Serializable;
/**
 * The CancellationsReport class represents a report specifically for park cancellations.
 * It inherits from the Report class and adds fields related to cancellation details.
 *
 */
@SuppressWarnings("serial")
public class CancellationsReport extends Report implements Serializable {
    /**
     * The area for which the cancellation report is generated (optional, defaults to "All").
     */
    private String Area = "All";

    /**
     * The number of cancelled orders within the specified timeframe.
     */
    private String canceledOrder;

    /**
     * The number of orders that were not visited within the reservation timeframe.
     */
    private String NotVisitedOrders;

    /**
     * Constructs a CancellationsReport object based on an existing Report object.
     * This constructor sets the report type to "Cancel Report".
     *
     * @param report The Report object to use as a base.
     */
	public CancellationsReport(Report report) {
	       super(report.getMonth(),report.getYear(),report.getParkName(),"Cancel Report");
	}
	public String getArea() {
		return Area;
	}
	public void setArea(String area) {
		Area = area;
	}
	
	public String getCanceledOrder() {
		return canceledOrder;
	}
	public void setCanceledOrder(String canceledOrder) {
		this.canceledOrder = canceledOrder;
	}
	public String getNotVisitedOrders() {
		return NotVisitedOrders;
	}
	public void setNotVisitedOrders(String notVisitedOrders) {
		NotVisitedOrders = notVisitedOrders;
	}
	
}
