package entities;

import java.io.Serializable;
/**
 * The TotalVisitorsReport class represents a report on the total number of visitors to a national park
 * in a given month and year, categorized into groups and individuals.
 * It extends the Report class, adding specific fields for group and individual numbers.
 *
 */
@SuppressWarnings("serial")
public class TotalVisitorsReport extends Report implements Serializable {

    /**
     * The number of visitor groups for the specified period.
     */
    private String GroupsNumber;

    /**
     * The number of individual visitors for the specified period.
     */
    private String IndividualsNumbers;

    /**
     * Creates a new TotalVisitorsReport with complete information.
     *
     * @param month The month for which the report is generated.
     * @param year The year for which the report is generated.
     * @param ParkName The name of the park for which the report is generated.
     * @param GroupsNumber The number of visitor groups.
     * @param IndividualsNumbers The number of individual visitors.
     */
	public TotalVisitorsReport(String month,String year,String ParkName,String GroupsNumber,String IndividualsNumbers) {
	       super(month,year,ParkName,"Total Visitors");
	       this.GroupsNumber=GroupsNumber;
	       this.IndividualsNumbers=IndividualsNumbers;
	}

    /**
     * Creates a new TotalVisitorsReport based on an existing Report object,
     * inheriting month, year, park name, and report type.
     *
     * @param report The existing Report object to base this report on.
     */
	public TotalVisitorsReport(Report report) {
	       super(report.getMonth(),report.getYear(),report.getParkName(),"Total Visitors");
	}
	public String getGroupsNumber() {
		return GroupsNumber;
	}
	public void setGroupsNumber(String groupsNumber) {
		GroupsNumber = groupsNumber;
	}
	
	public String getIndividualsNumbers() {
		return IndividualsNumbers;
	}
	public void setIndividualsNumbers(String individualsNumbers) {
		IndividualsNumbers = individualsNumbers;
	}
	
}
