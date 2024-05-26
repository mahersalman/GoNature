/**
 * The Park class represents a national park.
 * It stores information about the park name, maximum visitor capacity,
 * maximum number of reservations allowed, average visiting time, and
 * the current number of visitors in the park.
 *

 */
package entities;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Park implements Serializable {

    /**
     * The name of the national park.
     */
    private String name;

    /**
     * The maximum number of visitors allowed in the park at once.
     */
    private int maxVisitors;

    /**
     * The maximum number of reservations allowed in the park.
     */
    private int maxReservations;

    /**
     * The average time (in hours) that visitors spend in the park.
     */
    private float avgVisitingTime;

    /**
     * The current number of visitors in the park.
     */
    private int currentlyVisitorsInPark;

    /**
     * Creates a new Park object with complete information.
     *
     * @param name The name of the national park.
     * @param maxVisitors The maximum visitor capacity of the park.
     * @param maxReservations The maximum number of reservations allowed.
     * @param avgVisitingTime The average visiting time in hours.
     * @param currentlyVisitorsInPark The current number of visitors in the park.
     */
	public Park(String name, int maxVisitors, int maxReservations , float avgVisitingTime ,int currentlyVisitorsInPark) {
		this.name = name;
		this.maxVisitors = maxVisitors;
		this.setMaxReservations(maxReservations);
		this.avgVisitingTime = avgVisitingTime;
		this.currentlyVisitorsInPark = currentlyVisitorsInPark;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public int getMaxVisitors() {
		return maxVisitors;
	}
	public void setMaxVisitors(int maxVisitors) {
		this.maxVisitors = maxVisitors;
	}
	public float getAvgVisitingTime() {
		return avgVisitingTime;
	}
	public void setAvgVisitingTime(float avgVisitingTime) {
		this.avgVisitingTime = avgVisitingTime;
	}
	public int getMaxReservations() {
		return maxReservations;
	}
	public void setMaxReservations(int maxReservations) {
		this.maxReservations = maxReservations;
	}

	public int getCurrentlyVisitorsInPark() {
		return currentlyVisitorsInPark;
	}

	public void setCurrentlyVisitorsInPark(int currentlyVisitorsInPark) {
		this.currentlyVisitorsInPark = currentlyVisitorsInPark;
	}
	
	
}
