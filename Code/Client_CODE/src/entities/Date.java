package entities;

import java.io.Serializable;
/**
 * The DateTime class represents a specific date and time combination.
 * It's immutable, meaning the date and time cannot be changed after creation.
 *

 */
@SuppressWarnings("serial")
public class Date implements Serializable{
    /**
     * The hour value in HH format.
     */
	private String hour;

    /**
     * The date value in MM/dd/YYYY format.
     */
	private String date;
	
    /**
     * Constructs a DateTime object with the specified date and time.
     *
     * @param date 
     * @param hour 
     */
	public Date(String date , String hour) {
		this.setHour(hour);
		this.setDate(date);
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
}
