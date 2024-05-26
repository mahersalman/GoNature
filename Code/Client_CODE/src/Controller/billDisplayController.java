package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
/**
 * Controller class for managing the bill display interface.
 */
public class billDisplayController {

    @FXML
    private Button backBtn;

    @FXML
    private Label BillLbl;

    /**
     * Sets the bill label text.
     *
     * @param bill The bill text to set.
     */
    public void setBillLbl(String bill) {
    	BillLbl.setText(bill);
    }
}
