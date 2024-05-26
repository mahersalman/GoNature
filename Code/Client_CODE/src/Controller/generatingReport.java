package Controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import client.ChatClient;
import client.ClientUI;
import entities.Employee;
import entities.Message;
import entities.Report;
import entities.Reservation;
import entities.TotalVisitorsReport;
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
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
/**
 * This class controls the report generation functionality within the JavaFX application.
 * It allows park managers and department managers to generate different reports based on selection criteria.
 */
public class generatingReport implements Initializable {
	Employee employee = ChatClient.employee;
	public static Report report;
	@FXML
	private ComboBox<String> typeBox;

	@FXML
	private Button generateBtn;

	@FXML
	private ComboBox<String> monthBox;

	@FXML
	private ComboBox<String> yearBox;

	@FXML
	private Button backBtn;

	@FXML
	private ComboBox<String> comboBoxParkName;

    /**
     * - Loads the appropriate FXML file based on the employee's role (department manager or park manager).
     * - Launches the corresponding home page window.
     * - Hides the current report generation window.
     *
     * @param event The ActionEvent object associated with the button click.
     */
	@FXML
	void back(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader();
		switch (ChatClient.employee.getRole()) {
		case "DepartmentManager":
			loader.setLocation(getClass().getResource("/boundry/DepartMentManagerHomePage.fxml"));
			break;
		case "ParkManager":
			loader.setLocation(getClass().getResource("/boundry/ParkManagerHomePageGUI.fxml"));
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

	}


    /**
     * Initializes the controller after FXML loading.
     * - Sets up the combo boxes with appropriate options for report type, month, year, and park (if applicable).
     * - Disables the park selection if the user is a park manager (their park is pre-selected).
     *
     * @param arg0 The URL (ignored in this case).
     * @param arg1 The ResourceBundle (ignored in this case).
     */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		int currentYear = LocalDate.now().getYear();
		Collection<String> type;
		Collection<String> date_month = Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");
		Collection<String> parks = Arrays.asList("ALL", "Banias Nature", "Gan HaShlosha", "Ein Gedi");
		comboBoxParkName.getItems().addAll(parks);
		monthBox.getItems().addAll(date_month);

		Collection<String> yearsList = new ArrayList<>();

		for (int year = currentYear; year >= currentYear - 5; year--) {
			yearsList.add(String.valueOf(year));
		}
		yearBox.getItems().addAll(yearsList);

		if (employee.getRole().equals("ParkManager")) {
			type = Arrays.asList("Total Visitors", "Usage Report");
			comboBoxParkName.getSelectionModel().select(employee.getPark());
			comboBoxParkName.setDisable(true);
		} else {
			type = Arrays.asList("Total Visitors", "Usage Report", "Visiting Report", "Cancel Report");

		}

		typeBox.getItems().addAll(type);

	}

    /**
     * Handles the "Generate Report" button click event.
     * - Validates the user's selection for report type, month, year, and park (if applicable).
     * - If all selections are valid:
     *   - Creates a `Report` object with the selected criteria.
     *   - Launches the appropriate report display controller based on the selected type.
     * - Displays an error message if any selection is missing or invalid (e.g., "ALL Parks" can only be used for Cancel Reports).
     *
     * @param event The ActionEvent object associated with the button click.
     */
	@SuppressWarnings("static-access")
	@FXML
	void genereteReport(ActionEvent event) {
		String type = typeBox.getSelectionModel().getSelectedItem();
		String month = monthBox.getSelectionModel().getSelectedItem();
		String year = yearBox.getSelectionModel().getSelectedItem();
		String park = comboBoxParkName.getSelectionModel().getSelectedItem();

		if (type == null || month == null || year == null || park == null) {
			JOptionPane.showMessageDialog(null, "Missing Information", "", JOptionPane.INFORMATION_MESSAGE);
		} else if (park.equals("ALL") && !type.equals("Cancel Report")) {
			JOptionPane.showMessageDialog(null, "Only CancelReport Can Be For ALL Parks", "",
					JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			this.report = new Report(month, year, park, type);
			switch (type) {
			case "Total Visitors":
				DisplayTotalVisitorsReport totalVisitorControl = new DisplayTotalVisitorsReport();
				totalVisitorControl.start(new Stage());
				break;
			case "Usage Report":
				UsageReportController usageReport = new UsageReportController();
				usageReport.start(new Stage());
				break;
			case "Visiting Report":
				VisitingReportController VisitReport = new VisitingReportController();
				VisitReport.start(new Stage());
				break;
			case "Cancel Report":
				CancellationReportControler CancelControl = new CancellationReportControler();
				CancelControl.start(new Stage());
				break;

			}

		}

	}
}