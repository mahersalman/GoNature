package Server;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import controller.ServerController;
import database.DataBaseController;
import entities.Bill;
import entities.CancellationsReport;
import entities.ConnectedClients;
import entities.Date;
import entities.Employee;
import entities.GenereteUsageReport;
import entities.TravelerWithoutReservation;
import entities.User;
import entities.Message;
import entities.Park;
import entities.Report;
import entities.Reservation;
import entities.TotalVisitorsReport;
import ocsf.server.*;
/**
 * The EchoServer class implements a server for a national park management system.
 * It extends the AbstractServer class from the ocsf library and handles client connections
 * and messages. The server interacts with the database for various operations .
 *
 * @author Maher
 */
public class EchoServer extends AbstractServer {
    /**
     * Provides a reference to the ServerController object for server control.
     */
	public static ServerController serverController;

    /**
     * Stores a list of connected clients 
     */
	public static ArrayList<ConnectedClients> clients = new ArrayList<ConnectedClients>();
	   /**
     * Maps client connections to their corresponding ConnectedClients objects for easy access.
     */
	public static HashMap<ConnectionToClient, ConnectedClients> clientsMap = new HashMap<ConnectionToClient, ConnectedClients>();
    /**
     * Defines the default port number on which the server listens for connections.
     */
	final public static int DEFAULT_PORT = 5555;
	
    /**
     * Constructor Creates an EchoServer instance using the specified port.
     *
     * @param port The port number on which the server will listen for connections.
     */
	public EchoServer(int port) {
		super(port);

	}


