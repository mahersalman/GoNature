package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import client.ChatClient;
import client.ClientUI;
import entities.Date;
import entities.Message;
import entities.Reservation;
import javafx.collections.FXCollections;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * This class controls the "Unavailable Order Option" functionality within the JavaFX application.
 * It handles scenarios where a user tries to create a reservation for a date/time that is already
 * fully booked. It provides the user with two options:
 *  1. Enter Waiting List: Add the reservation to a waiting list for the chosen park and date.
 *  2. Choose Available Date: Show a table of available dates/times for the chosen park.
 */
public class UnavailableOrderOption implements Initializable {

	@FXML
	private Button EnterWaitingListBtn;

	@FXML
	private Button ShowAvailableDateTableBtn;

	@FXML
	private Button ExitOrderOptionsBtn;

	@FXML
	private Button BackBtn;

	@FXML
	private TableView<Date> DateTable;

	@FXML
	private TableColumn<Date, String> DateCol;

	@FXML
	private TableColumn<Date, String> hourCol;

	@FXML
	private Pane PaneOptions;

	@FXML
	private Pane paneLabel;

	@FXML
	private Label NoteLbl;

    /**
     *This method  used to launch the unavailable order options
     *
     * @param primaryStage The Stage on which to display the unavailable order options GUI.
     * @throws IOException If an error occurs while loading the FXML file.
     */
	public void start(Stage primaryStage) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("/boundry/UnavailableReservationOptions.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setTitle("UnavailableReservationOptions");
		primaryStage.setScene(scene);
		primaryStage.show();

	}
    /**
     * Handles going back to the main reservation creation screen.
     * Hides the current stage and opens a new stage for reservation creation.
     *
     * @param event The ActionEvent.
     */
	@FXML
	void back(ActionEvent event) {
		ReservationController control = new ReservationController();
		control.start(new Stage());
		((Node) event.getSource()).getScene().getWindow().hide();
	}


    /**
     * Handles selecting an available date/time from the table.
     * - Validates if a date is selected.
     * - If valid, updates the new reservation with the chosen date/time,
     * sends a message to the server to create the reservation,
     * hides the options pane and displays a success message with reservation details.
     * - If no date is selected, displays an error message.
     *
     * @param event The ActionEvent.
     */
	@FXML
	void chooseAvailableDate(ActionEvent event) {
		Date SelectedDate = DateTable.getSelectionModel().getSelectedItem();
		if (SelectedDate != null) {
			ReservationController.newReservation.setVisitDate(SelectedDate.getDate());
			ReservationController.newReservation.setVisitHour(SelectedDate.getHour());
			System.out.println("date : "+ReservationController.newReservation.getVisitDate() );
			System.out.println("hour : "+ReservationController.newReservation.getVisitHour() );

			Message<Reservation> msg = new Message<>("OrderUsingAvailableTimeTable",
					ReservationController.newReservation);
			ClientUI.chat.accept(msg);
			PaneOptions.setVisible(false);
			paneLabel.setVisible(true);
			NoteLbl.setText("Reservation Created Successfuly \nReservation Detail : \n-Reservation Number :"
					+ ReservationController.newReservation.getReservationId() + "\n-"
					+ ReservationController.newReservation.getPark_name() + "\n-On "
					+ ReservationController.newReservation.getVisitDate() + " - "
					+ ReservationController.newReservation.getVisitHour() + ":00\n"
					+ ReservationController.newReservation.getNumberOfVisitors() + " Visitors");
		}
		else {
			JOptionPane.showMessageDialog(null, "Please select a Date First.. ", "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

    /**
     * Handles entering the waiting list for the chosen park and date.
     * Updates the new reservation's status to "InWaitList",
     * sends a message to the server to register the reservation in the waiting list,
     * hides the options pane and displays a success message with the reservation ID.
     *
     * @param event The ActionEvent.
     */
	@FXML
	void enterWaitList(ActionEvent event) {
		ReservationController.newReservation.setStatus("InWaitList");
		Message<Reservation> msg = new Message<>("RegisterToWaitingList", ReservationController.newReservation);
		ClientUI.chat.accept(msg);
		PaneOptions.setVisible(false);
		paneLabel.setVisible(true);
		NoteLbl.setText("Rigster To waitingList Success \n Your Reservation Number : "
				+ ReservationController.newReservation.getReservationId());

	}

    /**
     * Initializes the controller. Hides the success message pane initially,
     * fetches available dates from the server for the chosen park and date,
     * sets up table cell value factories for date and hour,
     * and populates the table with the received available dates.
     *
     * @param arg0 The URL.
     * @param arg1 The resource bundle.
     */
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		paneLabel.setVisible(false);

		// initilize waiting List
		Message<Reservation> msg = new Message<>("AvailableDateTable", ReservationController.newReservation);
		ClientUI.chat.accept(msg);
		DateCol.setCellValueFactory(new PropertyValueFactory<Date, String>("date"));
		hourCol.setCellValueFactory(new PropertyValueFactory<Date, String>("hour"));

		ArrayList<Date> availableDate = (ArrayList<Date>) ChatClient.server_msg.get("AvailableDateTable").getObject();
		DateTable.setItems(FXCollections.observableArrayList(availableDate));
	}

}
