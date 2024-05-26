package entities;

import java.io.Serializable;
/**
 * The Reservation class represents a reservation made by a user for a national park visit.
 * It stores information about the reservation ID, user ID, visit date, visit hour,
 * number of visitors, park name, contact details, group status, payment status, and overall status.
 */
@SuppressWarnings("serial")
public class Reservation implements Serializable {
    /**
     * The unique identifier of the reservation.
     */
    private int reservationId;

    /**
     * The unique identifier of the user who made the reservation.
     */
    private String userID;

    /**
     * The date of the park visit (formatted as YYYY-MM-DD).
     */
    private String visitDate;

    /**
     * The hour of the park visit (formatted as HH:MM:SS).
     */
    private String visitHour;

    /**
     * The total number of visitors included in the reservation.
     */
    private int numberOfVisitors;

    /**
     * The name of the park for which the reservation is made.
     */
    private String park_name;

    /**
     * The email address of the user who made the reservation.
     */
    private String email;

    /**
     * The phone number of the user who made the reservation.
     */
    private String phoneNumber;

    /**
     * Indicates whether the reservation is for a group ("1") or not ("0").
     */
    private String IsGroup;

    /**
     * The current status of the reservation (e.g., "Pending", "Approved", "Canceled").
     */
    private String status;

    /**
     * Indicates whether payment has been made for the reservation ("1") or not ("0").
     */
    private String IsPaid;

    /**
     * Creates a new Reservation object with complete information.
     *
     * @param reservationId The unique identifier of the reservation.
     * @param userID The unique identifier of the user who made the reservation.
     * @param visitDate The date of the park visit.
     * @param visitHour The hour of the park visit.
     * @param numberOfVisitors The number of visitors included in the reservation.
     * @param park_name The name of the park for which the reservation is made.
     * @param email The email address of the user who made the reservation.
     * @param phoneNumber The phone number of the user who made the reservation.
     * @param IsGroup Indicates whether the reservation is for a group or not.
     * @param status The current status of the reservation.
     * @param IsPaid Indicates whether payment has been made for the reservation.
     */
	public Reservation( int reservationId ,String userID, String visitDate, String visitHour,
			int numberOfVisitors, String park_name, String email, String phoneNumber, String IsGroup, String status,String IsPaid) {
		super();
		this.reservationId = reservationId;
		this.userID = userID;
		this.setVisitDate(visitDate);
		this.setVisitHour(visitHour);
		this.setNumberOfVisitors(numberOfVisitors);
		this.setPark_name(park_name);
		this.setEmail(email);
		this.setPhoneNumber(phoneNumber);
		this.IsGroup = IsGroup;
		this.setStatus(status);
		this.IsPaid = IsPaid;
	}
	
	public int getReservationId() {
		return reservationId;
	}
	public void setReservationId(int id) {
		this.reservationId = id;
	}

	public String getUserID() {
		return userID;
	}

	public String getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(String visitDate) {
		this.visitDate = visitDate;
	}

	public String getVisitHour() {
		return visitHour;
	}

	public void setVisitHour(String visitHour) {
		this.visitHour = visitHour;
	}


	public String getPark_name() {
		return park_name;
	}

	public void setPark_name(String park_name) {
		this.park_name = park_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIsGroup() {
		return IsGroup;
	}

	public void setIsGroup(String isGroup) {
		IsGroup = isGroup;
	}
    /**
     * Overridden equals method to compare Reservation objects based on their reservation ID.
     *
     * @param obj The object to compare with.
     * @return True if the objects have the same reservation ID, false otherwise.
     */
	@Override
	  public boolean equals(Object obj) {
	    if (this == obj) {
	      return true;
	    }
	    if (obj == null || getClass() != obj.getClass()) {
	      return false;
	    }
	    Reservation other = (Reservation) obj;
	    return  reservationId==other.reservationId;
	  }

	public String getIsPaid() {
		return IsPaid;
	}

	public void setIsPaid(String isPaid) {
		IsPaid = isPaid;
	}

	public int getNumberOfVisitors() {
		return numberOfVisitors;
	}

	public void setNumberOfVisitors(int numberOfVisitors) {
		this.numberOfVisitors = numberOfVisitors;
	}



}
