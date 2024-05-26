package Server;

import javafx.application.Application;

import javafx.stage.Stage;

import java.io.IOException;

import controller.ServerController;
import database.Jdbc;

/**
 * The ServerUI class serves as the entry point and controller for the server application.
 *
 * @author Maher Salman     */
public class ServerUI extends Application {
    /**
     * Static reference to the EchoServer instance for global access.
     */
	public static EchoServer sv;

    /**
     * Launches the JavaFX application.
     *
     * @param args The command line arguments.
     * @throws Exception If an error occurs during application launch.
     */
	public static void main(String args[]) throws Exception {
		launch(args);
	} 

    /**
     * Initializes the ServerController and starts the GUI.
     *
     * @param primaryStage The primary stage of the application.
     * @throws Exception If an error occurs during GUI initialization.
     */
	@Override
	public void start(Stage primaryStage) throws Exception {
		ServerController aFrame = new ServerController();
		aFrame.start(primaryStage);
		
	}
	
	 /**
     * Starts the EchoServer on the specified port.

     *
     * @param p The port number as a String.
     * @return True if the server started successfully, false otherwise.
     */
	public static boolean runServer(String p) {
		int port = 0; // Port to listen on

		try {
			port = Integer.parseInt(p); 
		} catch (Throwable t) {
			EchoServer.serverController.writeToLog("ERROR - Could not connect!");
			return false;
			
		}

		sv = new EchoServer(port);

		try {
			sv.listen(); 
		} catch (Exception ex) {
			EchoServer.serverController.writeToLog("ERROR - Could not listen for clients!");
			return false;
		}
		EchoServer.serverController.writeToLog("Server Start Listning to port :" +  p );
		return true;
	}
	
    /**
     * Closes the EchoServer.
     *
     * @return True if the server closed successfully, false otherwise.
     */
	public static boolean closeServer()
	{
		try {
			if(sv!=null)
				sv.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
     * Called when the application is shutting down.
     */
	@Override
	public void stop()
	{
		closeServer();
	}

}
