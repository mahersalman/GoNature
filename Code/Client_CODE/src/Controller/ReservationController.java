package Controller;

import javafx.scene.control.Button;
import java.io.IOException;

import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
//import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import client.ChatClient;
import client.ClientUI;
import entities.Message;
import entities.Reservation;
import entities.User;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
//import javafx.stage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
//import entities.*;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.InetAddress;

/**
 * This class controls the reservation creation functionality within the JavaFX application.
 * It allows users to create new reservations by selecting a park, date, time, number of visitors,
 * and optionally requesting a guide. The controller validates user input, sends reservation data
 * to the server, and displays confirmation or error messages based on the server response.
 */
public class ReservationController implements Initializable {
	public static Reservation newReservation;
	@FXML
	private TextField PhoneNumberText;
	@FXML
	private TextField EmailTextFiled;
	@FXML
	private CheckBox guideCheckbox;
	@FXML
	private ComboBox<Integer> Num_of_vistirs_comboBox;
	@FXML
	private DatePicker reservationDatePicker;
	@FXML
	private ComboBox<String> Park_name_combobox;
	@FXML
	private Button CreateOrderBtn;
	@FXML
	private Button BackBtn;
	@FXML
	private Button exitBtn;
	@FXML
	private ComboBox<String> PickTimeHours;
	@FXML
	private Label noteLbl;

	/**
     * initialize comboBoxs For gui and checking if user is guide or not 
     *
     */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		configureDatePicker();
		setupParkComboBox();
		setupNumOfVisitorsComboBox();
		setupTimeSelectionComboBoxes();
		if (!ChatClient.user.isGuide()) {
			guideCheckbox.setDisable(true);
			guideCheckbox.setText("You Are Not A Guide");
		}
	}

	/**
     * Launches the reservation creation GUI window.
     *
     * @param stage The Stage on which to display the GUI.
     * @throws IOException If an error occurs while loading the FXML file.
     */
	public void start(Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/boundry/ReseravtionGui.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Create New Reservation");
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    /**
     * Configures the date picker to only allow selecting future dates.
     */
	private void configureDatePicker() {
		reservationDatePicker.setDayCellFactory(picker -> new DateCell() {
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();
				setDisable(empty || date.compareTo(today) < 0);
			}
		});
	}
    /**
     * Populates the park name combo box with a predefined list of parks.
     */
	private void setupParkComboBox() {
		try {
			Collection<String> parksToAdd = Arrays.asList("Banias Nature", "Gan HaShlosha", "Ein Gedi");
			Park_name_combobox.getItems().addAll(parksToAdd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    /**
     * Populates the number of visitors combo box with a list of integers from 1 to 15.
     */
	private void setupNumOfVisitorsComboBox() {
		try {
			// Create a list of integers from 1 to 15
			List<Integer> numbers = IntStream.rangeClosed(1, 15).boxed().collect(Collectors.toList());

			// Add the list of integers to the combo box
			Num_of_vistirs_comboBox.getItems().addAll(numbers);

			// Set the default selected value to 1 (assuming 0-based indexing)
			Num_of_vistirs_comboBox.getSelectionModel().select(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    /**
     * Populates the time selection combo box with available hours (8 AM to 3 PM).
     */
	private void setupTimeSelectionComboBoxes() {
		try {
			IntStream.rangeClosed(8, 15).forEach(i -> PickTimeHours.getItems().addAll(Integer.toString(i)));
		} catch (Exception e) {
			// Handle the exception
		}
	}
    /**
     * Validates the phone number format (10 digits).
     *
     * @param phone The phone number to validate.
     * @return True if the phone number is valid, false otherwise.
     */
	private boolean checkPhoneNumber(String phone) {
		return Pattern.matches("^\\d{10}$", phone);
	}

    /**
     * Validates the email format using a regular expression.
     *
     * @param email The email address to validate.
     * @return True if the email is valid, false otherwise.
     */
	private boolean isValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
				+ "A-Z]{2,7}$";
		Pattern pat = Pattern.compile(emailRegex);
		if (email == null)
			return false;
		return pat.matcher(email).matches();
	}

    /**
     * Validates all user input fields before creating a reservation.
     *
     * @return True if all input is valid, false otherwise.
     */
	private boolean validateReservationInputs() {
		if (!isValidEmail(EmailTextFiled.getText()) || !checkPhoneNumber(PhoneNumberText.getText()))
			return false;
		return true;
	}

    /**
     * Handles the "Create Order" button click.
     * - Gathers user input from various controls.
     * - Validates user input.
     * - Creates a new `Reservation` object with the user's selections.
     * - Sends a message to the server to create the reservation.
     * - Displays a confirmation message or error message based on the server response.
     *   - In case of a successful reservation, disables the "Create Order" button
     *     to prevent duplicate reservations.
     *   - In case of an unsuccessful reservation (due to park being fully booked),
     *     opens the "Unavailable Order Option" window to provide alternative options.
     *
     * @param event The ActionEvent.
     * @throws IOException If an error occurs while communicating with the server.
     */
	@SuppressWarnings({ "unchecked", "static-access" })
	@FXML
	void actionOrderBtn(ActionEvent event) throws IOException {
		LocalDate selectedDate;
		String Date, hour, park, isGuide;
		int numberOfVisitors;
		try {
			selectedDate = reservationDatePicker.getValue();
			Date = selectedDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
			hour = PickTimeHours.getSelectionModel().getSelectedItem().toString();
			numberOfVisitors = Num_of_vistirs_comboBox.getSelectionModel().getSelectedItem();
			park = Park_name_combobox.getSelectionModel().getSelectedItem().toString();
			isGuide = guideCheckbox.isSelected() ? "1" : "0";
		} catch (Exception e) {
			noteLbl.setText("Missing Data");
			return;
		}
		if (!validateReservationInputs() || Date == null || hour == null || park == null) {
			noteLbl.setText("Invalid Data..Try Again");
		} else {

			this.newReservation = new Reservation(0, ChatClient.user.getId(), Date, hour, numberOfVisitors, park,
					EmailTextFiled.getText(), PhoneNumberText.getText(), isGuide, "-", "0");

			Message<Reservation> msg = new Message<>("Create_Reservation", newReservation);
			ClientUI.chat.accept(msg);
			msg = (Message<Reservation>) ChatClient.server_msg.get("Create_Reservation");
			if (msg.getMsg().equals("Create_Reservation_Success")) {
				noteLbl.setText("Your Reservation Has Confirmed.");
				noteLbl.setTextFill(Color.GREEN);
				ChatClient.User_reservation.add(msg.getObject());
				CreateOrderBtn.setDisable(true);
			} else {
				noteLbl.setText("Your Reservation Has Declined.");
				UnavailableOrderOption control = new UnavailableOrderOption();
				control.start(new Stage());
				((Node) event.getSource()).getScene().getWindow().hide();

			}

		}

	}

	@FXML
	void Back(ActionEvent event) {
		UserHomePageController control = new UserHomePageController();
		control.start(new Stage());
		((Node) event.getSource()).getScene().getWindow().hide();
	}

}