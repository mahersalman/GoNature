package entities;

import java.io.Serializable;
/**
 * The TravelerWithoutReservation class represents a visitor who has entered a national park
 * without making a  reservation.
 */
@SuppressWarnings("serial")
public class TravelerWithoutReservation implements Serializable {

    /**
     * The name of the park where the visitor is located.
     */
    private String park;

    /**
     * The unique identifier of the visitor 
     */
    private String id;

    /**
     * The total number of visitors in the group.
     */
    private int numberOfVisitors;

    /**
     * Indicates whether the visitor is part of a group ("1") or not ("0").
     */
    private String isGroup;

    /**
     * The current status of the visitor 
     */
    private String status;
;

	public TravelerWithoutReservation(String park, String id, int numberOfVisitors, String isGroup, String status) {
		this.park = park;
		this.id = id;
		this.numberOfVisitors = numberOfVisitors;
		this.isGroup = isGroup;
		this.status = status;

	}

	public TravelerWithoutReservation(String park, String id) {
		this.park = park;
		this.id = id;

	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getNumberOfVisitors() {
		return numberOfVisitors;
	}

	public void setNumberOfVisitors(int numberOfVisitors) {
		this.numberOfVisitors = numberOfVisitors;
	}

	public String getIsGroup() {
		return isGroup;
	}

	public void setIsGroup(String isGroup) {
		this.isGroup = isGroup;
	}

	public String getPark() {
		return park;
	}

	public void setPark(String park) {
		this.park = park;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
