package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import entities.Employee;
import entities.Message;
import entities.Park;
import entities.UpdateSettingTable;
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
import javafx.stage.Stage;

/**
 * This class controls the park manager's main window functionality within the
 * JavaFX application. It allows park managers to: - View their park name and
 * full name. - View update requests submitted for their park's settings
 * (maximum visitors, reservations, average visiting time). - Check the current
 * number of visitors in the park. - Generate reports (functionality likely
 * implemented in a separate window). - Update park settings (opens the
 * `ParkSettingsController`). - Logout of the application and return to the home
 * page. - Refresh the update request table.
 */
public class ParkManagerController implements Initializable {
	Employee park_manager = ChatClient.employee;

	@FXML
	private Button genereagteReportBtn;

	@FXML
	private Button LogoutBtn;

	@FXML
	private Button updateBtn;

	@FXML
	private Label parkNameLbl;

	@FXML
	private TableView<UpdateSettingTable> updateSettingTable;

	@FXML
	private TableColumn<UpdateSettingTable, String> maxVisitorsCol;

	@FXML
	private TableColumn<UpdateSettingTable, String> maxReservationsCol;

	@FXML
	private TableColumn<UpdateSettingTable, String> averageVistingTimeCol;

	@FXML
	private TableColumn<UpdateSettingTable, String> updateStatusCol;
	@FXML
	private Label visitorInParkLbl;
	@FXML
	private Button refereshUpdateTable;

	@FXML
	private Button checkVacancyBtn;

	/**
	 * Initializes the controller after FXML loading. - Sets the park name and
	 * manager name labels. - Initializes the update request table: - Requests
	 * update settings for the park manager's park from the server. - Sets up the
	 * table columns with appropriate property value factories. - Populates the
	 * table with data from the `ChatClient.updateSettingTable` collection.
	 *
	 * @param arg0 The URL (ignored in this case).
	 * @param arg1 The ResourceBundle (ignored in this case).
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		parkNameLbl.setText("Park : " + park_manager.getPark() + "\nManager : " + park_manager.getFullName());
		// initilize updateTable
		getRequestUpdateSettingTable();
		maxVisitorsCol.setCellValueFactory(new PropertyValueFactory<UpdateSettingTable, String>("MaxVisitors"));
		maxReservationsCol.setCellValueFactory(new PropertyValueFactory<UpdateSettingTable, String>("MaxReservations"));
		averageVistingTimeCol
				.setCellValueFactory(new PropertyValueFactory<UpdateSettingTable, String>("AverageWaitingTime"));
		updateStatusCol.setCellValueFactory(new PropertyValueFactory<UpdateSettingTable, String>("Status"));
		updateSettingTable.setItems(FXCollections.observableArrayList(ChatClient.updateSettingTable));

	}

	/**
	 * Handles the "Check Vacancy" button click. - Sends a message to the server
	 * requesting information about the park (including current visitors). -
	 * Retrieves the `Park` object from the server response. - Updates the "Visitors
	 * in Park" label with the current visitor count.
	 *
	 * @param event The ActionEvent object associated with the button click.
	 */
	@FXML
	void checkVacancy(ActionEvent event) {
		Message<Park> msg = new Message<>("GetParkInfo", new Park(ChatClient.employee.getPark(), 0, 0, 0, 0));
		System.out.println("ChatClient.employee.getPark() : = >" + ChatClient.employee.getPark());
		ClientUI.chat.accept(msg);
		Park park = (Park) ChatClient.server_msg.get("GetParkInfo").getObject();
		visitorInParkLbl.setText(String.valueOf(park.getCurrentlyVisitorsInPark()));
	}

	/**
	 * Handles the "Logout" button click. - Sends a message to the server indicating
	 * employee logout. - Clears the logged-in employee information. -move to
	 * `HomePageController` controller and gui
	 * 
	 * @param event The ActionEvent object associated with the button click.
	 */
	@FXML
	void Logout(ActionEvent event) {
		Message<Employee> msg = new Message<>("Employee_Logout", park_manager);
		ClientUI.chat.accept(msg);
		ChatClient.employee = null;
		HomePageController control = new HomePageController();
		control.start(new Stage());
		((Node) event.getSource()).getScene().getWindow().hide();

	}

	/**
	 * Handles the "Generate Report" button click. - Loads the "GenereteReport.fxml"
	 * file (likely for a separate report generation window). - Launches the report
	 * window. - Hides the current park manager window.
	 *
	 * @param event The ActionEvent object associated with the button click.
	 * @throws IOException If an error occurs while loading the FXML file.
	 */
	@FXML
	void genereteReport(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/boundry/GenereteReport.fxml"));
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
	 * Handles the "Update Settings" button click. - Creates a new
	 * `ParkSettingsController` instance. - Launches the park settings window. -
	 * Hides the current park manager window.
	 *
	 * @param event The ActionEvent object associated with the button click.
	 * @throws IOException If an error occurs while launching the park settings
	 *                     window.
	 */
	@FXML
	void updateSettings(ActionEvent event) throws IOException {
		ParkSettingsController control = new ParkSettingsController();
		control.start(new Stage());
		((Node) event.getSource()).getScene().getWindow().hide();

	}

	/**
	 * Requests update settings for the park manager's park from the server. - Sends
	 * a message with type "Get_Request_Update_Settings_ParkManager" and the park
	 * name.
	 */
	private void getRequestUpdateSettingTable() {
		Message<String> msg = new Message<>("Get_Request_Update_Settings_ParkManager", ChatClient.employee.getPark());
		ClientUI.chat.accept(msg);

	}

	/**
	 * Handles the "Refresh Update Request Table" button click. - Requests update
	 * settings for the park manager's park from the server. - Refreshes the update
	 * request table data.
	 *
	 * @param event The ActionEvent object associated with the button click.
	 */
	@FXML
	void refereshUpdateRequestTable(ActionEvent event) {
		getRequestUpdateSettingTable();
		refresh();
	}

	/**
	 * Refreshes the update request table data. - Updates the table items with the
	 * latest data from `ChatClient.updateSettingTable`. - Calls the `refresh`
	 * method on the table view to visually update the table.
	 */
	public void refresh() {
		updateSettingTable.setItems(FXCollections.observableArrayList(ChatClient.updateSettingTable));
		updateSettingTable.refresh();
	}

}
