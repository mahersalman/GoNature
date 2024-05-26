package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

import entities.CancellationsReport;
import entities.Date;
import entities.Employee;
import entities.GenereteUsageReport;
import entities.TravelerWithoutReservation;
import entities.Message;
import entities.Park;
import entities.Report;
import entities.Reservation;
import entities.TotalVisitorsReport;
import entities.UpdateSettingTable;
import entities.User;

/**
 * This Class Controller , include all methods as static to control On the
 * dataBase
 *
 * @author Maher Salman
 */
public class DataBaseController {
	/**
	 * Inserts a new employee record into the "gonature.employee" table in the
	 * database.
	 * 
	 * @param fullname       The full name of the employee.
	 * @param employeeNumber The employee's identification number.
	 * @param email          The employee's email address.
	 * @param role           The employee's job role or position.
	 * @param username       The employee's username for system login.
	 * @param password       The employee's password for system login.
	 * @param park           The park where the employee is assigned (assumed park
	 *                       management system).
	 * @param isLogged       A flag indicating whether the employee is currently
	 *                       logged in (assumed for tracking).
	 */
	public static void insertEmployee(String fullname, String employeeNumber, String email, String role,
			String username, String password, String park, String isLogged) {
		String query = "INSERT INTO gonature.employee VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement statement = Jdbc.connection.prepareStatement(query);
			statement.setString(1, fullname);
			statement.setString(2, employeeNumber);
			statement.setString(3, email);
			statement.setString(4, role);
			statement.setString(5, username);
			statement.setString(6, password);
			statement.setString(7, park);
			statement.setString(8, isLogged);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method handles user identification based on a received message object.
	 * And Use Data Base To check if he already logged In or Not if not exits in
	 * data base mean not logged in then create new row for this use in db
	 * 
	 * @param <T> The generic type of the message object's data (assumed to be a
	 *            subclass of User).
	 * @param msg The message object containing user data.
	 * 
	 * @throws SQLException If an error occurs during database operations.
	 */
	public static <T> void identification(Message<T> msg) {
		try {
			User user = (User) msg.getObject();
			PreparedStatement ps = Jdbc.connection.prepareStatement("SELECT * FROM users WHERE id = ?");
			ps.setString(1, user.getId());

			ResultSet rs = ps.executeQuery();

			// found
			if (rs.next()) {
				if (rs.getString(2).equals("0")) {
					user.setGuide(false);
				} else {
					user.setGuide(true);
				}
				if (rs.getString(3).equals("0")) {
					user.setLogged(true);
					UpdateUserLogged("1", user.getId());
					msg.setMsg("Identification_Success");
				} else {
					msg.setMsg("Identification_Failed");
				}
			} else {// not found create new User
				InsertUser(user.getId(), "0", "1");
				user.setGuide(false);
				user.setLogged(true);
				msg.setMsg("Identification_Success");
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method handles employee login based on a received message object. check
	 * it on data base and return message and employee info
	 * 
	 * @param tmp The message object containing employee login credentials.
	 * 
	 * @throws SQLException If an error occurs during database operations.
	 */
	public static void Login(Message<?> tmp) {
		Employee employee = (Employee) tmp.getObject();
		try {
			PreparedStatement ps = Jdbc.connection.prepareStatement(
					"SELECT fullname,employeeNumber,email,role,park,isLogged FROM employee WHERE username = ? AND password = ?");
			ps.setString(1, employee.getUserName());
			ps.setString(2, employee.getPassword());

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				employee.setFullName(rs.getString(1));
				employee.setEmployeeNumber(Integer.parseInt(rs.getString(2)));
				employee.setEmail(rs.getString(3));
				employee.setRole(rs.getString(4));
				employee.setPark(rs.getString(5));
				employee.setIsLogged(rs.getString(6));

				if (employee.getIsLogged().equals("0")) {
					tmp.setMsg("Employee_Login_Sucesss");
					UpdateEmployeeLogged("1", employee.getUserName(), employee.getPassword());
				} else {
					tmp.setMsg("Employee_Login_Failed");
				}

			} else {
				tmp.setMsg("Employee_Login_Failed");

			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method updates the logged-in status of an employee in the database.
	 * 
	 * @param isLogged The new logged-in status ("0" for not logged in, "1" for
	 *                 logged in).
	 * @param userName The username of the employee to update.
	 * @param password The password of the employee to update.
	 * 
	 * @throws SQLException If an error occurs during database operations.
	 */
	public static void UpdateEmployeeLogged(String isLogged, String userName, String password) {
		try {
			PreparedStatement ps = Jdbc.connection
					.prepareStatement("UPDATE gonature.Employee SET isLogged = ? WHERE username = ? AND password = ?");
			ps.setString(1, isLogged);
			ps.setString(2, userName);
			ps.setString(3, password);

			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method updates the logged-in status of a user in the database.
	 * 
	 * @param isLogged The new logged-in status ("0" for not logged in, "1" for
	 *                 logged in).
	 * @param id       The ID of the user to update.
	 * 
	 * @throws SQLException If an error occurs during database operations.
	 */
	public static void UpdateUserLogged(String isLogged, String id) {
		try {
			PreparedStatement ps = Jdbc.connection
					.prepareStatement("UPDATE gonature.users SET isLogged = ? WHERE id = ?");
			ps.setString(1, isLogged);
			ps.setString(2, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method inserts a new user record into the "gonature.users" table in the
	 * database.
	 * 
	 * @param id       The unique identifier for the user.
	 * @param isGuide  A flag indicating whether the user is a guide ("0" for not a
	 *                 guide, "1" for guide).
	 * @param isLogged The initial logged-in status of the user ("0" for not logged
	 *                 in, "1" for logged in).
	 * 
	 * @throws SQLException If an error occurs during database operations.
	 */
	public static void InsertUser(String id, String isGuide, String isLogged) {
		try {
			PreparedStatement ps = Jdbc.connection
					.prepareStatement("INSERT INTO gonature.users (id ,Guide, isLogged)VALUES(?,?,?)");
			ps.setString(1, id);
			ps.setString(2, isGuide);
			ps.setString(3, isLogged);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves a user's reservations from the database and creates a message
	 * object containing them.
	 * 
	 * @param <T> The generic type of the input message object (assumed to contain a
	 *            User object).
	 * @param msg The input message object containing the user's information.
	 * @return A message object containing a list of the user's reservations, or
	 *         null if an error occurs.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Message<T> GetUserReservations(Message<T> msg) {
		User user = (User) msg.getObject();
		Message<ArrayList<Reservation>> reservation = new Message<ArrayList<Reservation>>("User_Reservation",
				new ArrayList<Reservation>());
		try {
			PreparedStatement ps = Jdbc.connection
					.prepareStatement("SELECT * FROM gonature.Reservation WHERE userID = ?");
			ps.setString(1, user.getId());

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Reservation tmp = new Reservation(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getInt(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9),
						rs.getString(10), rs.getString(11));
				reservation.getObject().add(tmp);
			}

			rs.close();
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return (Message<T>) reservation;

	}

	/**
	 * Retrieves a reservation from the database based on its reservation number.
	 * 
	 * @param reservationNumber The unique reservation number to search for.
	 * @return A Reservation object containing the details if found, null otherwise.
	 * @throws SQLException If an error occurs during database operations.
	 */
	public static Reservation getReservationByNumber(int reservationNumber) {
		Reservation reservation = null;
		try {
			PreparedStatement ps = Jdbc.connection
					.prepareStatement("SELECT * FROM gonature.Reservation WHERE reservationId = ?");
			ps.setInt(1, reservationNumber);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				reservation = new Reservation(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getInt(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9),
						rs.getString(10), rs.getString(11));

			}

			rs.close();
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return reservation;

	}

	/**
	 * Creates a new reservation in the database based on a provided Reservation
	 * object.
	 * 
	 * @param msg A message object containing a Reservation object with the
	 *            reservation details.
	 * @throws SQLException If an error occurs during database operations.
	 */
	@SuppressWarnings("unchecked")
	public static void createReservation(Message<?> msg) {
		Reservation tmp = ((Message<Reservation>) msg).getObject();
		try {
			PreparedStatement ps = Jdbc.connection.prepareStatement(
					"INSERT INTO gonature.Reservation (userID, visitDate, visitHour, numberOfVisitors, park_name, email, phoneNumber, isGroup, Status,isPaid) VALUES(?,?,?,?,?,?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, tmp.getUserID());
			ps.setString(2, tmp.getVisitDate());
			ps.setString(3, tmp.getVisitHour());
			ps.setInt(4, tmp.getNumberOfVisitors());
			ps.setString(5, tmp.getPark_name());
			ps.setString(6, tmp.getEmail());
			ps.setString(7, tmp.getPhoneNumber());
			ps.setString(8, tmp.getIsGroup());
			ps.setString(9, tmp.getStatus());
			ps.setString(10, tmp.getIsPaid());

			ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				tmp.setReservationId(rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates the status of a reservation in the gonature database.
	 *
	 * @param reservation The reservation object containing the updated status.
	 * @throws SQLException If there is a database error during the update process.
	 */
	public static void UpdateReservationStatus(Reservation reservation) {
		try {
			PreparedStatement ps = Jdbc.connection
					.prepareStatement("UPDATE gonature.Reservation SET Status = ? WHERE reservationId = ?");
			ps.setString(1, reservation.getStatus());
			ps.setInt(2, reservation.getReservationId());
			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Registers a guide in the gonature database.
	 *
	 * @param msg The message containing the guide to register.
	 * @throws SQLException If there is a database error during user selection or
	 *                      update (should rarely happen with proper connection
	 *                      handling).
	 */
	public static void RegisterGuide(Message<?> msg) {
		User guide = (User) msg.getObject();
		try {
			PreparedStatement ps = Jdbc.connection.prepareStatement("SELECT Guide FROM gonature.Users WHERE id = ?");
			ps.setString(1, guide.getId());

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getString(1).equals("1")) {
					msg.setMsg("Guide_Already_Register");
				} else {
					try {
						PreparedStatement up = Jdbc.connection
								.prepareStatement("UPDATE users SET Guide = ? WHERE id = ?");
						up.setString(1, "1");
						up.setString(2, guide.getId());
						up.executeUpdate();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					msg.setMsg("Guide_Registered_Success");
				}

			} else {
				DataBaseController.InsertUser(guide.getId(), "1", "0");
				msg.setMsg("Guide_Registered_Success");

			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Updates the settings in the gonature database by inserting a new record into
	 * the UpdateSettingTable.
	 *
	 * @param tmp The message containing the updated settings.
	 * @throws SQLException If there is a database error during the update process.
	 */
	public static void UpdateSettings(Message<?> tmp) {
		UpdateSettingTable newSetting = (UpdateSettingTable) tmp.getObject();
		try {
			PreparedStatement ps = Jdbc.connection.prepareStatement(
					"INSERT INTO gonature.UpdateSettingTable (MaxVisitors, MaxReservations, AverageWaitingTime, Status, park_name)VALUES(?,?,?,?,?)");
			ps.setString(1, newSetting.getMaxVisitors());
			ps.setString(2, newSetting.getMaxReservations());
			ps.setString(3, newSetting.getAverageWaitingTime());
			ps.setString(4, newSetting.getStatus());
			ps.setString(5, newSetting.getPark_name());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves pending update setting requests from the database, either for a
	 * specific park or for all parks.
	 *
	 * @param park_Name The name of the park to retrieve requests for, or "ALL" to
	 *                  retrieve requests for all parks.
	 * @return A Message object containing a list of pending UpdateSettingTable
	 *         requests.
	 */
	public static Message<ArrayList<UpdateSettingTable>> GetRequestUpdateSettings(String park_Name) {
		ArrayList<UpdateSettingTable> requestsLst = new ArrayList<UpdateSettingTable>();
		PreparedStatement ps;
		try {
			if (park_Name.equals("ALL")) {
				ps = Jdbc.connection
						.prepareStatement("SELECT * FROM gonature.UpdateSettingTable WHERE Status = 'Pending'");
			} else {
				ps = Jdbc.connection.prepareStatement("SELECT * FROM gonature.UpdateSettingTable WHERE park_name = ?");
				ps.setString(1, park_Name);

			}

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				UpdateSettingTable tmp = new UpdateSettingTable(rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getString(5), rs.getString(6));
				tmp.setId(Integer.parseInt(rs.getString(1)));
				requestsLst.add(tmp);
			}

			rs.close();
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		Message<ArrayList<UpdateSettingTable>> msg = new Message<>("RequestsUpdateSettingTable", requestsLst);

		return msg;
	}

	/**
	 * Updates the status of an update setting request in the database.
	 *
	 * @param tmp The message containing the request to update.
	 */
	public static void ChangeStatusOnSettingRequest(Message<?> tmp) {
		UpdateSettingTable request = (UpdateSettingTable) tmp.getObject();

		try {
			PreparedStatement ps = Jdbc.connection
					.prepareStatement("UPDATE gonature.UpdateSettingTable SET Status = ? WHERE request_id = ?");
			ps.setString(1, request.getStatus());
			ps.setInt(2, request.getId());
			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Updates the parameters (max visitors, max reservations, average visiting time) of a park in the gonature database.
	 *
	 * @param tmp The message containing the updated park information.
	 * @throws SQLException If there is a database error during the update process.
	 */
	public static void ChangeParkParameters(Message<?> tmp) {
		UpdateSettingTable request = (UpdateSettingTable) tmp.getObject();
		try {
			PreparedStatement ps = Jdbc.connection.prepareStatement(
					"UPDATE gonature.Park SET maxVisitros = ?, maxReservations = ?, avgVisitingTime = ? WHERE name = ?");

			ps.setString(1, request.getMaxVisitors());
			ps.setString(2, request.getMaxReservations());
			ps.setString(3, request.getAverageWaitingTime());
			ps.setString(4, request.getPark_name());

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Retrieves information about a park from the gonature database.
	 *
	 * @param park The Park object to be populated with information.
	 * @return The Park object containing the retrieved information, or the original Park object if no information was found.
	 * @throws SQLException If there is a database error during the query process.
	 */
	public static Park getParkInfo(Park park) {
		String query = "SELECT  * FROM gonature.park WHERE name = ?";
		try {
			PreparedStatement statement = Jdbc.connection.prepareStatement(query);
			statement.setString(1, park.getName());
			ResultSet resultSet;
			resultSet = statement.executeQuery();
			if (resultSet.next()) {
				park.setMaxVisitors(resultSet.getInt(2));
				park.setMaxReservations(resultSet.getInt(3));
				park.setAvgVisitingTime(resultSet.getFloat(4));
				park.setCurrentlyVisitorsInPark(resultSet.getInt(5));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return park;
	}
	/**
	 * Retrieves the largest reservation ID from the gonature.Reservation table.
	 *
	 * @return The largest reservation ID, or 0 if there are no reservations or a database error occurs.
	 * @throws SQLException If there is a database error during the query process.
	 */
	public static int getLastId() {
		try {
			PreparedStatement statement = Jdbc.connection
					.prepareStatement("SELECT MAX(reservationId) AS maxId FROM gonature.Reservation");
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				int maxId = resultSet.getInt("maxId");
				return maxId;
			} else {
				return 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	/**
	 * Checks availability for a reservation within a 10-hour window starting from the provided visit date and hour.
	 *
	 * @param reservation The reservation object containing visit details (park, date, hour, number of visitors).
	 * @return An ArrayList of available Date objects within the 10-hour window, or an empty list if no slots are available.
	 * @throws ParseException If there is an error parsing the provided visit date.
	 */
	public static ArrayList<Date> GetAvailableDate(Reservation reservation) {
		ArrayList<Date> lst = new ArrayList<>();
		String format = "MM/dd/yyyy"; // Date format
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar calendar = Calendar.getInstance();

		try {
			calendar.setTime(sdf.parse(reservation.getVisitDate()));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		int hour = Integer.parseInt(reservation.getVisitHour()), counter = 0;

		while (counter < 10) {
			// Set date and hour in the calendar
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, 0); // Set minutes to 0
			// Format the date
			String formattedDate = sdf.format(calendar.getTime());

			if (checkAvailableDateForReservation(reservation.getPark_name(), formattedDate, String.valueOf(hour),
					reservation.getNumberOfVisitors())) {
				lst.add(new Date(formattedDate, String.valueOf(hour)));
				counter += 1;
			}

			// Increment hour
			hour++;
			// Check if hour needs to be reset
			if (hour > 15) {
				hour = 8;
				calendar.add(Calendar.DAY_OF_MONTH, 1); // Increment date when hour resets

			}
		}
		return lst;
	}
	/**
	 * Processes reservations in the waiting list and assigns them spots if available.
	 * Iterates through reservations with "InWaitList" status and checks if enough space is available
	 * in the chosen park at the requested date and hour. If space is available, the reservation is confirmed
	 * and park vacancy is updated accordingly.
	 *
	 * @throws SQLException If there is a database error during the process.
	 */
	public static void GivePlaceToWaitingList() {
		int availablePlace;
		Reservation reservation = null;
		try {
			PreparedStatement statement = Jdbc.connection
					.prepareStatement("SELECT * FROM gonature.Reservation WHERE Status = 'InWaitList'");
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				reservation = new Reservation(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getInt(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9),
						rs.getString(10), rs.getString(11));
				availablePlace = GetAvailablePlaceNumber(reservation.getPark_name(), reservation.getVisitDate(),
						reservation.getVisitHour());
				if (availablePlace >= reservation.getNumberOfVisitors()) {
					UpdateParkVacancy_visitorsWithReservation(reservation.getNumberOfVisitors(),
							reservation.getPark_name(), reservation.getVisitDate(), reservation.getVisitHour());
					reservation.setStatus("Confirmed");
					UpdateReservationStatus(reservation);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Retrieves the number of available places in a park at a specific date and hour.
	 * Calculates available space by subtracting the number of visitors with reservations from the park's maximum capacity.
	 *
	 * @param parkName The name of the park.
	 * @param date The date of the visit (format assumed to be MM/dd/yyyy).
	 * @param hour The hour of the visit.
	 * @return The number of available places in the park at the specified time, or 0 if there is an error.
	 * @throws SQLException If there is a database error during the query process.
	 */
	private static int GetAvailablePlaceNumber(String parkName, String date, String hour) {
		Park park = getParkInfo(new Park(parkName, 0, 0, 0, 0));
		int nonAvailableTime = 0;
		try {
			PreparedStatement statement = Jdbc.connection.prepareStatement(
					"SELECT  visitors_withReservation FROM gonature.ParkVacancy WHERE ParkName = ? AND Date = ? AND Hour = ?");
			statement.setString(1, parkName);
			statement.setString(2, date);
			statement.setString(3, hour);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				nonAvailableTime = Integer.parseInt(resultSet.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return park.getMaxReservations() - nonAvailableTime;
	}
	/**
	 * Checks if a user is registered as a guide in the gonature database.
	 *
	 * @param id The user's ID.
	 * @return True if the user is registered as a guide, false otherwise.
	 * @throws SQLException If there is a database error during the query process.
	 */
	public static boolean checkIfGuide(String id) {
		try {
			PreparedStatement statement = Jdbc.connection
					.prepareStatement("SELECT  Guide FROM gonature.users WHERE id = ?");
			statement.setString(1, id);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				if (resultSet.getString(1).equals("1")) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;

	}
	/**
	 * Inserts a new entry into the gonature.genereteVisitingReport table, generating a visiting report.
	 *
	 * @param id The user's ID.
	 * @param isGroup Indicates whether the visit is a group visit.
	 * @throws SQLException If there is a database error during the insertion process.
	 */
	public static void InsertToGenereteVisitingReport(String id, String isGroup) {
		LocalDate currentDate = LocalDate.now();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		String date = currentDate.format(dateFormatter);
		String[] dateParts = date.split("/");
		String month = dateParts[0];
		String year = dateParts[2];

		LocalTime now = LocalTime.now();
		String entertime = String.valueOf(now.getHour() + ":" + now.getMinute());
		try {
			PreparedStatement statement = Jdbc.connection
					.prepareStatement("INSERT INTO gonature.genereteVisitingReport VALUES(?,?,?,?,?,?)");
			statement.setString(1, id);
			statement.setString(2, entertime);
			statement.setString(3, "-");
			statement.setString(4, year);
			statement.setString(5, month);
			statement.setString(6, isGroup);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Updates the exit time in the `genereteVisitingReport` table for a specific reservation.
	 *
	 * @param reservationNumber The reservation number used as the key identifier in the report.
	 * @throws SQLException If there is a database error during the update process.
	 */
	public static void UpdateExitTimeInGenereteVisitingReport(String reservationNumber) {
		try {
			LocalTime now = LocalTime.now();
			String exittime = String.valueOf(now.getHour() + ":" + now.getMinute());
			PreparedStatement statement = Jdbc.connection.prepareStatement(
					"UPDATE gonature.genereteVisitingReport SET exitTime = ? WHERE keyId = ? and exitTime = '-'");
			statement.setString(1, exittime);
			statement.setString(2, reservationNumber);

			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	/**
	 * Updates the number of currently visiting visitors in a park.
	 *
	 * @param park_name The name of the park.
	 * @param numberOfVisitors The number of visitors to be added to the current count.
	 * @throws SQLException If there is a database error during the update process.
	 */
	public static void updateCurrentlyVisitors(String park_name, int numberOfVisitors) {
		try {
			PreparedStatement statement = Jdbc.connection.prepareStatement(
					"UPDATE gonature.Park SET currentlyVisitorsInPark = currentlyVisitorsInPark + ? WHERE name = ?");
			statement.setInt(1, numberOfVisitors);
			statement.setString(2, park_name);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Inserts a new entry into the gonature.VisitorsWithoutReservation table for visitors without a reservation.
	 *
	 * @param visitors A TravelerWithoutReservation object containing visitor information.
	 * @param date The date of the visit in MM/dd/yyyy format.
	 * @throws SQLException If there is a database error during the insertion process.
	 */
	public static void InsertToVisitorsWithoutReservation(TravelerWithoutReservation visitors, String Date) {
		try {
			String[] dateParts = Date.split("/");
			String month = dateParts[0];
			String year = dateParts[2];

			PreparedStatement statement = Jdbc.connection
					.prepareStatement("INSERT INTO gonature.VisitorsWithoutReservation VALUES(?,?,?,?,?,?,?)");
			statement.setString(1, visitors.getPark());
			statement.setString(2, visitors.getId());
			statement.setString(3, month);
			statement.setString(4, year);
			statement.setInt(5, visitors.getNumberOfVisitors());
			statement.setString(6, visitors.getIsGroup());
			statement.setString(7, visitors.getStatus());

			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	/**
	 * Creates a TotalVisitorsReport object, retrieving visitor data from the database and inserting the report into the gonature.TotalVisitorsReport table.
	 *
	 * @param tmp A Message<?> object containing a Report object with park name, month, and year information.
	 * @return A populated TotalVisitorsReport object.
	 * @throws SQLException If there is a database error during the process.
	 */
	@SuppressWarnings("unchecked")
	public static TotalVisitorsReport CreateTotalVisitorsReport(Message<?> tmp) {
		Report msg = ((Message<Report>) tmp).getObject();
		TotalVisitorsReport report = new TotalVisitorsReport(msg);
		int PlannedGroupsNumber, plannedIndividualsNumber;
		int UnPlannedGroupsNumber, UnplannedIndividualsNumber;
		try {
			// PLANNED
			PreparedStatement statement = Jdbc.connection.prepareStatement(
					"SELECT SUM(numberOfVisitors) AS Planned_total_visitors FROM gonature.Reservation WHERE  YEAR(STR_TO_DATE(visitDate, '%m/%d/%Y')) = ?  AND MONTH(STR_TO_DATE(visitDate, '%m/%d/%Y')) = ? AND park_name = ? AND isGroup = ? AND Status = ?");

			statement.setString(1, msg.getYear());
			statement.setString(2, msg.getMonth());
			statement.setString(3, msg.getParkName());
			statement.setString(4, "1");
			statement.setString(5, "Visited");
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				PlannedGroupsNumber = resultSet.getInt("Planned_total_visitors");
			} else {
				PlannedGroupsNumber = 0;
			}
			PreparedStatement statement1 = Jdbc.connection.prepareStatement(
					"SELECT SUM(numberOfVisitors) AS Planned_total_visitors FROM gonature.Reservation WHERE  YEAR(STR_TO_DATE(visitDate, '%m/%d/%Y')) = ?  AND MONTH(STR_TO_DATE(visitDate, '%m/%d/%Y')) = ?  AND park_name = ? AND isGroup = ? AND Status = ?");
			statement1.setString(1, msg.getYear());
			statement1.setString(2, msg.getMonth());
			statement1.setString(3, msg.getParkName());
			statement1.setString(4, "0");
			statement1.setString(5, "Visited");
			ResultSet resultSet1 = statement1.executeQuery();
			if (resultSet1.next()) {
				plannedIndividualsNumber = resultSet1.getInt("Planned_total_visitors");
			} else {
				plannedIndividualsNumber = 0;
			}
			// UNPLANNED
			PreparedStatement statement3 = Jdbc.connection.prepareStatement(
					"SELECT SUM(numberOfVisitors) AS UnPlanned_total_visitors FROM gonature.VisitorsWithoutReservation  WHERE  month = ?  AND year = ?  AND park = ? AND isGroup = ? ");

			statement3.setString(1, msg.getMonth());
			statement3.setString(2, msg.getYear());
			statement3.setString(3, msg.getParkName());
			statement3.setString(4, "1");
			ResultSet resultSet3 = statement3.executeQuery();
			if (resultSet3.next()) {
				UnPlannedGroupsNumber = resultSet3.getInt("UnPlanned_total_visitors");
			} else {
				UnPlannedGroupsNumber = 0;
			}

			PreparedStatement statement41 = Jdbc.connection.prepareStatement(
					"SELECT SUM(numberOfVisitors) AS UnPlanned_total_visitors FROM gonature.VisitorsWithoutReservation  WHERE  month = ?  AND year = ?  AND park = ? AND isGroup = ? ");
			statement41.setString(1, msg.getMonth());
			statement41.setString(2, msg.getYear());
			statement41.setString(3, msg.getParkName());
			statement41.setString(4, "0");
			ResultSet resultSet41 = statement41.executeQuery();
			if (resultSet41.next()) {
				UnplannedIndividualsNumber = resultSet41.getInt("UnPlanned_total_visitors");
			} else {
				UnplannedIndividualsNumber = 0;
			}
			report.setIndividualsNumbers(String.valueOf(UnplannedIndividualsNumber + plannedIndividualsNumber));
			report.setGroupsNumber(String.valueOf(UnPlannedGroupsNumber + PlannedGroupsNumber));

			PreparedStatement ps = Jdbc.connection.prepareStatement(
					"INSERT INTO gonature.TotalVisitorsReport ( month, year, ParkName, GroupsNumber, IndividualsNumbers)VALUES(?,?,?,?,?)");
			ps.setString(1, report.getMonth());
			ps.setString(2, report.getYear());
			ps.setString(3, report.getParkName());
			ps.setString(4, report.getGroupsNumber());
			ps.setString(5, report.getIndividualsNumbers());
			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return report;

	}
	/**
	 * Retrieves the total number of planned visitors for a given group status.
	 *
	 * @param msg The Report object containing park, month, and year information.
	 * @param isGroup True if retrieving for groups, false if retrieving for individuals.
	 * @return The total number of planned visitors.
	 * @throws SQLException If there is a database error during the query.
	 */
	@SuppressWarnings("unchecked")
	public static TotalVisitorsReport GetTotalVisitorsReport(Message<?> tmp) {
		Report info = (Report) tmp.getObject();
		TotalVisitorsReport report = null;
		try {
			PreparedStatement statement = Jdbc.connection.prepareStatement(
					"SELECT GroupsNumber, IndividualsNumbers FROM gonature.TotalVisitorsReport WHERE year = ? AND month = ? AND ParkName = ?");
			statement.setString(1, info.getYear());
			statement.setString(2, info.getMonth());
			statement.setString(3, info.getParkName());
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				report = new TotalVisitorsReport(info.getMonth(), info.getYear(), info.getParkName(),
						resultSet.getString(1), resultSet.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return report;

	}

	/**
	 * Updates the number of visitors with reservations for a specific park, date, and hour in the gonature.ParkVacancy table.
	 *
	 * @param visitorsWithReservation The number of visitors to add to the current count.
	 * @param parkName The name of the park.
	 * @param date The date of the visit (format assumed to be MM/dd/yyyy).
	 * @param hour The hour of the visit.
	 * @return True if the update was successful, false otherwise.
	 * @throws SQLException If there is a database error during the update process.
	 */
	public static boolean UpdateParkVacancy_visitorsWithReservation(int visitorsWithReservation, String parkName,
			String date, String hour) {

		try (PreparedStatement statement = Jdbc.connection.prepareStatement(
				"UPDATE gonature.ParkVacancy SET visitors_withReservation = visitors_withReservation + ? WHERE ParkName = ? AND Date= ? AND Hour=?")) {
			statement.setInt(1, visitorsWithReservation);
			statement.setString(2, parkName);
			statement.setString(3, date);
			statement.setString(4, hour);

			int rowsUpdated = statement.executeUpdate();

			// Return false if no rows were affected
			return rowsUpdated > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}
	/**
	 * Inserts a new entry into the gonature.ParkVacancy table, representing the vacancy information for a park at a specific date and hour.
	 *
	 * @param parkName The name of the park.
	 * @param date The date of the visit (format assumed to be MM/dd/yyyy).
	 * @param hour The hour of the visit.
	 * @param visitorsWithReservation The number of visitors with reservations.
	 * @param visitorsWithoutReservation The number of visitors without reservations.
	 * @throws SQLException If there is a database error during the insertion process.
	 */
	public static void insertToParkVacancy(String parkName, String date, String hour, int visitors_withReservation,
			int visitors_withoutResevation) {
		try {
			PreparedStatement statement = Jdbc.connection
					.prepareStatement("INSERT INTO gonature.ParkVacancy VALUES(?,?,?,?,?)");
			statement.setString(1, parkName);
			statement.setString(2, date);
			statement.setString(3, hour);
			statement.setInt(4, visitors_withReservation);
			statement.setInt(5, visitors_withoutResevation);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	/**
	 * Checks if there is enough space available in a park for a reservation.
	 *
	 * @param reservation A Reservation object containing park, date, time, and visitor information.
	 * @return True if there is space available, false otherwise.
	 * @throws SQLException If there is a database error during the query process.
	 */
	@SuppressWarnings("unchecked")
	public static boolean checkAvailibility(Reservation reservation) {
		Park park = new Park(reservation.getPark_name(), 0, 0, 0, 0);

		int numOfVisitors = reservation.getNumberOfVisitors();
		int currentRes;
		try {
			park = getParkInfo(park);
			PreparedStatement statement = Jdbc.connection.prepareStatement(
					"SELECT  visitors_withReservation FROM gonature.ParkVacancy WHERE ParkName = ? AND Date = ? AND Hour = ?");
			statement.setString(1, park.getName());
			statement.setString(2, reservation.getVisitDate());
			statement.setString(3, reservation.getVisitHour());
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				currentRes = Integer.parseInt(resultSet.getString(1));
				if ((currentRes + numOfVisitors) <= park.getMaxReservations()) {
					UpdateParkVacancy_visitorsWithReservation(numOfVisitors, park.getName(), reservation.getVisitDate(),
							reservation.getVisitHour());
					return true;
				}
			} else {
				insertToParkVacancy(park.getName(), reservation.getVisitDate(), reservation.getVisitHour(),
						numOfVisitors, 0);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();

		}
		return false;

	}
	/**
	 * Checks if there is enough space available in a park for a reservation at a specific date and hour.
	 *
	 * @param parkName The name of the park.
	 * @param date The date of the visit in MM/dd/yyyy format.
	 * @param hour The hour of the visit.
	 * @param visitorsNumber The number of visitors in the reservation.
	 * @return True if there is enough space available, false otherwise.
	 * @throws SQLException If there is a database error during the process.
	 */
	@SuppressWarnings("unchecked")
	public static boolean checkAvailableDateForReservation(String parkName, String date, String hour,
			int visitorsNumber) {
		Park park = new Park(parkName, 0, 0, 0, 0);
		park = getParkInfo(park);
		int currentRes;
		try {
			PreparedStatement statement = Jdbc.connection.prepareStatement(
					"SELECT  visitors_withReservation FROM gonature.ParkVacancy WHERE ParkName = ? AND Date = ? AND Hour = ?");
			statement.setString(1, parkName);
			statement.setString(2, date);
			statement.setString(3, hour);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				currentRes = resultSet.getInt(1);
				if ((currentRes + visitorsNumber) <= park.getMaxReservations()) {
					return true;
				}
			} else {
				insertToParkVacancy(parkName, date, hour, 0, 0);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();

		}
		return false;

	}
	/**
	 * Checks if there is enough space available in a park for visitors without reservations at a specific date and hour.
	 * This method considers the park's maximum capacity and the number of visitors with reservations.
	 *
	 * @param parkName The name of the park.
	 * @param visitorsNumber The number of visitors without reservations.
	 * @param date The date of the visit in MM/dd/yyyy format.
	 * @param hour The hour of the visit.
	 * @return True if there is enough space available, false otherwise.
	 * @throws SQLException If there is a database error during the process.
	 */

	public static boolean checkVacancy_WithoutReservation(String parkName, int visitorsNumber, String date,
			String hour) {
		Park park = new Park(parkName, 0, 0, 0, 0);
		park = getParkInfo(park);
		int currentVisitorsWithoutReservation;
		try {
			PreparedStatement statement = Jdbc.connection.prepareStatement(
					"SELECT  visitors_withoutResevation FROM gonature.ParkVacancy WHERE ParkName = ? AND Date = ? AND Hour = ?");
			statement.setString(1, parkName);
			statement.setString(2, date);
			statement.setString(3, hour);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				currentVisitorsWithoutReservation = resultSet.getInt(1);
				if ((currentVisitorsWithoutReservation + visitorsNumber) <= park.getMaxVisitors()
						- park.getMaxReservations()) {
					return true;
				}
			} else {
				insertToParkVacancy(parkName, date, hour, 0, 0);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();

		}
		return false;
	}
	/**
	 * Updates the number of visitors without reservations for a specific park, date, and hour in the gonature.ParkVacancy table.
	 *
	 * @param visitorsWithoutReservation The number of visitors to add to the current count.
	 * @param parkName The name of the park.
	 * @param date The date of the visit (format assumed to be MM/dd/yyyy).
	 * @param hour The hour of the visit.
	 * @return True if the update was successful, false otherwise.
	 * @throws SQLException If there is a database error during the update process.
	 */
	public static boolean UpdateParkVacancy_visitorsWithoutReservation(int visitors_withoutResevation, String parkName,
			String date, String hour) {
		try (PreparedStatement statement = Jdbc.connection.prepareStatement(
				"UPDATE gonature.ParkVacancy SET visitors_withoutResevation = visitors_withoutResevation + ? WHERE ParkName = ? AND Date= ? AND Hour=?")) {
			statement.setInt(1, visitors_withoutResevation);
			statement.setString(2, parkName);
			statement.setString(3, date);
			statement.setString(4, hour);

			int rowsUpdated = statement.executeUpdate();

			// Return false if no rows were affected
			return rowsUpdated > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}
	/**
	 * Retrieves information about a traveler without a reservation from the gonature.VisitorsWithoutReservation table.
	 *
	 * @param traveler The `TravelerWithoutReservation` object containing the ID to query.
	 * @return The traveler's information, or null if a matching record is not found.
	 * @throws SQLException If there is a database error during the query process.
	 */
	public static TravelerWithoutReservation getTravelerWithoutReservationInfo(TravelerWithoutReservation Visitors) {
		TravelerWithoutReservation info = null;
		try {
			PreparedStatement statement = Jdbc.connection
					.prepareStatement("SELECT  park,status FROM gonature.VisitorsWithoutReservation WHERE id = ?");
			statement.setString(1, Visitors.getId());
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				info = new TravelerWithoutReservation(Visitors.getId(), rs.getString(1));
				info.setStatus(rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return info;
	}


	@SuppressWarnings("unchecked")
	public static CancellationsReport CreateCancellationReport(Message<?> tmp) {
		Report msg = ((Message<Report>) tmp).getObject();
		CancellationsReport report = new CancellationsReport(msg);
		int CanceledOrderNumber, NotVisitedOrderNumber;
		String Area = report.getArea();
		if (Area.equals("All")) {
			PreparedStatement statement1;
			try {
				statement1 = Jdbc.connection.prepareStatement(
						"SELECT COUNT(*) AS canceledCount FROM gonature.Reservation WHERE Status = ? AND YEAR(STR_TO_DATE(visitDate, '%m/%d/%Y')) = ? AND MONTH(STR_TO_DATE(visitDate, '%m/%d/%Y')) = ?");

				statement1.setString(1, "Canceled");
				statement1.setString(2, msg.getYear());
				statement1.setString(3, msg.getMonth());
				ResultSet resultSet1 = statement1.executeQuery();
				if (resultSet1.next()) {
					CanceledOrderNumber = resultSet1.getInt("canceledCount");
				} else {
					CanceledOrderNumber = 0;
				}
				PreparedStatement statement3;
				statement3 = Jdbc.connection.prepareStatement(
						"SELECT COUNT(*) AS NotVisitedCount FROM gonature.Reservation WHERE Status = ? AND YEAR(STR_TO_DATE(visitDate, '%m/%d/%Y')) = ? AND MONTH(STR_TO_DATE(visitDate, '%m/%d/%Y')) = ?");

				statement3.setString(1, "NotVisited");
				statement3.setString(2, msg.getYear());
				statement3.setString(3, msg.getMonth());
				ResultSet resultSet3 = statement3.executeQuery();
				if (resultSet3.next()) {
					NotVisitedOrderNumber = resultSet3.getInt("NotVisitedCount");
				} else {
					NotVisitedOrderNumber = 0;
				}
				report.setCanceledOrder(String.valueOf(CanceledOrderNumber));
				report.setNotVisitedOrders(String.valueOf(NotVisitedOrderNumber));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				PreparedStatement statement2 = Jdbc.connection.prepareStatement(
						"SELECT COUNT(*) AS canceledCount FROM gonature.Reservation WHERE Status = ? AND YEAR(STR_TO_DATE(visitDate, '%m/%d/%Y')) = ? AND MONTH(STR_TO_DATE(visitDate, '%m/%d/%Y')) = ? AND park_name= ?");
				statement2.setString(1, "Canceled");
				statement2.setString(2, msg.getYear());
				statement2.setString(3, msg.getMonth());
				statement2.setString(4, Area);
				ResultSet resultSet2 = statement2.executeQuery();
				if (resultSet2.next()) {
					CanceledOrderNumber = resultSet2.getInt("canceledCount");
				} else {
					CanceledOrderNumber = 0;
				}
				PreparedStatement statement4 = Jdbc.connection.prepareStatement(
						"SELECT COUNT(*) AS NotVisitedCount FROM gonature.Reservation WHERE Status = ? AND YEAR(STR_TO_DATE(visitDate, '%m/%d/%Y')) = ? AND MONTH(STR_TO_DATE(visitDate, '%m/%d/%Y')) = ? AND park_name= ?");
				statement4.setString(1, "NotVisited");
				statement4.setString(2, msg.getYear());
				statement4.setString(3, msg.getMonth());
				statement4.setString(4, Area);
				ResultSet resultSet4 = statement4.executeQuery();
				if (resultSet4.next()) {
					NotVisitedOrderNumber = resultSet4.getInt("NotVisitedCount");
				} else {
					NotVisitedOrderNumber = 0;
				}
				report.setCanceledOrder(String.valueOf(CanceledOrderNumber));
				report.setNotVisitedOrders(String.valueOf(NotVisitedOrderNumber));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		try {
			PreparedStatement ps = Jdbc.connection.prepareStatement(
					"INSERT INTO gonature.CancellationReport ( month, year, area, CanceledOrders, NotVisitedOrders)VALUES(?,?,?,?,?)");
			ps.setString(1, msg.getMonth());
			ps.setString(2, msg.getYear());
			ps.setString(3, Area);
			ps.setString(4, report.getCanceledOrder());
			ps.setString(5, report.getNotVisitedOrders());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return report;
	}
	/**
	 * Generates a `CancellationsReport` object containing cancellation and not-visited order counts based on a provided `Message` object
	 * and optionally a park area. The report data is also inserted into the `gonature.CancellationReport` table.
	 *
	 * @param message The message object containing report information (likely month and year).
	 * @return The generated `CancellationsReport` object.
	 * @throws SQLException If there is a database error during the report generation or insertion process.
	 */
	public static CancellationsReport GetCancellationReport(Message<?> msg) {

		Report tmp = (Report) msg.getObject();
		CancellationsReport report = null;
		;
		String Area = tmp.getParkName();

		try {
			PreparedStatement statement = Jdbc.connection.prepareStatement(
					"SELECT CanceledOrders,NotVisitedOrders FROM gonature.CancellationReport WHERE year = ? AND month = ? AND area = ?");
			statement.setString(1, tmp.getYear());
			statement.setString(2, tmp.getMonth());
			statement.setString(3, Area);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {

				report = new CancellationsReport(tmp);
				report.setCanceledOrder(resultSet.getString(1));
				report.setNotVisitedOrders(resultSet.getString(2));
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return report;
	}
	/**
	 * Generates a report listing dates and times when a specific park was not full during a given month and year.
	 *
	 * @param year The year for which to generate the report.
	 * @param month The numeric representation of the month (1-12) for which to generate the report.
	 * @param park The park object representing the park to check occupancy for.
	 * @return An ArrayList containing `GenereteUsageReport` objects, each representing a date and time when the park was not full.
	 * @throws ParseException If there is an error parsing the start date.
	 */
	public static ArrayList<GenereteUsageReport> getWhenParkWasNotFull(String year, String month, Park park) {
		ArrayList<GenereteUsageReport> report = new ArrayList<>();
		String format = "MM/dd/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar calendar = Calendar.getInstance();
		String startDate = month + "/01/" + year;
		try {
			calendar.setTime(sdf.parse(startDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int hour = 8;
		while (calendar.get(Calendar.MONTH) == Integer.parseInt(month) - 1) {
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, 0);
			String formattedDate = sdf.format(calendar.getTime());
			if (checkParkVacancyNotFull(park.getName(), formattedDate, String.valueOf(hour), park.getMaxVisitors())) {
				report.add(new GenereteUsageReport(formattedDate, String.valueOf(hour), park.getName()));
			}
			hour++;
			if (hour > 16) {
				hour = 8;
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
			if (calendar.get(Calendar.DAY_OF_MONTH) > calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
				break;
			}
		}
		return report;

	}
	
	/**
	 * Checks if a park was not full at a specified date and hour based on park vacancy data.
	 *
	 * @param parkName The name of the park to check occupancy for.
	 * @param date The date to check (formatted as MM/dd/yyyy).
	 * @param hour The hour of the day to check (in 24-hour format).
	 * @param maxVisitors The maximum capacity of the park.
	 * @return True if the park was not full at the specified date and hour, false otherwise.
	 */
	private static boolean checkParkVacancyNotFull(String parkName, String date, String hour, int MaxVisitors) {
		int visitorsReservation, withoutReservation;
		try {
			PreparedStatement statement = Jdbc.connection.prepareStatement(
					"SELECT  visitors_withReservation,visitors_withoutResevation FROM gonature.ParkVacancy WHERE parkName = ? AND Date = ? AND Hour = ?");
			statement.setString(1, parkName);
			statement.setString(2, date);
			statement.setString(3, hour);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				visitorsReservation = Integer.parseInt(rs.getString(1));
				withoutReservation = Integer.parseInt(rs.getString(2));
				if ((visitorsReservation + withoutReservation) < MaxVisitors) {
					return true;
				}
				return false;

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	/**
	 * Sends SMS notifications to users with confirmed reservations for the following day, reminding them to confirm or cancel within 2 hours.
	 * Also updates reservation statuses and creates wait approval records.
	 */
	public static void notifySMSUserOneDayBefore() {
		try {
			PreparedStatement statement = Jdbc.connection.prepareStatement(
					"SELECT reservationId,userID  FROM gonature.Reservation WHERE STR_TO_DATE(visitDate, '%m/%d/%Y') = DATE(DATE_ADD(CURDATE(), INTERVAL 1 DAY)) AND Status = ?");
			statement.setString(1, "Confirmed");
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				String UserId = rs.getString(2);
				int ReservationId = rs.getInt(1);
				String Sms = "Your Visit is for tommorow! ,you have 2 hours from now to confirm/cancel your reservation ";
				statement = Jdbc.connection.prepareStatement(
						"INSERT INTO gonature.SMS (UserId, reservationNumber, message) VALUES (?,?,?)");
				statement.setString(1, UserId);
				statement.setInt(2, ReservationId);
				statement.setString(3, Sms);
				statement.executeUpdate();
				statement = Jdbc.connection.prepareStatement(
						"UPDATE gonature.Reservation SET Status = 'waitApprove' WHERE reservationId = ? AND STR_TO_DATE(visitDate, '%m/%d/%Y') = DATE(DATE_ADD(CURDATE(), INTERVAL 1 DAY)) AND Status = ?");
				statement.setInt(1, ReservationId);
				statement.setString(2, "Confirmed");
				statement.executeUpdate();
				statement = Jdbc.connection
						.prepareStatement("INSERT INTO gonature.waitApprove (reservationId, smsTime) VALUES (?,?)");
				statement.setInt(1, ReservationId);
				LocalTime currentTime = LocalTime.now();
				DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH:mm");
				String hour = hourFormatter.format(currentTime);
				statement.setString(2, hour);
				statement.executeUpdate();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Cancels reservations that have not been approved within 2 hours of receiving an SMS reminder.
	 * Also removes the corresponding wait approval records and sends cancellation notifications to users.
	 */
	public static void CancelOrderUserTwoHoursAfter() {
		try {
			PreparedStatement statement = Jdbc.connection.prepareStatement("SELECT * FROM gonature.waitapprove");
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				int reservationId = rs.getInt(1);
				LocalTime currentTime = LocalTime.now();
				DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH:mm");
				String hour = hourFormatter.format(currentTime);
				String[] smsTime = rs.getString(2).split(":");
				String[] currTime = hour.split(":");
				if (calculateMinutesBetween(smsTime, currTime) >= 2f) {
					statement = Jdbc.connection
							.prepareStatement("UPDATE gonature.Reservation SET Status = ? WHERE reservationId = ?");
					statement.setString(1, "Canceled");
					statement.setInt(2, reservationId);
					statement.executeUpdate();
					statement = Jdbc.connection
							.prepareStatement("DELETE FROM gonature.waitapprove WHERE reservationId = ?");
					statement.setInt(1, reservationId);
					statement.executeUpdate();
					Reservation r = getReservationByNumber(reservationId);
					DataBaseController.sendNotification(r.getUserID(), reservationId,
							"Reservation NOT APPROVED SO it will Canceled automatically.");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	/**
	 * Generates a list of average waiting times for each hour of a day, based on visit data for a given month and year.
	 * Differentiates between individual and group visits for calculating average waiting times.
	 *
	 * @param tmp A Message object containing a Report object with year and month information.
	 * @return An ArrayList of Float values, where each value represents the average waiting time in minutes for a specific hour of the day (0-15).
	 * @throws SQLException If a database error occurs.
	 */
	public static ArrayList<Float> GetVisitsReport(Message<?> tmp) {
		ArrayList<Float> averageWaitingTime = new ArrayList<Float>(16);
		ArrayList<Integer> counter = new ArrayList<>(16);

		int index;
		for (int i = 0; i < 16; i++) {
			averageWaitingTime.add((float) 0);
			counter.add(0);
		}

		Report report = (Report) tmp.getObject();

		try {
			PreparedStatement statement = Jdbc.connection.prepareStatement(
					"SELECT enterTime,exitTime,isGroup FROM gonature.genereteVisitingReport WHERE  year = ? AND month = ?");
			statement.setString(1, report.getYear());
			statement.setString(2, report.getMonth());

			ResultSet rs = statement.executeQuery();

			while (rs.next()) {

				String[] enterTime = rs.getString(1).split(":");
				String[] exitTime = rs.getString(2).split(":");

				if (rs.getString(3).equals("1")) {
					index = Integer.parseInt(enterTime[0]);
				} else {
					index = Integer.parseInt(enterTime[0]) - 8;
				}

				averageWaitingTime.set(index,
						averageWaitingTime.get(index) + calculateMinutesBetween(enterTime, exitTime));

				counter.set(index, counter.get(index) + 1);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < 16; i++) {
			if (counter.get(i) != 0) {
				averageWaitingTime.set(i, averageWaitingTime.get(i) / counter.get(i));
			}
		}
		return averageWaitingTime;
	}
	/**
	 * Calculates the difference in minutes between two times represented as string arrays.
	 *
	 * @param enterTime A string array containing the entry time in the format [hour, minute].
	 * @param exitTime A string array containing the exit time in the format [hour, minute].
	 * @return The difference in minutes between the two times, as a float.
	 * @throws IllegalArgumentException If either time string array is invalid or has incorrect length.
	 */
	public static float calculateMinutesBetween(String[] enterTime, String[] exitTime) {


		int enterHour = Integer.parseInt(enterTime[0]);
		int enterMinute = Integer.parseInt(enterTime[1]);
		int exitHour = Integer.parseInt(exitTime[0]);
		int exitMinute = Integer.parseInt(exitTime[1]);

		int enterMinutes = enterHour * 60 + enterMinute;
		int exitMinutes = exitHour * 60 + exitMinute;

		int minutesDiff = exitMinutes - enterMinutes;

		return minutesDiff / 60f;
	}
	/**
	 * Updates the status of user-approved reservations to "NotVisited" if the visit date has passed
	 * (including the current date and time if the visit hour has already passed).
	 */
	public static void UpdateStatusNotVisitedReservation() {
		PreparedStatement statement;
		try {
			statement = Jdbc.connection.prepareStatement(
					"SELECT reservationId , visitDate , visitHour FROM gonature.Reservation WHERE Status = ? ");
			statement.setString(1, "User_Approved");
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				int reservationId = rs.getInt(1);
				String visitDateStr = rs.getString(2);
				int visitHourStr = Integer.valueOf(rs.getString(3));
				DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
				LocalDate visitDate = LocalDate.parse(visitDateStr, dateFormatter);
				LocalDate currentDate = LocalDate.now();
				int currentTime = LocalTime.now().getHour();

				// Check if the reservation date is before the current date or if it's the
				// current date but the time has already passed
				if (visitDate.isEqual(currentDate) && visitHourStr < currentTime) {
					statement = Jdbc.connection
							.prepareStatement("UPDATE gonature.Reservation SET Status = ? WHERE reservationId = ?");
					statement.setString(1, "NotVisited");
					statement.setInt(2, reservationId);
					statement.executeUpdate();
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	/**
	 * Retrieves all SMS notifications for a given user ID and combines them into a single string.
	 *
	 * @param userId The user ID to retrieve notifications for.
	 * @return A string containing all SMS notifications for the user, separated by newlines.
	 * @throws SQLException If a database error occurs.
	 */
	public static String getNotification(String userId) {
		StringBuilder msg = new StringBuilder();

		PreparedStatement statement;
		try {
			statement = Jdbc.connection
					.prepareStatement("SELECT reservationNumber,message FROM gonature.SMS WHERE Userid = ?  ");
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				msg.append("# Reservation Number" + rs.getString(1) + " :  " + rs.getString(2) + "\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return msg.toString();
	}
	/**
	 * Sends a new SMS notification to a user for a specific reservation with a provided message.
	 *
	 * @param userId The user ID to send the notification to.
	 * @param reservationId The reservation ID associated with the notification.
	 * @param message The content of the SMS notification.
	 * @throws SQLException If a database error occurs.
	 */
	public static void sendNotification(String userId, int reservationId, String msg) {
		PreparedStatement statement;
		try {
			statement = Jdbc.connection.prepareStatement("INSERT INTO gonature.sms VALUES (?,?,?)");
			statement.setString(1, userId);
			statement.setInt(2, reservationId);
			statement.setString(3, msg);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
