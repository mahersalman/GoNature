package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import client.ClientController;
import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * This class controls the application's home page functionality.
 * It allows users to:
 * - Enter the server's IP address and port number.
 * - Connect to the server.
 * - Choose between logging in as a traveler or employee upon successful connection.
 */
public class HomePageController implements Initializable  {
    @FXML
    private Button TravelerBtn;

    @FXML
    private Button empolyeeBtn;

    @FXML
    private TextField portTxt;

    @FXML
    private Button connectBtn;

    @FXML
    private TextField ipTxt;

    @FXML
    private Label NoteLbl;

    @FXML
    private AnchorPane userPane;
    
    /**
     * Handles the "Connect" button click event.
     * - Validates the entered IP address (ensures it's not empty).
     * - Attempts to connect to the server using the provided IP and port number.
     * - If connection is successful:
     *   - Creates a new `ClientController` object to handle communication with the server.
     *   - Displays a success message.
     *   - Disables the connect button to prevent reconnection attempts.
     *   - Shows the traveler/employee login buttons.
     * - If connection fails, displays an appropriate error message.
     *
     * @param event The ActionEvent object associated with the button click.
     */
    @FXML
    void connect(ActionEvent event) {
    	int port = Integer.parseInt(portTxt.getText());
		String ip = ipTxt.getText();
		if (ip.trim().isEmpty()) {
			NoteLbl.setText("Please Chosse Correct Server IpAddress/port !");
		} else {
			try {
				ClientUI.chat = new ClientController(ipTxt.getText(), port);
				NoteLbl.setTextFill(Color.GREEN);
				NoteLbl.setText("Connected Successfully .");
				connectBtn.setDisable(true);
				userPane.setVisible(true);

			} catch (IOException e) {
				if (e.getMessage().equals("ConnectionFailed")) {
					NoteLbl.setText("Connection Failed");
				} else {
					e.printStackTrace();
					System.exit(0);
				}
			}
		}
    }
    

    /**
     * Handles the "Employee Login" button click event.
     * - Creates a new `employeeLoginController` instance.
     * - Launches the employee login window.
     * - Hides the current home page window.
     *
     * @param event The ActionEvent object associated with the button click.
     */
    @FXML
    void openEmployeeLoginGUI(ActionEvent event) {
    	employeeLoginController control = new employeeLoginController();
		control.start(new Stage());
		((Node) event.getSource()).getScene().getWindow().hide();

    }
    
    /**
     * Handles the "Traveler Login" button click event.
     * - Creates a new `IdentificationControl` instance.
     * - Launches the traveler login window.
     * - Hides the current home page window.
     *
     * @param event The ActionEvent object associated with the button click.
     */
    @FXML
    void openTravelerLoginGUI(ActionEvent event) {
		IdentificationControl control = new IdentificationControl();
		control.start(new Stage());
		((Node) event.getSource()).getScene().getWindow().hide();

    }

    /**
     * Launches the home page GUI window.
     * - Loads the home page GUI from the FXML file.
     * - Sets up the scene and displays the window.
     *
     * @param primaryStage The Stage object representing the main application window.
     */
	public void start(Stage primaryStage) {
		FXMLLoader loader = new FXMLLoader();
		Pane root;
		try {
			root = loader.load(getClass().getResource("/boundry/HomePageGUI.fxml").openStream());
			Scene scene = new Scene(root);
			primaryStage.setTitle("home_page");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

    /**
     * - Hides the traveler/employee login buttons initially.
     *
     * @param arg0 The URL (ignored in this case).
     * @param arg1 The ResourceBundle (ignored in this case).
     */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		userPane.setVisible(false);

	}

}
