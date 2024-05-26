package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import client.ChatClient;
import client.ClientUI;
import entities.Message;
import entities.Reservation;
import entities.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * This class controls the User Home Page functionality within the JavaFX
 * application. It handles displaying user information, managing reservations
 * (refreshing, creating, paying for group reservations, approving/canceling),
 * displaying notifications, and logging out.
 */
public class UserHomePageController implements Initializable {

	@FXML
	private Button paymentBtn;
	@FXML
	private Button create_reservationBtn;
	@FXML
	private Button decisionBtn;
	@FXML
	private Button logoutBtn;
	@FXML
	private Label pageLbl;
	@FXML
	private TableView<Reservation> reservationsTable;
	@FXML
	private TableColumn<Reservation, String> reservationId;
	@FXML
	private TableColumn<Reservation, String> visitDate;
	@FXML
	private TableColumn<Reservation, String> visitHour;
	@FXML
	private TableColumn<Reservation, String> numberOfVisitors;
	@FXML
	private TableColumn<Reservation, String> park_name;
	@FXML
	private TableColumn<Reservation, String> email;
	@FXML
	private TableColumn<Reservation, String> phoneNumber;
	@FXML
	private TableColumn<Reservation, String> isGroup;
	@FXML
	private TableColumn<Reservation, String> status;
	@FXML
	private TextArea notificationArea;
	@FXML
	private Button RefreshNotificationBtn;
	@FXML
	private Button RefreshReservation;

	/**
	 * Refreshes the notification area by sending a "GetNotification" message to the
	 * server and displaying the received notification string.
	 *
	 * @param event The ActionEvent (can be null).
	 */
	@FXML
	void RefreshNotification(ActionEvent event) {
		Message<String> msg = new Message<>("GetNotification", ChatClient.user.getId());
		ClientUI.chat.accept(msg);
		msg = (Message<String>) ChatClient.server_msg.get("GetNotification");
		notificationArea.setText(msg.getObject());
	}

