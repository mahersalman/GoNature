/**
 * The GenereteUsageReport class save data to generate a usage report for a specific park,
 * date, and hour. It's likely used to gather information about park usage patterns for analysis.
 *
 * @author (Your Name)
 * @version (Version Number)
 */
package entities;

import java.io.Serializable;

/**
 * Suppressing serial warning for clarity, as this class is intended for data
 * transfer.
 */
@SuppressWarnings("serial")
public class GenereteUsageReport implements Serializable {

	/**
	 * The name of the park for which to generate the usage report.
	 */
	private String parkName;

	/**
	 * The date for which to generate the usage report (formatted as YYYY-MM-DD).
	 */
	private String date;

	/**
	 * The hour for which to generate the usage report (formatted as HH:MM:SS).
	 */
	private String hour;

	/**
	 * Creates a new GenereteUsageReport object with complete information.
	 *
	 * @param date     The date for the report.
	 * @param hour     The hour for the report.
	 * @param parkName The name of the park for the report.
	 */
	public GenereteUsageReport(String date, String hour, String parkName) {
		this.parkName = parkName;
		this.hour = hour;
		this.date = date;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getParkName() {
		return parkName;
	}

	public void setParkName(String parkName) {
		this.parkName = parkName;
	}

}
