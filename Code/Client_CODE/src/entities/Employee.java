package entities;

import java.io.Serializable;
/**
 * The Employee class represents an employee in the system.
 * It stores information about the employee's personal details, work details,
 * credentials, and current status.
 *
 */
@SuppressWarnings("serial")
public class Employee implements Serializable{

    /**
     * The full name of the employee.
     */
    private String fullName;

    /**
     * The employee's unique identification number.
     */
    private int employeeNumber;

    /**
     * The employee's email address.
     */
    private String email;

    /**
     * The employee's job role within the organization.
     */
    private String role;

    /**
     * The employee's username for logging into the system.
     */
    private String userName;

    /**
     * The employee's password for authentication.
     */
    private String password;

    /**
     * The park where the employee primarily works.
     */
    private String park;

    /**
     * Indicates whether the employee is currently logged into the system.
     */
    private String isLogged;

    /**
     * Constructs a basic Employee object with only a username and password.
     * This constructor is likely used for login purposes.
     *
     * @param userName The employee's username.
     * @param password The employee's password.
     */
    public Employee(String userName,String password) {
    	super();
    	this.userName = userName;
    	this.password = password;
    }

    /**
     * Constructs a complete Employee object with all employee details.
     *
     * @param fullName The employee's full name.
     * @param employeeNumber The employee's unique identification number.
     * @param email The employee's email address.
     * @param role The employee's job role.
     * @param userName The employee's username.
     * @param password The employee's password.
     * @param park The park where the employee works.
     * @param isLogged The employee's current login status.
     */
    public Employee(String fullName, int employeeNumber, String email, String role, String userName, String password, String park,String isLogged) {
        super();
    	this.setFullName(fullName);
        this.setEmployeeNumber(employeeNumber);
        this.setEmail(email);
        this.setRole(role);
        this.setUserName(userName);
        this.setPassword(password);
        this.setPark(park);
        this.isLogged = isLogged;
    }

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public int getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(int employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPark() {
		return park;
	}

	public void setPark(String park) {
		this.park = park;
	}
	public String getIsLogged() {
		return isLogged;
	}
	public void setIsLogged(String isLogged) {
		this.isLogged = isLogged;
	}

}