	/**
	 * This method used to launch the user home page *
	 * 
	 * @param primaryStage
	 * @throws IOException If an error occurs while loading the FXML file.
	 */
	public void start(Stage primaryStage) {
		FXMLLoader loader = new FXMLLoader();
		Pane root;
		try {
			root = loader.load(getClass().getResource("/boundry/UserHomePage.fxml").openStream());
			Scene scene = new Scene(root);
			primaryStage.setTitle("USER HOME PAGE ");
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Opens a new stage to create a new reservation.
	 *
	 * @param event The ActionEvent.
	 */
	@FXML
	public void create_reservation(ActionEvent event) {
		ReservationController control = new ReservationController();
		control.start(new Stage());
		((Node) event.getSource()).getScene().getWindow().hide();

	}

	/**
	 * Handles payment for a selected group reservation. - Validates if a
	 * reservation is selected. - Validates if the selected reservation is a group
	 * reservation. - Opens a new stage for payment if valid, displaying an error
	 * message otherwise.
	 *
	 * @param event The ActionEvent.
	 */
	@FXML
	void payment(ActionEvent event) {
		Reservation selectedReservation = reservationsTable.getSelectionModel().getSelectedItem();
		if (selectedReservation != null) {
			if (selectedReservation.getIsGroup().equals("1")) {
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/boundry/paymentGUI.fxml"));
					Parent root = loader.load();
					Scene scene = new Scene(root);
					Stage stage = new Stage();
					paymentController control = loader.getController();
					control.loadInfo(selectedReservation);
					stage.setScene(scene);
					stage.setTitle("Payment");
					stage.show();
					((Node) event.getSource()).getScene().getWindow().hide();

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				JOptionPane.showMessageDialog(null, "Thats Not a group reservation , cant make a prepayment !!", "",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Please select a reservation .", "", JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * Handles approving or canceling a selected reservation. - Validates if a
	 * reservation is selected. - Opens a new stage for approval/cancellation if
	 * valid, displaying an error message otherwise.
	 *
	 * @param event The ActionEvent.
	 */
	@FXML
	public void decision(ActionEvent event) {
		Reservation SelectedReservation = reservationsTable.getSelectionModel().getSelectedItem();
		if (SelectedReservation != null) {
			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("/boundry/approveOrCancelReservation.fxml"));
				Parent root;
				root = loader.load();
				approveOrCancelReservationControl controller = loader.getController();
				controller.setReservation(SelectedReservation);
				Scene scene = new Scene(root);
				Stage stage = new Stage();
				stage.setScene(scene);
				stage.setTitle("Cancel");
				stage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			JOptionPane.showMessageDialog(null, "Please select a reservation To cancel it .", "",
					JOptionPane.ERROR_MESSAGE);

		}

	}

	/**
	 * Logs the user out by setting the logged in flag to false, sending a
	 * "User_Logout" message to the server, hiding the current stage, opening the
	 * home page, and setting the logged in user to null.
	 *
	 * @param event The ActionEvent.
	 */
	@FXML
	void logout(ActionEvent event) {
		ChatClient.user.setLogged(false);
		Message<User> msg = new Message<>("User_Logout", ChatClient.user);
		ClientUI.chat.accept(msg);
		((Node) event.getSource()).getScene().getWindow().hide();
		HomePageController homePageControl = new HomePageController();
		homePageControl.start(new Stage());
		ChatClient.user = null;
	}

	/**
	 * Initializes the controller. Sets the welcome message with user ID, populates
	 * the reservation table with data from `ChatClient.User_reservation`, sets up
	 * table cell value factories, refreshes the notification area, and sets a
	 * refresh listener for the reservation table.
	 *
	 * @param location  The URL.
	 * @param resources The resource bundle.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		pageLbl.setText("Welcome To Go Nature \nID : " + ChatClient.user.getId());
		reservationId.setCellValueFactory(new PropertyValueFactory<Reservation, String>("reservationId"));
		visitDate.setCellValueFactory(new PropertyValueFactory<Reservation, String>("visitDate"));
		visitHour.setCellValueFactory(new PropertyValueFactory<Reservation, String>("visitHour"));
		numberOfVisitors.setCellValueFactory(new PropertyValueFactory<Reservation, String>("numberOfVisitors"));
		park_name.setCellValueFactory(new PropertyValueFactory<Reservation, String>("park_name"));
		email.setCellValueFactory(new PropertyValueFactory<Reservation, String>("email"));
		phoneNumber.setCellValueFactory(new PropertyValueFactory<Reservation, String>("phoneNumber"));
		isGroup.setCellValueFactory(new PropertyValueFactory<Reservation, String>("IsGroup"));
		status.setCellValueFactory(new PropertyValueFactory<Reservation, String>("status"));
		reservationsTable.setItems(ChatClient.User_reservation);
		RefreshNotification(null);
	}

	/**
	 * Refreshes the reservation table by setting the items to
	 * `ChatClient.User_reservation` and calling the table's refresh method.
	 */
	/* referesh table-view */
	public void refresh() {
		reservationsTable.setItems(ChatClient.User_reservation);
		reservationsTable.refresh();
	}

	/**
	 * Appends a new message to the notification area.
	 *
	 * @param sms The message to be appended.
	 */
	public void writeToNotifiction(String sms) {
		notificationArea.appendText("# " + sms + "\n");
	}

	/**
	 * Refreshes the reservation table by sending a "User_Reservation" message to
	 * the server and then calling the `refresh` method.
	 *
	 * @param event The ActionEvent (can be null).
	 */
	@FXML
	void RefreshReservation(ActionEvent event) {
		Message<User> msg = new Message<>("User_Reservation", ChatClient.user);
		msg.setMsg("User_Reservation");
		ClientUI.chat.accept(msg);
		refresh();
	}
}
