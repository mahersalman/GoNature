package controller;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import otherSystem.MinistryOfTourism;
import otherSystem.UserManagementSystem;
import Server.EchoServer;
import Server.ServerUI;
import database.DataBaseController;
import database.Jdbc;
import entities.ConnectedClients;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

/**
 * The ServerController class manages the server application's interaction with
 * the user interface, database, and background tasks.
 */
public class ServerController implements Initializable {
	/**
	 * Label displaying the server's IP address.
	 */
	@FXML
	private Label IpAddressLbl;

	/**
	 * Label displaying the server connection status (connected/disconnected).
	 */
	@FXML
	private Label statusLbl;

	/**
	 * Circle representing the server connection status visually (green for
	 * connected, red for disconnected).
	 */
	@FXML
	private Circle cycleStatus;

	/**
	 * Text field for entering the server port number.
	 */
	@FXML
	private TextField portTxt;

	/**
	 * Text field for entering the SQL username for database connection.
	 */
	@FXML
	private TextField SqlUsernameTxt;

	/**
	 * Text field for entering the SQL password for database connection.
	 */
	@FXML
	private TextField SqlPasswordTxt;

	/**
	 * Button to initiate server connection.
	 */
	@FXML
	private Button connectBtn;

	/**
	 * Button to disconnect the server.
	 */
	@FXML
	private Button disconnectBtn;

	/**
	 * Text area displaying server logs.
	 */
	@FXML
	private TextArea logArea;

	/**
	 * Table view displaying information about connected clients.
	 */
	@FXML
	private TableView<ConnectedClients> ConnectedClientTableView;

	/**
	 * Table column displaying the IP address of connected clients.
	 */
	@FXML
	private TableColumn<ConnectedClients, String> IpColumn;

	/**
	 * Table column displaying the hostname of connected clients.
	 */
	@FXML
	private TableColumn<ConnectedClients, String> hostColumn;

	/**
	 * Table column displaying the role of connected clients.
	 */
	@FXML
	private TableColumn<ConnectedClients, String> roleColumn;

	// Threads
	private Thread waitListChecks;
	private Thread NotVisitedReservation;
	private Thread oneDayBeforeReservationNotification;
	private Thread unconfirmedReservation;

	/**
	 * This method loads the FXML file for the server user interface and sets the
	 * controller for the EchoServer class.
	 * 
	 * @param primaryStage The primary stage of the application.
	 * @throws Exception If an error occurs during FXML loading.
	 */
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/boundry/ServerUi.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		primaryStage.setTitle("Server");
		primaryStage.setScene(scene);
		primaryStage.show();
		EchoServer.serverController = loader.getController();
	}

	/**
	 * This method attempts to connect to the database using the provided
	 * credentials, starts the server on the specified port. also the method
	 * simulate the getter infomration from userManagement system using
	 * dataBaseController , the same for full price, at end the function run server
	 * threads.
	 * 
	 * @param event The ActionEvent triggered by clicking the connect button.
	 * @throws Exception If an error occurs during database connection, server
	 *                   startup, or thread creation.
	 */
	public void connect(ActionEvent event) throws Exception {
		if (portTxt.getText().trim().isEmpty()) {
			logArea.appendText("You must enter a port number !! ");
		} else {
			if (Jdbc.connectionToDB(SqlUsernameTxt.getText(), SqlPasswordTxt.getText())) {
				if (ServerUI.runServer(portTxt.getText())) {

					UserManagementSystem.EmployeesGetter();

					MinistryOfTourism.getFullPriceFromMinistryOfTourism();
					connectBtn.setDisable(true);
					statusLbl.setText("Connected");
					disconnectBtn.setDisable(false);
					cycleStatus.setFill(Color.GREEN);

					oneDayBeforeReservationNotification = new Thread(new Runnable() {

						@Override
						public void run() {

							while (true) {
								DataBaseController.notifySMSUserOneDayBefore();
								try {

									Thread.sleep(1000);// sleep for 1 seconds
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					});
					/* Unconfirmed order */
					unconfirmedReservation = new Thread(new Runnable() {
						@Override
						public void run() {
							while (true) {
								DataBaseController.CancelOrderUserTwoHoursAfter();

								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					});

					/* WaitListCheck Thread */
					waitListChecks = new Thread(new Runnable() {
						@Override
						public void run() {
							while (true) {
								DataBaseController.GivePlaceToWaitingList();
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					});

					NotVisitedReservation = new Thread(new Runnable() {
						@Override
						public void run() {

							while (true) {
								DataBaseController.UpdateStatusNotVisitedReservation();
								try {

									Thread.sleep(1000);// sleep for 1 seconds
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					});
					NotVisitedReservation.start();

					waitListChecks.start();
					oneDayBeforeReservationNotification.start();
					unconfirmedReservation.start();
				}
			} else {
				writeToLog("Wrong SQL Password , Try Again");
			}
		}
	}

	/**
	 * This method disconnects the server, stops background threads, clears
	 * connected clients, and updates the GUI to reflect the disconnected state.
	 * 
	 * @param event The ActionEvent triggered by clicking the disconnect button.
	 * @throws Exception If an error occurs during server shutdown.
	 */
	public void disconnect(ActionEvent event) throws Exception {
		ServerUI.sv.disconnectServerToUsers();
		ServerUI.closeServer();
		EchoServer.clients.clear();
		waitListChecks.stop();
		NotVisitedReservation.stop();
		oneDayBeforeReservationNotification.stop();
		unconfirmedReservation.stop();
		connectBtn.setDisable(false);
		statusLbl.setText("disconnected");
		disconnectBtn.setDisable(true);
		cycleStatus.setFill(Color.RED);
		writeToLog("Server Stopped");

	}

	/**
	 * This method is intended to run in a background thread to periodically check
	 * for disconnected clients from the server.
	 */
	public synchronized void checkClients() {
		ServerUI.sv.checkDisconnectedClients();

	}

	/**
	 * referesh ConnectedClient table-view
	 */
	public void refresh() {
		ConnectedClientTableView.setItems(FXCollections.observableArrayList(EchoServer.clients));
		ConnectedClientTableView.refresh();
	}

	/**
	 * method to add connections to the connected users tableview
	 */
	public synchronized void addConnection() {
		refresh();
	}

	/**
	 * method checks if users have disconnected from the server and updates the gui
	 * accordingly
	 */
	public synchronized void removeConnection() {
		ServerUI.sv.checkDisconnectedClients();
	}

	/**
	 * function to inilize the server ip address to the gui and to initilize the
	 * table view
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		String ipAddress;
		try {
			ipAddress = InetAddress.getLocalHost().getHostAddress();
			IpAddressLbl.setText(ipAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(0);
		}

		IpColumn.setCellValueFactory(new PropertyValueFactory<ConnectedClients, String>("ip"));
		hostColumn.setCellValueFactory(new PropertyValueFactory<ConnectedClients, String>("hostName"));
		roleColumn.setCellValueFactory(new PropertyValueFactory<ConnectedClients, String>("role"));
		ConnectedClientTableView.setItems(FXCollections.observableArrayList(EchoServer.clients));

		disconnectBtn.setDisable(true);

	}

	/**
	 * write string to log
	 */
	public void writeToLog(String str) {
		logArea.appendText(str + "\n");

	}

}