    /**
     * This method handles any messages received from the client.
     * It parses the message, identifies the message type, and performs the
     * corresponding operation using the DataBaseController. It then sends a response
     * message back to the client.
     *
     * @param msg The message received from the client.(include String msg and object)
     * @param client The connection from which the message originated.
     * @throws IOException If an I/O error occurs while sending a message to the client.
     */
	@SuppressWarnings("unchecked")
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		serverController.writeToLog(((Message) msg).getMsg() + " : Client -> " + client);
		if (msg instanceof Message) {
			Message<?> tmp = (Message<?>) msg;
			Reservation reservation = null;
			switch (tmp.getMsg()) {
			case "add_client":
				clients.add((ConnectedClients) tmp.getObject());
				clientsMap.put(client, (ConnectedClients) tmp.getObject());
				serverController.addConnection();
				break;
			case "Identification":
				DataBaseController.identification(tmp);
				break;
			case "User_Logout":
				User user = (User) tmp.getObject();
				DataBaseController.UpdateUserLogged("0", user.getId());
				clients.remove(clientsMap.get(client));
				serverController.refresh();
				break;
			case "User_Reservation":
				tmp = DataBaseController.GetUserReservations(tmp);
				break;
			case "Create_Reservation":
				if (DataBaseController.checkAvailibility((Reservation) tmp.getObject())) {
					((Reservation) tmp.getObject()).setStatus("Confirmed");
					DataBaseController.createReservation(tmp);
					DataBaseController.sendNotification(((Reservation)tmp.getObject()).getUserID(),((Reservation)tmp.getObject()).getReservationId(),"Reservation Created Successfully.");
					tmp.setMsg("Create_Reservation_Success");

				} else {
					tmp.setMsg("Create_Reservation_Failed");
				}

				break;
			case "Approve_Reservation":
				DataBaseController.UpdateReservationStatus((Reservation) tmp.getObject());
				DataBaseController.sendNotification(((Reservation)tmp.getObject()).getUserID(),((Reservation)tmp.getObject()).getReservationId(),"Reservation Approved Successfully.");
				break;
			case "Cancel_Reservation":
				reservation = ((Reservation) tmp.getObject());
				reservation.setStatus("Canceled");
				DataBaseController.UpdateReservationStatus(reservation);
				DataBaseController.UpdateParkVacancy_visitorsWithReservation((-1)*reservation.getNumberOfVisitors() ,
						reservation.getPark_name(), reservation.getVisitDate(), reservation.getVisitHour());
				DataBaseController.sendNotification(((Reservation)tmp.getObject()).getUserID(),((Reservation)tmp.getObject()).getReservationId(),"Reservation Canceled Successfully.");

				break;

			case "Employee_Login":
				DataBaseController.Login(tmp);
				break;
				
			case "Employee_Logout":
				Employee employee = (Employee) tmp.getObject();
				DataBaseController.UpdateEmployeeLogged("0", employee.getUserName(), employee.getPassword());
				clients.remove(clientsMap.get(client));
				serverController.refresh();
				break;

			case "GetParkInfo":
				DataBaseController.getParkInfo((Park) tmp.getObject());

				break;
			case "Register_Guide":
				DataBaseController.RegisterGuide(tmp);
				break;

			case "Update_Settings":
				DataBaseController.UpdateSettings(tmp);
				break;
			case "Get_Request_Update_Settings_ParkManager":
				String park_Name = (String) tmp.getObject();
				tmp = DataBaseController.GetRequestUpdateSettings(park_Name);
				break;
			case "Get_Request_Update_Settings_DepartmentManager":
				tmp = DataBaseController.GetRequestUpdateSettings("ALL");
				break;
			case "Declined_Update_Request":
				DataBaseController.ChangeStatusOnSettingRequest(tmp);
				break;
			case "Confirm_Update_Request":
				DataBaseController.ChangeStatusOnSettingRequest(tmp);
				DataBaseController.ChangeParkParameters(tmp);
				break;
			case "OrderUsingAvailableTimeTable":
				((Reservation) tmp.getObject()).setStatus("Confirmed");
				DataBaseController.createReservation(tmp);
				DataBaseController.UpdateParkVacancy_visitorsWithReservation(
						((Reservation) tmp.getObject()).getNumberOfVisitors(),
						((Reservation) tmp.getObject()).getPark_name(), ((Reservation) tmp.getObject()).getVisitDate(),
						((Reservation) tmp.getObject()).getVisitHour());
				break;
			case "RegisterToWaitingList":
				DataBaseController.createReservation(tmp);
				break;
			case "AvailableDateTable":
				tmp = new Message<ArrayList<Date>>("AvailableDateTable",
						DataBaseController.GetAvailableDate(((Reservation) tmp.getObject())));
				break;
			case "EntryWorker_GetVisitorReservation":
				reservation = DataBaseController.getReservationByNumber(Integer.parseInt((String) tmp.getObject()));
				tmp = new Message<Reservation>("EntryWorker_GetVisitorReservation", reservation);
				break;

			case "EntryWorker_EntryWithoutReservation":
				LocalDate currentDate = LocalDate.now();
				LocalTime currentTime = LocalTime.now();
				DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
				String date = currentDate.format(dateFormatter);
				DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH");
				String hour = hourFormatter.format(currentTime);

				TravelerWithoutReservation visitors = (TravelerWithoutReservation) tmp.getObject();
				if (visitors.getIsGroup().equals("1")) {// if guide check if guide register to the system
					if (!DataBaseController.checkIfGuide(visitors.getId())) {
						tmp.setMsg("Entry_Without_Reservation_NotGuide");
					}
				}
				// entry to park
				if (!tmp.getMsg().equals("Entry_Without_Reservation_NotGuide")) {
					if (DataBaseController.checkVacancy_WithoutReservation(visitors.getPark(),
							visitors.getNumberOfVisitors(), date, hour)) {// Check there is a place in park
						visitors.setStatus("InPark");
						DataBaseController.InsertToGenereteVisitingReport(visitors.getId(), visitors.getIsGroup());
						DataBaseController.updateCurrentlyVisitors(visitors.getPark(), visitors.getNumberOfVisitors());
						DataBaseController.InsertToVisitorsWithoutReservation(visitors, date);
						if (!DataBaseController.UpdateParkVacancy_visitorsWithoutReservation(
								visitors.getNumberOfVisitors(), visitors.getPark(), date, hour)) {
							DataBaseController.insertToParkVacancy(visitors.getPark(), date, hour, 0,
									visitors.getNumberOfVisitors());
						}

						tmp.setMsg("Entry_Without_Reservation_EntrySuccess");

					} else {
						tmp.setMsg("Entry_Without_Reservation_NoPlace");
					}
				}
				break;

			case "EntryWorker_RegisterWithReservation":
				reservation = DataBaseController.getReservationByNumber(Integer.parseInt((String) tmp.getObject()));
				reservation.setStatus("InPark");
				DataBaseController.UpdateReservationStatus(reservation);
				DataBaseController.InsertToGenereteVisitingReport(String.valueOf(reservation.getReservationId()),
						reservation.getIsGroup());
				DataBaseController.updateCurrentlyVisitors(reservation.getPark_name(),
						reservation.getNumberOfVisitors());
				if (!DataBaseController.UpdateParkVacancy_visitorsWithReservation(reservation.getNumberOfVisitors(),
						reservation.getPark_name(), reservation.getVisitDate(), reservation.getVisitHour())) {
					DataBaseController.insertToParkVacancy(reservation.getPark_name(), reservation.getVisitDate(),
							reservation.getVisitHour(), reservation.getNumberOfVisitors(), 0);
				}
				break;

			case "EntryWorker_ExitVisitorsWithReservation":
				reservation = (Reservation) tmp.getObject();
				reservation.setStatus("Visited");
				DataBaseController
						.UpdateExitTimeInGenereteVisitingReport(String.valueOf(reservation.getReservationId()));
				DataBaseController.updateCurrentlyVisitors(reservation.getPark_name(),
						(-1) * reservation.getNumberOfVisitors());
				DataBaseController.UpdateReservationStatus(reservation);
				break;
			case "EntryWorker_ExitVisitorsWithoutReservation":
				TravelerWithoutReservation exitVisitors = (TravelerWithoutReservation) tmp.getObject();
				exitVisitors = DataBaseController.getTravelerWithoutReservationInfo(exitVisitors);

				if (exitVisitors == null || !exitVisitors.getStatus().equals("InPark")) {
					tmp.setMsg("Visitors_Without_Reservation_Not_In_Park");
				} else if (!exitVisitors.getPark().equals(((TravelerWithoutReservation) tmp.getObject()).getPark())) {
					tmp.setMsg("Visitors_Without_Reservation_In_Another_Park");
				} else {
					DataBaseController.UpdateExitTimeInGenereteVisitingReport(exitVisitors.getId());
					DataBaseController.updateCurrentlyVisitors(exitVisitors.getPark(),
							(-1) * exitVisitors.getNumberOfVisitors());
					tmp.setMsg("Visitors_Without_Reservation_Exit_Successfully_Registered");
				}
				break;
			case "bill_Reservation":
				reservation = DataBaseController.getReservationByNumber(Integer.parseInt((String) tmp.getObject()));
				if (reservation == null) {
					tmp.setMsg("Bill_Reservation_DisplayFiled");
				} else {
					Bill bill = new Bill(reservation);
					tmp = new Message<String>("Bill_Reservation_DisplaySuccess", bill.toString());
				}
				break;

			case "Get_Total_Visitors_Report":
				TotalVisitorsReport totalReport;
				totalReport = DataBaseController.GetTotalVisitorsReport(tmp);
				if (totalReport == null) {
					totalReport = DataBaseController.CreateTotalVisitorsReport(tmp);
				}
				tmp = new Message<TotalVisitorsReport>("Get_Total_Visitors_Report", totalReport);
				break;
			case "Get_Usage_Report":
				Report info = (Report) tmp.getObject();
				ArrayList<GenereteUsageReport> Usagereport;
				Park park = DataBaseController.getParkInfo(new Park(info.getParkName(), 0, 0, 0, 0));
				Usagereport = DataBaseController.getWhenParkWasNotFull(info.getYear(), info.getMonth(), park);
				tmp = new Message<ArrayList<GenereteUsageReport>>("Get_Usage_Report", Usagereport);
				break;

			case "Get_Visiting_Report":
				ArrayList<Float> averageStayingTime = DataBaseController.GetVisitsReport(tmp);
				tmp = new Message<ArrayList<Float>>("Get_Visiting_Report", averageStayingTime);
				break;
			case "Get_Cancellation_Report":
				CancellationsReport cancellationReport = null;

				cancellationReport = DataBaseController.GetCancellationReport(tmp);

				if (cancellationReport == null) {
					cancellationReport = DataBaseController.CreateCancellationReport(tmp);
				}
				tmp = new Message<CancellationsReport>("Get_Cancellation_Report", cancellationReport);

				break;
			case "BillWithoutReservation":
				Bill bill1 = new Bill((TravelerWithoutReservation)tmp.getObject());
				tmp = new Message<Bill>("BillWithoutReservation",bill1);
				break;
			case "PriceToPay":
				Bill bill2 = new Bill((Reservation)tmp.getObject());
				tmp = new Message<Float>("PriceToPay",bill2.getPriceToPay());
				break;
			case "GetNotification":
				tmp = new Message<String>("GetNotification",DataBaseController.getNotification((String) tmp.getObject()));
				break;
			
			}

			try {
				client.sendToClient(tmp);
	
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
	/**
	 * Periodically checks for disconnected clients and removes them from the clients list.
	 */
	public void checkDisconnectedClients() {
		ArrayList<ConnectionToClient> connections = new ArrayList<ConnectionToClient>(clientsMap.keySet());
		for (ConnectionToClient c : connections) {
			synchronized (c) {
				if (c != null) {
					if (c.toString() == null) {
						clientDisconnected(c);
					}
				}
			}
		}
	}
	/**
	 * Sends a "ServerStopped" message to all connected clients before shutting down the server.
	 */
	public  void disconnectServerToUsers() {
		sendToAllClients("ServerStopped");
	}
	/**
	 * Handles the disconnection of a client, removing it from the clients list and updating server state.
	 *
	 * @param client The connection that has been disconnected.
	 */
	protected void clientDisconnected(ConnectionToClient client) {
		ConnectedClients connection = clientsMap.remove(client);
		clientsMap.remove(connection);
		serverController.refresh();
	}

}
//End of EchoServer class
