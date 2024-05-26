package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import client.ChatClient;
import client.ClientUI;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * Controller class for the Department Manager interface.
 */
public class DepartmentMangerController implements Initializable {

	@FXML
	private Button reportsbtn;

	@FXML
	private Button LogoutBtn;

	@FXML
	private Button confirmBtn;

	@FXML
	private Button checkVacancy;

	@FXML
	private Button DeclinedBtn;

	@FXML
	private Button RefreshUpdateRequestTable;

	@FXML
	private TableView<UpdateSettingTable> updateTable;

	@FXML
	private TableColumn<UpdateSettingTable, String> maxVisitorsCol;

	@FXML
	private TableColumn<UpdateSettingTable, String> maxReservationCol;

	@FXML
	private TableColumn<UpdateSettingTable, String> avgVisitingTimeCol;

	@FXML
	private TableColumn<UpdateSettingTable, String> parkCol;
	@FXML
	private Label visitorInParkLbl;
	@FXML
	private ComboBox<String> comboBoxParkName;

	/**
	 * Initializes the controller with the necessary data and UI components.
	 *
	 * @param arg0 The URL location of the FXML document to initialize.
	 * @param arg1 The resource bundle to initialize the FXML document with.
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		getRequestUpdateSettingTable();
		maxVisitorsCol.setCellValueFactory(new PropertyValueFactory<UpdateSettingTable, String>("MaxVisitors"));
		maxReservationCol.setCellValueFactory(new PropertyValueFactory<UpdateSettingTable, String>("MaxReservations"));
		avgVisitingTimeCol
				.setCellValueFactory(new PropertyValueFactory<UpdateSettingTable, String>("AverageWaitingTime"));
		parkCol.setCellValueFactory(new PropertyValueFactory<UpdateSettingTable, String>("park_name"));
		updateTable.setItems(FXCollections.observableArrayList(ChatClient.updateSettingTable));
		Collection<String> parks = Arrays.asList("Banias Nature", "Gan HaShlosha", "Ein Gedi");
		comboBoxParkName.getItems().addAll(parks);
	}

	/**
	 * Retrieves and displays the current vacancy of the selected park.
	 *
	 * @param event The action event triggered by clicking the "Check Vacancy"
	 *              button.
	 */
	@FXML
	void checkVacancy(ActionEvent event) {
		Message<Park> msg = new Message<>("GetParkInfo",
				new Park(comboBoxParkName.getSelectionModel().getSelectedItem(), 0, 0, 0, 0));
		System.out.println("ChatClient.employee.getPark() : = >" + ChatClient.employee.getPark());
		ClientUI.chat.accept(msg);
		Park park = (Park) ChatClient.server_msg.get("GetParkInfo").getObject();
		visitorInParkLbl.setText(String.valueOf(park.getCurrentlyVisitorsInPark()));
	}

	/**
	 * Logs out the department manager.
	 *
	 * @param event The action event triggered by clicking the "Logout" button.
	 */
	@FXML
	void Logout(ActionEvent event) {
		HomePageController control = new HomePageController();
		control.start(new Stage());
		((Node) event.getSource()).getScene().getWindow().hide();

	}

	/**
	 * Declines the selected update request.
	 *
	 * @param event The action event triggered by clicking the "Declined" button.
	 */
	@FXML
	void DeclinedRequest(ActionEvent event) {
		UpdateSettingTable selectedRequest = updateTable.getSelectionModel().getSelectedItem();
		if (selectedRequest != null) {
			selectedRequest.setStatus("Declined");
			Message<UpdateSettingTable> msg = new Message<UpdateSettingTable>("Declined_Update_Request",
					selectedRequest);
			ClientUI.chat.accept(msg);
			getRequestUpdateSettingTable();
			refresh();
			JOptionPane.showMessageDialog(null, "Request Declined Success", "", JOptionPane.INFORMATION_MESSAGE);

		} else {
			JOptionPane.showMessageDialog(null, "Please select a Request" + " to update.", "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Refreshes the update request table.
	 *
	 * @param event The action event triggered by clicking the "Refresh" button.
	 */
	@FXML
	void RefreshUpdateRequestTable(ActionEvent event) {
		getRequestUpdateSettingTable();
		refresh();
	}

	/**
	 * Opens the report generation interface.
	 *
	 * @param event The action event triggered by clicking the "Reports" button.
	 */
	@FXML
	void Reports(ActionEvent event) {
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
	 * Confirms the selected update request.
	 *
	 * @param event The action event triggered by clicking the "Confirm" button.
	 */
	@FXML
	void confiormRequest(ActionEvent event) {
		UpdateSettingTable selectedRequest = updateTable.getSelectionModel().getSelectedItem();
		if (selectedRequest != null) {
			selectedRequest.setStatus("Confirm");
			Message<UpdateSettingTable> msg = new Message<UpdateSettingTable>("Confirm_Update_Request",
					selectedRequest);
			ClientUI.chat.accept(msg);
			getRequestUpdateSettingTable();
			refresh();
			JOptionPane.showMessageDialog(null, "Request Confirmed Success", "", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, "Please select a Request" + " to update.", "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Retrieves the update setting table from the server.
	 */
	private void getRequestUpdateSettingTable() {
		Message<ArrayList<UpdateSettingTable>> msg = new Message<>("Get_Request_Update_Settings_DepartmentManager",
				new ArrayList<UpdateSettingTable>());
		ClientUI.chat.accept(msg);

	}

	/**
	 * Refreshes the updateTable table view.
	 */
	public void refresh() {
		updateTable.setItems(FXCollections.observableArrayList(ChatClient.updateSettingTable));
		updateTable.refresh();
	}

}
