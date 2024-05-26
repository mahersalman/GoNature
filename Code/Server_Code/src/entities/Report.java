package entities;

import java.io.Serializable;
/**
 * The Report class represents a generic report for a national park.
 * It stores information about the month, year, park name, and report type.
 * Reports can be extended for specific purposes (e.g., TotalVisitorsReport).
 *
 * @author (Your Name)

 */
@SuppressWarnings("serial")
public class Report implements Serializable {

    /**
     * The month for which the report is generated (formatted as MM).
     */
    private String month;

    /**
     * The year for which the report is generated (formatted as YYYY).
     */
    private String year;

    /**
     * The name of the park for which the report is generated.
     */
    private String ParkName;

    /**
     * The type of report (e.g., "Total Visitors", "Revenue", "Visitor Feedback").
     */
    private String Type;

    /**
     * Creates a new Report object with complete information.
     *
     * @param month The month for which the report is generated.
     * @param year The year for which the report is generated.
     * @param ParkName The name of the park for which the report is generated.
     * @param Type The type of report.
     */
	public Report(String month,String year,String ParkName,String Type) {
		this.month=month;
		this.year=year;
		this.ParkName=ParkName;
		this.Type=Type;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getParkName() {
		return ParkName;
	}
	public void setParkName(String parkName) {
		ParkName = parkName;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	
}
