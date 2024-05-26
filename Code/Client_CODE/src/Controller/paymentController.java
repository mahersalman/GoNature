package Controller;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.ResourceBundle;


import client.ChatClient;
import client.ClientUI;
import entities.Message;
import entities.Reservation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


/**
 * This class controls the payment functionality within the JavaFX application.
 * It allows users to enter their payment information (card name, visa number, CVV, expiry date)
 * for a selected reservation. The controller validates the entered data and sends a payment confirmation
 * message to the server. It displays success or error messages based on the server response.
 * It also allows users to navigate back to the user home page.
 */
public class paymentController implements Initializable {

	private Reservation reservation;
	@FXML
	private Label reservationNumberLbl;
	
	@FXML
	private TextField nameOnCard;
	@FXML
	private TextField visaNumber;

	@FXML
	private TextField cvvTxt;

	@FXML
	private ComboBox<String> yearBox;

	@FXML
	private ComboBox<String> monthBox;

	@FXML
	private Label priceLbl;
	
    @FXML
    private Label noteLbl;

	@FXML
	private Button payBtn;
    @FXML
    private Button backBtn;

    /*price to pay as foat */
    private float priceToPay;
    /**
     * Handles the "Back" button click.
     * - Creates an instance of the `UserHomePageController`.
     * - Starts the user home page in a new window using the controller's `start` method.
     * - Hides the current payment window.
     *
     * @param event The ActionEvent.
     */
    @FXML
    void back(ActionEvent event) {
    	UserHomePageController controller = new UserHomePageController();
    	controller.start(new Stage());
		((Node) event.getSource()).getScene().getWindow().hide();

    }
    /**
     * Handles the "Pay" button click.
     * - Validates user-entered payment information (name, visa number, CVV).
     * - If valid, sends a payment confirmation message to the server with the reservation object.
     *   - Displays a success message upon receiving a successful response from the server.
     * - If invalid, displays an error message.
     *
     * @param event The ActionEvent.
     */
	@FXML
	void payment(ActionEvent event) {
		if ((nameOnCard.getText() != null)&&isValidVisaNumber(visaNumber.getText())&&isValidCVV(cvvTxt.getText())) 
		{
			Message<Reservation> msg = new Message<>("payment",reservation);
			ClientUI.chat.accept(msg);
			noteLbl.setText("Payment Success");
			noteLbl.setTextFill(Color.GREEN);
		}
		else 
		{
			noteLbl.setText("Invalid Data..Try Again");
			noteLbl.setTextFill(Color.RED);

		}
		
	}

    /**
     * Loads reservation information into the payment control(this) and  gui.
     * - Sets the reservation number label with the selected reservation's ID.
     * - Sends a message to the server requesting the price for the selected reservation.
     * - Updates the price label with the received price from the server.
     *
     * @param selectedReservation The selected reservation object.
     */
	public void loadInfo(Reservation selectedReservation) {
		this.reservation = selectedReservation;
	    reservationNumberLbl.setText(String.valueOf(reservation.getReservationId()));
	    Message<Reservation> msg = new Message<>("PriceToPay", selectedReservation);
		ClientUI.chat.accept(msg);
	    priceToPay =(Float)(ChatClient.server_msg.get("PriceToPay").getObject());
		priceLbl.setText(String.valueOf(priceToPay));

	}
	   /**
     * Initializes the FXML components.
     * - Populates the year and month combo boxes with predefined values.
     * - Handles any exceptions that may occur during initialization.
     *
     * @param location The URL of the FXML file.
     * @param resources The ResourceBundle used to localize the root object.
     */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		  try {
	        	Collection<String> date_year = Arrays.asList("2024", "2025", "2026","2027","2028","2029");
	        	yearBox.getItems().addAll(date_year);
	        	Collection<String> date_month = Arrays.asList("1", "2", "3","4","5","6","7","8","9","10","11","12");
	        	monthBox.getItems().addAll(date_month);
	        	
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	}
	   /**
     * Validates the entered Visa number format (16 digits, numeric only).
     *
     * @param number The Visa number string to validate.
     * @return True if the number is valid, False otherwise.
     */
	public static boolean isValidVisaNumber(String number) {
		return number.matches("\\d+") && number.length()== 16;

    }
	   /**
     * Validates the entered CVV format (3 digits, numeric only).
     *
     * @param cvv The CVV string to validate.
     * @return True if the CVV is valid, False otherwise.
     */
	 public static boolean isValidCVV(String cvv) {
			return cvv.matches("\\d+") && cvv.length()== 3;

	    }
}
