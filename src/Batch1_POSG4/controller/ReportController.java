package Batch1_POSG4.controller;

import Batch1_POSG4.dao.ReportDAO;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class ReportController {

    @FXML
    private ComboBox<String> cmbMonth;   // <-- strongly typed

    private final String dbUrl = "jdbc:sqlite:db/db_pos_g4.db";
    private final ReportDAO reportDao = new ReportDAO(dbUrl);

    /**
     * Called by FXMLLoader after the @FXML fields are injected.
     */
    @FXML
    public void initialize() {
        try {
            // fetch the list of "MMMM yyyy" strings
            ObservableList<String> periods = reportDao.fetchAvailablePeriods();
            cmbMonth.setItems(periods);
        } catch (Exception e) {
            e.printStackTrace();
            // you might want to show an alert to the user here
        }
    }

    /**
     * onAction="#handlesGenerate" from your FXML.
     */
    @FXML
    void handlesGenerate(ActionEvent event) {
        String selected = cmbMonth.getValue();
        if (selected == null) {
            System.out.println("Please select a month first.");
            return;
        }

        try {
            double total = reportDao.fetchTotalForPeriod(selected);
            System.out.println();
            System.out.println("==== Sales Report for " + selected + " ====");
            System.out.printf("Total Sales: ₱%.2f%n", total);
            System.out.println("======================================");
            System.out.println();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
