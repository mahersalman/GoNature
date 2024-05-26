package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import entities.CancellationsReport;
import entities.Message;
import entities.Report;
import entities.TotalVisitorsReport;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Controller class for managing the cancellation report interface.
 */
public class CancellationReportControler implements Initializable {
	@FXML
	private Label date;

	@FXML
	private Label NotVisitedReservations;

	@FXML
	private Label CancelReservations;

	@FXML
	private Label area;

    @FXML
    private PieChart piechart;

	private CancellationsReport report;

    /**
     * Starts the cancellation report interface.
     *
     * @param primaryStage The primary stage to display the report.
     */
	public void start(Stage primaryStage) {
		FXMLLoader loader = new FXMLLoader();
		Pane root;
		try {
			root = loader.load(getClass().getResource("/boundry/CancellationReportGui.fxml").openStream());
			Scene scene = new Scene(root);
			primaryStage.setTitle("Cancellations Report");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    /**
     * Initializes the controller with the cancellation report data.
     *
     * @param arg0 .
     * @param arg1 The resource bundle to initialize the FXML document with.
     */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Message<Report> msg = new Message<>("Get_Cancellation_Report", generatingReport.report);
		ClientUI.chat.accept(msg);
		this.report = (CancellationsReport) ChatClient.server_msg.get("Get_Cancellation_Report").getObject();
		area.setText("Area : "+generatingReport.report.getParkName());
		date.setText("date : "+generatingReport.report.getMonth()+"/"+generatingReport.report.getYear());
		NotVisitedReservations.setText(report.getNotVisitedOrders());
		CancelReservations.setText(report.getCanceledOrder());
		
		PieChart.Data Category1 = new PieChart.Data("Canceled",Integer.parseInt(report.getCanceledOrder()));
		PieChart.Data Category2 = new PieChart.Data("Not Canceled & Not Visited ", Integer.parseInt(report.getNotVisitedOrders()));

		piechart.getData().addAll(Category1, Category2);
	}
    /**
     * Sets the cancellation report.
     *
     * @param report The cancellation report to set.
     */
	public void setReport(CancellationsReport report) {
		this.report = report;
	}

}
