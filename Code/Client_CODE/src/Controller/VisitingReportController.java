package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import entities.CancellationsReport;
import entities.Message;
import entities.Report;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * class controls the visiting report functionality within the JavaFX
 * application. It handles displaying the report's month and year, fetching data
 * from the server, populating a bar chart with average stay times, and
 * displaying the report visually.
 */
public class VisitingReportController implements Initializable {
	@FXML
	private Label Month;
	@FXML
	private BarChart<String, Number> StayTimeBar;
	
    /**
     * (Optional) This method could potentially be used to launch the visiting report
     * GUI from another part of the application. It's currently unused and commented out.
     *
     * @param primaryStage The Stage on which to display the visiting report GUI.
     * @throws IOException If an error occurs while loading the FXML file.
     */
	public void start(Stage primaryStage) {
		FXMLLoader loader = new FXMLLoader();
		Pane root;
		try {
			root = loader.load(getClass().getResource("/boundry/visitingReportGui.fxml").openStream());
			Scene scene = new Scene(root);
			primaryStage.setTitle("visiting Report");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the controller. Sets the month and year label, sends a message to
	 * the server requesting the visiting report data, and populates the bar chart
	 * with average stay time information.
	 *
	 * @param arg0 The URL that was used to load the FXML file.
	 * @param arg1 The resource bundle that was used to localize the root element.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Month.setText(generatingReport.report.getMonth() + "/" + generatingReport.report.getYear());
		Message<Report> msg = new Message<>("Get_Visiting_Report", generatingReport.report);
		ClientUI.chat.accept(msg);
		ArrayList<Float> avgStayTime = (ArrayList<Float>) ChatClient.server_msg.get("Get_Visiting_Report").getObject();

		XYChart.Series series1 = new XYChart.Series();
		series1.setName("Individual/Family");
		series1.getData().add(new XYChart.Data("8:00", avgStayTime.get(0)));
		series1.getData().add(new XYChart.Data("9:00", avgStayTime.get(1)));
		series1.getData().add(new XYChart.Data("10:00", avgStayTime.get(2)));
		series1.getData().add(new XYChart.Data("11:00", avgStayTime.get(3)));
		series1.getData().add(new XYChart.Data("12:00", avgStayTime.get(4)));
		series1.getData().add(new XYChart.Data("13:00", avgStayTime.get(5)));
		series1.getData().add(new XYChart.Data("14:00", avgStayTime.get(6)));
		series1.getData().add(new XYChart.Data("15:00", avgStayTime.get(7)));

		XYChart.Series series2 = new XYChart.Series();
		series2.setName("Group");
		series2.getData().add(new XYChart.Data("8:00", avgStayTime.get(8)));
		series2.getData().add(new XYChart.Data("9:00", avgStayTime.get(9)));
		series2.getData().add(new XYChart.Data("10:00", avgStayTime.get(10)));
		series2.getData().add(new XYChart.Data("11:00", avgStayTime.get(11)));
		series2.getData().add(new XYChart.Data("12:00", avgStayTime.get(12)));
		series2.getData().add(new XYChart.Data("13:00", avgStayTime.get(13)));
		series2.getData().add(new XYChart.Data("14:00", avgStayTime.get(14)));
		series2.getData().add(new XYChart.Data("15:00", avgStayTime.get(15)));

		StayTimeBar.getData().addAll(series1, series2);

	}

}
