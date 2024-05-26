package otherSystem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import database.DataBaseController;

/**
 * The UserManagementSystem class provides functionality for managing employees,
 * specifically retrieving employee information from a text file and inserting
 * it into the database.
 *
 * @author Maher Salman
 */
public class UserManagementSystem {

    /**
     * Reads employee information from a text file and inserts it into the database.
     * The file format for each line is:
     *  fullname employeeNumber email role username password park isLogged
     *
     * @throws Exception If an error occurs while reading the file or inserting data.
     */
	public static void EmployeesGetter() {
		String path = "employee.txt";
		String line;
		InputStream inputStream = UserManagementSystem.class.getResourceAsStream(path);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			while ((line = br.readLine()) != null) {
				String[] employee = line.split("\\s+");
				String fullname = employee[0];
				String employeeNumber = employee[1];
				String email = employee[2];
				String role = employee[3];
				String username = employee[4];
				String password = employee[5];
				String park = employee[6].replace('_', ' ');
				String isLogged = employee[7];
				 DataBaseController.insertEmployee(fullname, employeeNumber, email, role,
				 username, password, park, isLogged);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}