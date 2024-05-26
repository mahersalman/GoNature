package client;




import Controller.HomePageController;
import javafx.application.Application;

import javafx.stage.Stage;

public class ClientUI extends Application {
	public static ClientController chat;

	@Override
	public void start(Stage primaryStage) {
		
		HomePageController homePageControl = new HomePageController();
		homePageControl.start(primaryStage);
		

	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void stop()
	{
		System.exit(0);
	}
}