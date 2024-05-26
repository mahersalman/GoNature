package Controller;

import java.io.IOException;

import client.ChatClient;
import client.ClientUI;
import entities.Message;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * This class controls the Service Representative functionality within the JavaFX application.
 * It allows a service representative to register new park guides by entering their IDs.
 */
public class ServiceRepresintitiveController {
	@FXML
	private TextField id;
	@FXML
	private Button RegisterGuide;
	@FXML
	private Button BackTo;
	@FXML
	private Label txtLbl;


    /**
     * Handles registering a new park guide.
     * - Validates if the ID field is empty.
     * - If empty, displays an error message.
     * - If valid, creates a new `User` object with the ID and sets the user type to "guide".
     * - Sends a message to the server to register the guide.
     * - Based on the server response, displays a success or error message.
     *
     * @param event The ActionEvent.
     * @throws IOException If an error occurs during communication with the server.
     */
	@SuppressWarnings("unchecked")
	public void Register_Guide(ActionEvent event) throws IOException {
		if (id.getText().trim().isEmpty()) {
			txtLbl.setText("You must fill all fields correctly before signning up");
			txtLbl.setTextFill(Color.RED);
		} else {
			User guide = new User(id.getText(), true, false);
			Message<User> msg = new Message<>("Register_Guide", guide);
			ClientUI.chat.accept(msg);
			msg = (Message<User>) ChatClient.server_msg.get("Register_Guide");

			switch (msg.getMsg()) {
			case "Guide_Already_Register":
				txtLbl.setText("Guide " + id.getText() + " Already Exists.");
				txtLbl.setTextFill(Color.RED);
				break;
			case "Guide_Registered_Success":
				txtLbl.setText("Guide " + id.getText() + " Registered Successfully");
				txtLbl.setTextFill(Color.GREEN);
				break;
			}
		}

	}
    /**
     * Handles going back to the employee login screen.
     * Hides the current stage and opens a new stage for employee login.
     *
     * @param event The ActionEvent.
     * @throws IOException If an error occurs while loading the FXML file.
     */
	public void BackBtn(ActionEvent event) throws IOException {
    	employeeLoginController control = new employeeLoginController();
    	control.start(new Stage());
		((Node) event.getSource()).getScene().getWindow().hide();
	}
}
