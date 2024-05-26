package Controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.JOptionPane;

import client.ChatClient;
import client.ClientUI;
import entities.TravelerWithoutReservation;
import entities.Bill;
import entities.Employee;
import entities.Message;
import entities.Park;
import entities.Reservation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * This class is the controller for the EntryWorker It handles all actions
 * related to park entry and exit for visitors, including entry with or without
 * reservation, exit with or without reservation, displaying bills, and
 * refreshing park vacancy information.
 */
public class EntryWorkerController implements Initializable {
	/**
	 * The currently logged-in employee retrieved from the ChatClient class.
	 */
	private Employee employee = ChatClient.employee;
	@FXML
	private Button displayBillBtn1;

	@FXML
	private Button entryBtn;

	@FXML
	private Button ExitBtn;

	@FXML
	private TextField reservationID;

	@FXML
	private Label txtLbl;

	@FXML
	private TextField travelerId;

	@FXML
	private Button entryBtn1;

	@FXML
	private Button registerExitBtn1;

	@FXML
	private Button refreshParkVacancyBtn;

	@FXML
	private Label visitorInParkLbl;

	@FXML
	private Label AvailablePlacesLbl;

	@FXML
	private Label park_name;

	@FXML
	private CheckBox isGroup;

	@FXML
	private ComboBox<Integer> numberOfVisitorsLbl;
	@FXML
	private Button LogoutBtn;

    /**
     * Logs out the user and opens the HomePage scene.
     *
     * @param event The ActionEvent triggered by clicking the Logout button.
     */
	@FXML
	void Logout(ActionEvent event) {
		HomePageController control = new HomePageController();
		control.start(new Stage());
		((Node) event.getSource()).getScene().getWindow().hide();

	}


