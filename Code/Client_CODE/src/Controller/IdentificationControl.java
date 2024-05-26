package Controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import client.ChatClient;
import client.ClientUI;
import entities.ConnectedClients;
import entities.Message;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Node;

/**
 * This class controls the user identification process within the  application.
 * It handles user login, registration with the server, and navigation to the user home page upon successful login.
 */
public class IdentificationControl {
	@FXML
	private TextField idTxt;
	@FXML
	private Button loginBtn;
	@FXML
	private Label noteLbl;
    @FXML
    private Button backBtn;


    /**
     * Handles the "Back" button click event.
     * - Creates a new `HomePageController` instance.
     * - Launches the home page window.
     * - Hides the current identification window.
     *
     * @param event The ActionEvent object associated with the button click.
     */
    @FXML
    void back(ActionEvent event) {
    	HomePageController control = new HomePageController();
    	control.start(new Stage());
		((Node) event.getSource()).getScene().getWindow().hide();
    }

    /**
     * Launches the identification GUI window.
     * - Loads the identification GUI from the FXML file.
     * - Sets up the scene and displays the window.
     *
     * @param primaryStage The Stage object representing the main application window.
     */
	public void start(Stage primaryStage) {
		FXMLLoader loader = new FXMLLoader();
		Pane root;
		try {
			root = loader.load(getClass().getResource("/boundry/identificationGui.fxml").openStream());
			Scene scene = new Scene(root);
			primaryStage.setTitle("Identification_Gui");
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    /**
     * Handles the login button click event.
     * - Validates the entered ID (ensures it's not empty and only contains digits).
     * - Sends an identification request to the server with the user's ID.
     * - If identification is successful:
     *   - Registers the user's connection information with the server.
     *   - Retrieves the user's reservations from the server.
     *   - Displays a success message.
     *   - Navigates to the user home page.
     * - If identification fails, displays an appropriate error message.
     *
     * @param event The ActionEvent object associated with the button click.
     */
	@SuppressWarnings("unchecked")
	public void login(ActionEvent event) {
		if (idTxt.getText().trim().isEmpty() || !idTxt.getText().matches("\\d+")) {
			noteLbl.setText("Please Enter Id..");
		} else {

			ChatClient.user = new User();
			ChatClient.user.setId(idTxt.getText());
			Message<User> msg = new Message<>("Identification", ChatClient.user);
			ClientUI.chat.accept(msg);//identification
			msg = (Message<User>) ChatClient.server_msg.get("Identification");
			if (msg.getMsg().equals("Identification_Success")) {
				try {
					String role = (ChatClient.user.isGuide()) ? "Guide" : "Visitors";
					ConnectedClients client_info = new ConnectedClients(InetAddress.getLocalHost().getHostAddress(),
							InetAddress.getLocalHost().getHostName(), role,ChatClient.user.getId());
					Message<ConnectedClients> msg_connect = new Message<>("add_client",client_info);
					ClientUI.chat.accept(msg_connect);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}

				msg.setMsg("User_Reservation");
				ClientUI.chat.accept(msg);//get reservation
				noteLbl.setText("Login Success");

				UserHomePageController control = new UserHomePageController();
				control.start(new Stage());


				((Node) event.getSource()).getScene().getWindow().hide();


			} else {
				noteLbl.setText("You Already Logged In..");

			}

		}
	}
	

}
