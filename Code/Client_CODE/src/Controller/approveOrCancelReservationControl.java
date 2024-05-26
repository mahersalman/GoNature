package Controller;



import client.ClientUI;
import entities.Message;
import entities.Reservation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
/**
 * Controller class for managing the approval or cancellation of a reservation.
 */
public class approveOrCancelReservationControl  {
	private Reservation reservation;

	@FXML
	private Label reservationNumberLbl;

	@FXML
	private Button CancelReservationBtn;

	@FXML
	private Button approveBtn;

	@FXML
	private Label lblNote;

	  /**
     * Handles the action event when the approve button is clicked.
     *
     * @param event The action event triggered by clicking the approve button.
     */
	@FXML
	void approve(ActionEvent event) {
		reservation.setStatus("User_Approved");
		Message<Reservation> msg = new Message<>("Approve_Reservation",reservation);
		ClientUI.chat.accept(msg);
		lblNote.setText("Reservation Approved.");
		lblNote.setTextFill(Color.GREEN);
		
	}

    /**
     * Handles the action event when the cancel reservation button is clicked.
     *
     * @param event The action event triggered by clicking the cancel reservation button.
     */
	@FXML
	public void cancelReservation(ActionEvent event) {
		reservation.setStatus("Canceled");
		Message<Reservation> msg = new Message<>("Cancel_Reservation", reservation);
		ClientUI.chat.accept(msg);
		lblNote.setText("Reservation Canceled.");
		lblNote.setTextFill(Color.RED);
		
	}

    /**
     * Sets the reservation information and updates the UI accordingly.
     *
     * @param reservation The reservation to be displayed and managed.
     */
	public void setReservation(Reservation reservation) {
		if (! reservation.getStatus().equals("waitApprove")) {
			approveBtn.setDisable(true);
			lblNote.setText("**Approve Option Available Day Before Visiting..");
			lblNote.setTextFill(Color.RED);
		}
		this.reservation = reservation;
		reservationNumberLbl.setText(String.valueOf(reservation.getReservationId()));
	}



}
