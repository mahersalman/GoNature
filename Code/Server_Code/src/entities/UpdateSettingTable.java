package entities;

import java.io.Serializable;
/**
 * The UpdateSettingTable class represents a request to update settings for a national park.
 * It stores details such as the maximum number of visitors, maximum reservations,
 * average waiting time, status of the request, and the park name.
 *
 */
@SuppressWarnings("serial")
public class UpdateSettingTable implements Serializable {
    /**
     * The unique ID of the update request.
     */
    private int id;

    /**
     * The proposed maximum number of visitors allowed in the park.
     */
    private String MaxVisitors;

    /**
     * The proposed maximum number of reservations allowed in the park.
     */
    private String MaxReservations;

    /**
     * The average waiting time for visitors.
     */
    private String AverageWaitingTime;

    /**
     * The status of the update request.
     */
    private String Status;

    /**
     * The name of the park for which the settings are being updated.
     */
    private String park_name;
	
    public UpdateSettingTable(String maxVisitors, String maxReservations, String AverageWaitingTime, String status, String parkName) {
    	this.MaxVisitors = maxVisitors;
        this.MaxReservations = maxReservations;
        this.setAverageWaitingTime(AverageWaitingTime);
        this.Status = status;
        this.park_name = parkName;
    }
    
	public String getMaxVisitors() {
		return MaxVisitors;
	}
	public void setMaxVisitors(String maxVisitors) {
		MaxVisitors = maxVisitors;
	}
	public String getMaxReservations() {
		return MaxReservations;
	}
	public void setMaxReservations(String maxReservations) {
		MaxReservations = maxReservations;
	}

	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public String getPark_name() {
		return park_name;
	}
	public void setPark_name(String park_name) {
		this.park_name = park_name;
	}

	public String getAverageWaitingTime() {
		return AverageWaitingTime;
	}

	public void setAverageWaitingTime(String averageWaitingTime) {
		AverageWaitingTime = averageWaitingTime;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
