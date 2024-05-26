package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import entities.Message;
import entities.UpdateSettingTable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * This class controls the park settings functionality within the JavaFX
 * application. It allows park managers to view their park's current settings
 * and request updates for maximum visitors, maximum reservations, and average
 * staying time. The controller
 * 
 */
public class ParkSettingsController implements Initializable {

	@FXML
	private Label parkNameLbl;

	@FXML
	private Label noteLbl;

	@FXML
	private Label parName;

	@FXML
	private Button updateBtn;

	@FXML
	private TextField maxVisitorsTxt;

	@FXML
	private TextField MaxReservationTxt;

	@FXML
	private TextField StayingTimeTxt;

	@FXML
	private Button backBtn;

	/**
	 * Handles the "Back" button click : move to park manager home page.
	 * 
	 * @param event The ActionEvent object associated with the button click.
	 */
	@FXML
	void backBtn(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/boundry/ParkManagerHomePageGUI.fxml"));
		Parent root;
		try {
			root = loader.load();
			Scene scene = new Scene(root);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.show();
			((Node) event.getSource()).getScene().getWindow().hide();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

    /**
     * Handles the "Update" button click.
     * - Validates user-entered input for all three text fields:
     *   - Ensures all fields are filled.
     *   - Checks if values are numeric.
     *   - Verifies that maximum visitors are greater than maximum reservations.
     * - If input is valid, creates an `UpdateSettingTable` object with the new settings.
     * - Sends an update request message to the server using the `ClientUI.chat.accept` method.
     * - Adds the update request to the local `ChatClient.updateSettingTable` collection (likely for tracking).
     * - Displays a success message to the user if the request is sent successfully.
     *
     * @param event The ActionEvent object associated with the button click.
     */
	@FXML
	void updateSettings(ActionEvent event) {
		if (maxVisitorsTxt.getText().trim().isEmpty() || MaxReservationTxt.getText().trim().isEmpty()
				|| StayingTimeTxt.getText().trim().isEmpty()) {
			noteLbl.setText("Please fill all empty Texts");
			noteLbl.setTextFill(Color.RED);
		} else if (!maxVisitorsTxt.getText().matches("\\d+") || !MaxReservationTxt.getText().matches("\\d+")
				|| !StayingTimeTxt.getText().matches("\\d+")) {
			noteLbl.setText("Wrong Input , Try Again..");
			noteLbl.setTextFill(Color.RED);

		} else if (Integer.parseInt(maxVisitorsTxt.getText()) < Integer.parseInt(MaxReservationTxt.getText())) {
			noteLbl.setText("Maximum Visitros Must be bigger than Maximum Reservations..");
			noteLbl.setTextFill(Color.RED);
		} else {
			UpdateSettingTable newSetting = new UpdateSettingTable(maxVisitorsTxt.getText(),
					MaxReservationTxt.getText(), StayingTimeTxt.getText(), "Pending", ChatClient.employee.getPark());
			Message<UpdateSettingTable> msg = new Message<>("Update_Settings", newSetting);
			ClientUI.chat.accept(msg);
			ChatClient.updateSettingTable.add(newSetting);
			noteLbl.setText("Change Requset send successfully to Department Manager ");
			noteLbl.setTextFill(Color.GREEN);
		}

	}

    /**
     * Launches the park settings GUI window.
     * - Loads the parkSettings.fxml file and sets up the scene.
     *
     * @param stage The Stage object to display the window.
     * @throws IOException If an error occurs while loading the FXML file.
     */
	public void start(Stage stage) throws IOException {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/boundry/parkSettings.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			stage.setTitle("Server");
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

    /**
     * Initializes the controller after FXML loading.
     * - Sets the park name label with the logged-in employee's park name retrieved from `ChatClient.employee.getPark()`.
     *
     * @param arg0 The URL (ignored in this case).
     * @param arg1 The ResourceBundle (ignored in this case).
     */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		parName.setText("Park : " + ChatClient.employee.getPark());
	}

}
