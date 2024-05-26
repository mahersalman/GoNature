package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import entities.Message;
import entities.Report;
import entities.TotalVisitorsReport;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Controller class for displaying the total visitors report.
 */
public class DisplayTotalVisitorsReport implements Initializable {

	@FXML
	private PieChart Piechart;
	private TotalVisitorsReport report;

    /**
     * Starts the display of the total visitors report.
     *
     * @param primaryStage The primary stage to display the report.
     */
	public void start(Stage primaryStage) {
		FXMLLoader loader = new FXMLLoader();
		Pane root;
		try {
			root = loader.load(getClass().getResource("/boundry/DisplayTotalVisitorsReport.fxml").openStream());
			Scene scene = new Scene(root);
			primaryStage.setTitle("Total Visitor Report");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
    /**
     * Initializes the controller with the total visitors report data.
     *
     * @param arg0 The URL location of the FXML document to initialize.
     * @param arg1 The resource bundle to initialize the FXML document with.
     */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Message<Report> msg = new Message<>("Get_Total_Visitors_Report", generatingReport.report);
		ClientUI.chat.accept(msg);
		this.report = (TotalVisitorsReport) ChatClient.server_msg.get("Get_Total_Visitors_Report").getObject();

		int groupsCount = Integer.parseInt(report.getGroupsNumber());
		int individualsCount = Integer.parseInt(report.getIndividualsNumbers());

		PieChart.Data groupsData = new PieChart.Data("Groups", groupsCount);
		PieChart.Data individualsData = new PieChart.Data("Individuals", individualsCount);

		Piechart.getData().addAll(groupsData, individualsData);
		
	}
	   /**
     * load the total visitors report.
     *
     * @param report The total visitors report to set.
     */

	public void setReport(TotalVisitorsReport report) {
		this.report = report;
	}

}