package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import entities.GenereteUsageReport;
import entities.Message;
import entities.Report;
import entities.UpdateSettingTable;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
/**
 * This class controls the Usage Report functionality within the JavaFX application.
 * It handles displaying the park name and date for the report, fetching usage data
 * from the server, populating a table with information about the number of visitors
 * per hour, and displaying the report visually.
 */
public class UsageReportController implements Initializable {

	@FXML
	private Label parkName;

	@FXML
	private Label date;

	@FXML
	private TableView<GenereteUsageReport> TableViewUsage;

	@FXML
	private TableColumn<GenereteUsageReport, String> dateCol;

	@FXML
	private TableColumn<GenereteUsageReport, String> hourCol;
	
    /**
	 *This method used to launch the usage report
	 *     *
     * @param primaryStage The Stage on which to display the usage report GUI.
     * @throws IOException If an error occurs while loading the FXML file.
     */
	public void start(Stage primaryStage) {
		FXMLLoader loader = new FXMLLoader();
		Pane root;
		try {
			root = loader.load(getClass().getResource("/boundry/UsageReportGUI.fxml").openStream());
			Scene scene = new Scene(root);
			primaryStage.setTitle("Usage Report");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/**
	 * This class controls the Usage Report functionality within the JavaFX application.
	 * It handles displaying the park name and date for the report, fetching usage data
	 * from the server, populating a table with information about the number of visitors
	 * per hour, and displaying the report visually.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		parkName.setText("Park : "+ generatingReport.report.getParkName());	
		date.setText("Date : "+ generatingReport.report.getMonth()+"/"+generatingReport.report.getYear());		

		dateCol.setCellValueFactory(new PropertyValueFactory<GenereteUsageReport, String>("date"));
		hourCol.setCellValueFactory(new PropertyValueFactory<GenereteUsageReport, String>("hour"));
		
		Message<Report> msg = new Message<>("Get_Usage_Report", generatingReport.report);
		ClientUI.chat.accept(msg);
		ArrayList<GenereteUsageReport> usageReport = (ArrayList<GenereteUsageReport>) ChatClient.server_msg.get("Get_Usage_Report").getObject();
		TableViewUsage.setItems(FXCollections.observableArrayList(usageReport));
	}

}
