package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import Controller.ReservationController;
import entities.Employee;
import entities.GenereteUsageReport;
import entities.Message;
import entities.Reservation;
import entities.UpdateSettingTable;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ocsf.client.AbstractClient;
/**
 * A chat client class that extends the `AbstractClient` class for network communication.
 * This class provides functionality for a chat application client.
 */
public class ChatClient extends AbstractClient {
	/**
	 * The interface type variable. It allows the implementation of the display
	 * method in the client.
	 */
	ChatIF clientUI;
    /**
     * A static flag indicating whether the client is currently awaiting a response from the server.
     * This flag can be used to control the client's behavior while waiting for server responses
     */
	public static boolean awaitResponse = false;
    /**
     * A static HashMap that  stores messages received from the server.
     * The key is potentially a message identifier 
     * and the value is the corresponding message object.
     */
	public static HashMap<String, Message<?>> server_msg;
    /**
     * A static Employee object that  represents the currently logged-in employee.
     * This object might hold information about the employee's credentials, roles, or other details.
     */
	public static Employee employee;
    /**
     * A static ArrayList that  stores data for updating settings tables in the client's user interface.
     */
	public static ArrayList<UpdateSettingTable> updateSettingTable = new ArrayList<UpdateSettingTable>();

    /**
     * A static User object that  represents the currently logged-in user.
     */
	public static User user;

    /**
     * A static ObservableList containing Reservation objects.
     * This list used to display or manage user reservations in the client's user interface.
     */
	public static ObservableList<Reservation> User_reservation;

	/**
	 * Constructs an instance of the chat client.
	 *
	 * @param host     The server to connect to.
	 * @param port     The port number to connect on.
	 * @param clientUI The interface type variable.
	 */
	public static ChatClient instance;

	public ChatClient(String host, int port, ChatIF clientUI) throws IOException {
		super(host, port); // Call the superclass constructor
		this.clientUI = clientUI;
		server_msg = new HashMap<>();
	}

	/**
	 * the function handle message from server
	 * 
	 * @param Object object from server
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void handleMessageFromServer(Object msg) {
		if (msg instanceof Message) {

			Message<?> MessageFromServer = (Message<?>) msg;
			// Identifification
			if (MessageFromServer.getMsg().equals("Identification_Success")
					|| MessageFromServer.getMsg().equals("Identification_Failed")) {
				server_msg.put("Identification", MessageFromServer);
				if (MessageFromServer.getMsg().equals("Identification_Success")) {
					user = (User) MessageFromServer.getObject();
				}
			}
			// get User reservation
			else if (MessageFromServer.getMsg().equals("User_Reservation")) {
				User_reservation = FXCollections
						.observableArrayList((ArrayList<Reservation>) MessageFromServer.getObject());
			}
			// Create Reservation
			else if (MessageFromServer.getMsg().equals("Create_Reservation_Success")
					|| MessageFromServer.getMsg().equals("Create_Reservation_Failed")) {
				server_msg.put("Create_Reservation", MessageFromServer);
			}
			// Employee Login
			else if (MessageFromServer.getMsg().equals("Employee_Login_Sucesss")
					|| MessageFromServer.getMsg().equals("Employee_Login_Failed")) {
				server_msg.put("Employee_Login", MessageFromServer);
				employee = (Employee) MessageFromServer.getObject();

			}
			// Service Represintitive register a guide
			else if (MessageFromServer.getMsg().equals("Guide_Already_Register")
					|| MessageFromServer.getMsg().equals("Guide_Registered_Success")) {
				server_msg.put("Register_Guide", MessageFromServer);

			} else if (MessageFromServer.getMsg().equals("RequestsUpdateSettingTable")) {
				updateSettingTable = (ArrayList<UpdateSettingTable>) MessageFromServer.getObject();

			}

			else if (MessageFromServer.getMsg().equals("Get_Request_Update_Settings_DepartmentManager")) {
				updateSettingTable = (ArrayList<UpdateSettingTable>) MessageFromServer.getObject();

			}else if (MessageFromServer.getMsg().equals("RegisterToWaitingList")
					|| MessageFromServer.getMsg().equals("OrderUsingAvailableTimeTable")) {
				User_reservation.add((Reservation) MessageFromServer.getObject());
				ReservationController.newReservation = (Reservation) MessageFromServer.getObject();
			}  else if (MessageFromServer.getMsg().equals("Approve_Reservation")
					|| MessageFromServer.getMsg().equals("Cancel_Reservation")
					|| MessageFromServer.getMsg().equals("Paid_Success")) {
				for (Reservation reservation : User_reservation) {
					if (reservation.equals(MessageFromServer.getObject())) {
						reservation.setStatus(((Reservation) MessageFromServer.getObject()).getStatus());
						break;
					}
				}
			}  else if (MessageFromServer.getMsg().equals("Entry_Without_Reservation_NotGuide")
					|| MessageFromServer.getMsg().equals("Entry_Without_Reservation_EntrySuccess")
					|| MessageFromServer.getMsg().equals("Entry_Without_Reservation_NoPlace")) {
				server_msg.put("EntryWorker_EntryWithoutReservation", MessageFromServer);

			} else if (MessageFromServer.getMsg().equals("Visitors_Without_Reservation_Not_In_Park")
					|| MessageFromServer.getMsg().equals("Visitors_Without_Reservation_In_Another_Park")
					|| MessageFromServer.getMsg().equals("Visitors_Without_Reservation_Exit_Successfully_Registered")) {
				server_msg.put("EntryWorker_ExitVisitorsWithoutReservation", MessageFromServer);

			} else if (MessageFromServer.getMsg().equals("Bill_Reservation_DisplayFiled")
					|| MessageFromServer.getMsg().equals("Bill_Reservation_DisplaySuccess")) {
				server_msg.put("Bill_Reservation", MessageFromServer);

			}
			else {
				server_msg.put(MessageFromServer.getMsg(),MessageFromServer );

			}
		}
		else if ((msg instanceof String) && msg.equals("ServerStopped")) {
			JOptionPane.showMessageDialog(null, "Server Stopped .", "",
					JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		awaitResponse = false;
	}

	/**
	 * This method handles all data coming from the UI
	 *
	 * @param message The message from the UI.
	 */

	public void handleMessageFromClientUI(Object message) {
		try {
			awaitResponse = true;
			sendToServer(message);

			// wait for response
			while (awaitResponse) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();

		}
	}
	/**
	 * Initiates the client's connection process to the server.
	 *
	 * @throws IOException If an I/O error occurs while connecting to the server.
	 */
	public void join() throws IOException {
		openConnection();
	}

}
//End of ChatClient class
