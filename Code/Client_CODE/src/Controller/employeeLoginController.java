package Controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import client.ChatClient;
import client.ClientUI;
import entities.ConnectedClients;
import entities.Employee;
import entities.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * This class is the controller for the employeeLogin It handles user
 * interaction for employee login, including displaying error messages,
 * validating input, and navigating to role-specific screens upon successful
 * login.
 */
public class employeeLoginController {
	@FXML
	private Label noteLbl;

	@FXML
	private TextField usernameTxt;

	@FXML
	private TextField passwordTxt;

	@FXML
	private Button loginBtn;

	@FXML
	private Button backBtn;

	/**
	 * Handles going back to the HomePage scene.
	 *
	 * @param event The ActionEvent triggered by clicking the Back button.
	 */
	@FXML
	void back(ActionEvent event) {
		HomePageController control = new HomePageController();
		control.start(new Stage());
		((Node) event.getSource()).getScene().getWindow().hide();

	}

	/**
	 * Handles the login process, including input validation, sending login request
	 * to the server, receiving response, and navigating to appropriate screens or
	 * displaying error messages.
	 *
	 * @param event The ActionEvent triggered by clicking the Login button.
	 */
	@SuppressWarnings("unchecked")
	@FXML
	void login(ActionEvent event) {

		if (usernameTxt.getText().trim().isEmpty() || passwordTxt.getText().trim().isEmpty()) {
			noteLbl.setText("You must fill all fields correctly before signning in");
		} else {
			ChatClient.employee = new Employee(usernameTxt.getText(), passwordTxt.getText());
			Message<Employee> msg = new Message<>("Employee_Login", ChatClient.employee);
			ClientUI.chat.accept(msg);

			msg = (Message<Employee>) ChatClient.server_msg.get("Employee_Login");
			if (msg.getMsg().equals("Employee_Login_Sucesss")) {
				if (ChatClient.employee.getIsLogged().equals("0")) {
					try {
						ConnectedClients client_info = new ConnectedClients(InetAddress.getLocalHost().getHostAddress(),
								InetAddress.getLocalHost().getHostName(), ChatClient.employee.getRole(),
								"" + ChatClient.employee.getEmployeeNumber());
						Message<ConnectedClients> msg_connect = new Message<>("add_client", client_info);
						ClientUI.chat.accept(msg_connect);
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
					FXMLLoader loader = new FXMLLoader();
					switch (ChatClient.employee.getRole()) {
					case "EntryWorker":
						loader.setLocation(getClass().getResource("/boundry/EntryWorkerGui.fxml"));
						break;
					case "DepartmentManager":
						loader.setLocation(getClass().getResource("/boundry/DepartMentManagerHomePage.fxml"));
						break;
					case "ParkManager":
						loader.setLocation(getClass().getResource("/boundry/ParkManagerHomePageGUI.fxml"));
						break;
					case "ServiceRepresenitive":
						loader.setLocation(getClass().getResource("/boundry/ServiceRepresentitive.fxml"));
						break;
					}
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

				} else {
					noteLbl.setText("The User Already Log in ..");
				}
			} else {
				noteLbl.setText("Username Or Password is wrong .");
			}

		}
	}

	/**
	 * handle load the fxml employeeLogin gui
	 * 
	 * @param stage
	 */
	public void start(Stage primaryStage) {
		FXMLLoader loader = new FXMLLoader();
		Pane root;
		try {
			root = loader.load(getClass().getResource("/boundry/employeeLogin.fxml").openStream());
			Scene scene = new Scene(root);
			primaryStage.setTitle("employeeLogin");
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
