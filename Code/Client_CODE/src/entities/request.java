package entities;

import java.io.Serializable;
/**
 * The request class represents a request made for a national park, such as a request for approval,
 * settings updates, or other actions. It stores the request's ID, type, status, and the park it relates to.
 *
 * @author (Your Name)
 * @version (Version Number)
 */
public class request implements Serializable {
    /**
     * The unique identifier of the request.
     */
    private String id;

    /**
     * The type of request (e.g., "Approval", "SettingsUpdate", "Other").
     */
    private String request_type;

    /**
     * The current status of the request (e.g., "Pending", "Approved", "Rejected").
     */
    private String status;

    /**
     * The park for which the request is made.
     */
    private Park park;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRequest_type() {
		return request_type;
	}
	public void setRequest_type(String request_type) {
		this.request_type = request_type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Park getPark() {
		return park;
	}
	public void setPark(Park park) {
		this.park = park;
	}
	
	
}