    /**
     * Handles entry for visitors with a reservation.
     *
     * @param event The ActionEvent triggered by clicking the Entry With Reservation button.
     */
	@FXML
	void EntryWithReservation(ActionEvent event) {
		String reservationNumber = reservationID.getText();
		if (reservationNumber.trim().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please Enter correct Reservation Number ", "",
					JOptionPane.ERROR_MESSAGE);
		} else {
			Message<String> msg_getVisitorReservation = new Message<>("EntryWorker_GetVisitorReservation",
					reservationNumber);
			ClientUI.chat.accept(msg_getVisitorReservation);
			Reservation VisitorReservation = (Reservation) ChatClient.server_msg
					.get("EntryWorker_GetVisitorReservation").getObject();
			if (VisitorReservation == null) {
				JOptionPane.showMessageDialog(null, "There Is No Reservation With This Number.", "",
						JOptionPane.INFORMATION_MESSAGE);

			} else if (!VisitorReservation.getStatus().equals("User_Approved")) {
				JOptionPane.showMessageDialog(null, "Reservation Not Available ", "", JOptionPane.INFORMATION_MESSAGE);
			} else {
				if (!checkReservationPark(VisitorReservation)) {
					JOptionPane.showMessageDialog(null, "Reservation Is Not Available In This Park", "",
							JOptionPane.INFORMATION_MESSAGE);
				} else if (!checkReservationDate(VisitorReservation)) {
					JOptionPane.showMessageDialog(null, "Reservation Date Not Available Now", "",
							JOptionPane.INFORMATION_MESSAGE);
				}

				else {
					Message<String> msg = new Message<>("EntryWorker_RegisterWithReservation", reservationNumber);
					ClientUI.chat.accept(msg);
					JOptionPane.showMessageDialog(null, "Entry Register Success ,NumberOfVisitors : " + "", "",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}

		}
	}
    /**
     * Checks if the reservation park matches the entry worker's park.
     *
     * @param reservation The reservation object to be checked.
     * @return True if the reservation park matches the entry worker's park, false otherwise.
     */
	private boolean checkReservationPark(Reservation reservation) {
		if (reservation.getPark_name().equals(employee.getPark())) {
			return true;
		}
		return false;
	}

    /**
     * Checks if the reservation date matches the current date and hour.
     *
     * @param reservation The reservation object to be checked.
     * @return True if the reservation date and hour match the current date and hour, false otherwise.
     */	private boolean checkReservationDate(Reservation reservation) {
		LocalDate currentDate = LocalDate.now();
		LocalTime currentTime = LocalTime.now();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		String date = currentDate.format(dateFormatter);
		DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH");
		String hour = hourFormatter.format(currentTime);

		if (reservation.getVisitDate().equals(date) && reservation.getVisitHour().equals(hour)) {
			return true;
		}
		return false;
	}

     /**
      * Handles entry for visitors without a reservation.
      *
      * @param event The ActionEvent triggered by clicking the Entry Without Reservation button.
      */
	@FXML
	void EntryWithoutReservation(ActionEvent event) {
		String id = travelerId.getText();
		int numberOfVisitors = numberOfVisitorsLbl.getSelectionModel().getSelectedItem();
		String Group = isGroup.isSelected() ? "1" : "0";

		if (id.trim().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please Enter correct ID ", "", JOptionPane.ERROR_MESSAGE);
		} else {
			TravelerWithoutReservation entry = new TravelerWithoutReservation(ChatClient.employee.getPark(), id,
					numberOfVisitors, Group, "Pending");
			Message<TravelerWithoutReservation> msg = new Message<>("EntryWorker_EntryWithoutReservation", entry);
			ClientUI.chat.accept(msg);

			String MessageFromServer = ChatClient.server_msg.get("EntryWorker_EntryWithoutReservation").getMsg();
			switch (MessageFromServer) {
			case "Entry_Without_Reservation_NotGuide":
				JOptionPane.showMessageDialog(null, "Can't be Group ,The traveler Id Not Registred as Guide..", "",
						JOptionPane.ERROR_MESSAGE);
				break;
			case "Entry_Without_Reservation_EntrySuccess":
				JOptionPane.showMessageDialog(null, "There Is Available Place , the entry register Success .", "",
						JOptionPane.INFORMATION_MESSAGE);
				break;
			case "Entry_Without_Reservation_NoPlace":
				JOptionPane.showMessageDialog(null, "No Available Place In The Park Currently", "",
						JOptionPane.ERROR_MESSAGE);
				break;
			}

		}
	}

    /**
     * Handles exit for visitors with a reservation.
     *
     * @param event The ActionEvent triggered by clicking the Exit With Reservation button.
     */
	@FXML
	void ExitWithReservation(ActionEvent event) {
		String reservationNumber = reservationID.getText();
		if (reservationNumber.trim().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please Enter correct Reservation Number ", "",
					JOptionPane.ERROR_MESSAGE);
		} else {
			Message<String> msg_getVisitorReservation = new Message<>("EntryWorker_GetVisitorReservation",
					reservationNumber);
			ClientUI.chat.accept(msg_getVisitorReservation);
			Reservation VisitorReservation = (Reservation) ChatClient.server_msg
					.get("EntryWorker_GetVisitorReservation").getObject();
			if (VisitorReservation == null) {
				JOptionPane.showMessageDialog(null, "There is No Reservation with this Number ", "",
						JOptionPane.INFORMATION_MESSAGE);
			} else if (!VisitorReservation.getStatus().equals("InPark")) {
				JOptionPane.showMessageDialog(null, "Cant make Exit , Visitors Not In Park", "",
						JOptionPane.INFORMATION_MESSAGE);
			} else if (!checkReservationPark(VisitorReservation)) {
				JOptionPane.showMessageDialog(null, "Cant make Exit , Reservation Not For This Park", "",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				Message<Reservation> msg = new Message<>("EntryWorker_ExitVisitorsWithReservation", VisitorReservation);
				ClientUI.chat.accept(msg);
				JOptionPane.showMessageDialog(null, "Register exit for Reservation Success.", "",
						JOptionPane.INFORMATION_MESSAGE);
			}

		}
	}


    /**
     * Handles exit for visitors without a reservation.
     *
     * @param event The ActionEvent triggered by clicking the Exit Without Reservation button.
     */
	@SuppressWarnings("unchecked")
	@FXML
	void ExitWithoutReservation(ActionEvent event) {
		String id = travelerId.getText();

		if (id.trim().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please Enter correct ID ", "", JOptionPane.ERROR_MESSAGE);
		} else {
			TravelerWithoutReservation exit = new TravelerWithoutReservation(ChatClient.employee.getPark(), id);
			Message<TravelerWithoutReservation> msg = new Message<>("EntryWorker_ExitVisitorsWithoutReservation", exit);
			ClientUI.chat.accept(msg);

			msg = (Message<TravelerWithoutReservation>) ChatClient.server_msg
					.get("EntryWorker_ExitVisitorsWithoutReservation");
			JOptionPane.showMessageDialog(null, msg.getMsg(), "", JOptionPane.INFORMATION_MESSAGE);
		}

	}

	   /**
     * Displays the bill for a reservation.
     *
     * @param event The ActionEvent triggered by clicking the Display Bill (Reservations) button.
     */
	@SuppressWarnings("unchecked")
	@FXML
	void displayBillReservations(ActionEvent event) {
		String reservationNumber = reservationID.getText();
		if (reservationNumber.trim().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please Enter correct Reservation Number ", "",
					JOptionPane.ERROR_MESSAGE);
		} else {
			Message<String> msg = new Message<>("bill_Reservation", reservationNumber);
			ClientUI.chat.accept(msg);
			msg = (Message<String>) ChatClient.server_msg.get("Bill_Reservation");
			if (msg.getMsg().equals("Bill_Reservation_DisplaySuccess")) {
				FXMLLoader loader = new FXMLLoader();
				try {
					Pane root = loader.load(getClass().getResource("/boundry/billDisplayToUser.fxml").openStream());
					billDisplayController control = loader.getController();
					control.setBillLbl(msg.getObject());
					Scene scene = new Scene(root);
					Stage primaryStage = new Stage();
					primaryStage.setTitle("BillDisplay Login");
					primaryStage.setScene(scene);
					primaryStage.show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (msg.getMsg().equals("Bill_Reservation_DisplayFiled")) {
				JOptionPane.showMessageDialog(null, "Reservation Number Not Exists ", "",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(null, "Something Got Wrong.. try Again", "", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

    /**
     * Displays the bill for visitors without a reservation.
     *
     * @param event The ActionEvent triggered by clicking the Display Bill (Without Reservations) button.
     */
	@FXML
	void displayBillWithoutReservations(ActionEvent event) {
		String id = travelerId.getText();
		int numberOfVisitors = numberOfVisitorsLbl.getSelectionModel().getSelectedItem();
		String Group = isGroup.isSelected() ? "1" : "0";

		if (id.trim().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please Enter correct ID ", "", JOptionPane.ERROR_MESSAGE);
		} else {
			TravelerWithoutReservation visitors = new TravelerWithoutReservation(ChatClient.employee.getPark(), id,
					numberOfVisitors, Group, "");
			Message<TravelerWithoutReservation> msg = new Message<>("BillWithoutReservation", visitors);
			ClientUI.chat.accept(msg);
			Bill bill = (Bill) ChatClient.server_msg.get("BillWithoutReservation").getObject();
			FXMLLoader loader = new FXMLLoader();
			try {
				Pane root = loader.load(getClass().getResource("/boundry/billDisplayToUser.fxml").openStream());
				billDisplayController control = loader.getController();
				control.setBillLbl(bill.toString());
				Scene scene = new Scene(root);
				Stage primaryStage = new Stage();
				primaryStage.setTitle("BillDisplay");
				primaryStage.setScene(scene);
				primaryStage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

    /**
     * Refreshes the park vacancy information on the GUI.
     *
     * @param event The ActionEvent triggered by clicking the Refresh Park Vacancy button.
     */
	@FXML
	void refreshParkVacancy(ActionEvent event) {
		Message<Park> msg = new Message<>("GetParkInfo", new Park(ChatClient.employee.getPark(), 0, 0, 0, 0));
		System.out.println("ChatClient.employee.getPark() : = >" + ChatClient.employee.getPark());
		ClientUI.chat.accept(msg);
		Park park = (Park) ChatClient.server_msg.get("GetParkInfo").getObject();
		visitorInParkLbl.setText(String.valueOf(park.getCurrentlyVisitorsInPark()));
	}

    /**
     * Handles the reservation number text field event (currently does nothing).
     *
     * @param event The ActionEvent triggered by any action on the reservation number text field.
     */
	@FXML
	void reservationNumberTxt(ActionEvent event) {

	}

    /**
     * Initializes the scene. Sets the park name label, initializes the visitor number combo box,
     * and refreshes the park vacancy labels.
     *
     * @param arg0 The URL location of the FXML file.
     * @param arg1 The ResourceBundle used to localize the scene.
     */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		park_name.setText("PARK : " + ChatClient.employee.getPark());
		try {
			List<Integer> numbers = IntStream.rangeClosed(1, 15).boxed().collect(Collectors.toList());
			numberOfVisitorsLbl.getItems().addAll(numbers);
			numberOfVisitorsLbl.getSelectionModel().select(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// refreshParkVacancy(null);

	}

}
