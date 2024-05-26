package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import Server.EchoServer;

/** Class Jdbc For Connecting to DataBase*/
public class Jdbc {
	/**static enitite for the connection to db*/
	public static Connection connection;
	
	/** methods to connect to db 
	 * 
	 * @param db_username  username for database
	 * @param db_password  password for database
	 * 
	 * @return true if connection success ,otherwise false
	 * */
	public static boolean connectionToDB(String db_username,String db_password) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			EchoServer.serverController.writeToLog("Driver definition succeed");
		} catch (Exception ex) {
			EchoServer.serverController.writeToLog("Driver definition failed");
			return false;
		}

		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost/gonature?serverTimezone=IST", db_username,db_password);
			EchoServer.serverController.writeToLog("SQL connection succeed");
		} catch (SQLException ex) {
			EchoServer.serverController.writeToLog("SQL connection Failed");
			EchoServer.serverController.writeToLog("SQLException: " + ex.getMessage());
			EchoServer.serverController.writeToLog("SQLState: " + ex.getSQLState());
			EchoServer.serverController.writeToLog("VendorError: " + ex.getErrorCode());
			return false;
		}
		return true;

	}

}
