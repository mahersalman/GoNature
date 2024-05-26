package entities;

import java.io.Serializable;
/**
 * The ConnectedClients class represents a client that is currently connected to the system.
 * It stores information about the client's IP address, hostname, role, and unique identifier.
 *
 */
@SuppressWarnings("serial")
public class ConnectedClients implements Serializable {
    /**
     * The IP address of the connected client.
     */
    private String ip;

    /**
     * The hostname of the connected client.
     */
    private String hostName;

    /**
     * The role of the connected client .
     */
    private String role;

    /**
     * The unique identifier of the connected client.
     */
    private String id;

    /**
     * Constructs a ConnectedClients object with all client details.
     *
     * @param ip The client's IP address.
     * @param hostName The client's hostname.
     * @param role The client's role.
     * @param id The client's unique identifier.
     */

	public ConnectedClients(String ip ,String hostName,String role, String id) {
		this.ip = ip;
		this.hostName = hostName;
		this.id = id;
		this.role = role;

	}

	public String getIp() {
		return ip;
	}

	public String getHostName() {
		return hostName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
}
