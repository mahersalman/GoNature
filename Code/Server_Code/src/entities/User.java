package entities;

import java.io.Serializable;
/**
 * The User class represents a user in the national park system.
 * It stores the user's ID, whether they are a guide, and whether they are currently logged in.
 *
 */
@SuppressWarnings("serial")
public class User implements Serializable{

    /**
     * The unique identifier of the user.
     */
	private String id;
    /**
     * Indicates whether the user is a registered guide.
     */
	private boolean isGuide;
    /**
     * Indicates whether the user is currently logged in to the system.
     */
	private boolean isLogged;

    /**
     * Creates an empty User object (no-argument constructor).
     */
	public User() {}
    /**
     * Creates a User object with the specified ID, guide status, and logged-in status.
     *
     * @param id The unique identifier of the user.
     * @param isGuide True if the user is a guide, false otherwise.
     * @param isLogged True if the user is logged in, false otherwise.
     */
	public User(String id,boolean isGuide, boolean isLogged) {
		this.id= id;
		this.isGuide = isGuide;
		this.isLogged = isLogged;
	}
	
    // Getters and setters for all fields

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public boolean isGuide() {
		return isGuide;
	}
	public void setGuide(boolean isGuide) {
		this.isGuide = isGuide;
	}
	public boolean isLogged() {
		return isLogged;
	}
	public void setLogged(boolean isLogged) {
		this.isLogged = isLogged;
	}

	

}
