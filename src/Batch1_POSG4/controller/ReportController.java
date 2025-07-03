package Batch1_POSG4.controller;

// Standard library imports

// Third-party packages
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

// Project-specific imports
import Batch1_POSG4.dao.ReportDAO;

// Handles report generation and populates the month selection combo box.
public class ReportController {

    // Public constants

    // Private constants

    // Public static fields

    // Private static fields

    // Public instance fields

    // Private instance fields
    @FXML
    private ComboBox<String> cmbMonth;
    private final String dbUrl = "jdbc:sqlite:db/db_pos_g4.db";
    private final ReportDAO reportDao = new ReportDAO(dbUrl);

    // Initializes the controller and populates the month combo box with available periods.
    @FXML
    public void initialize() {
        try {
            ObservableList<String> periods = reportDao.fetchAvailablePeriods();
            cmbMonth.setItems(periods);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Handles the generate action, fetches and prints the sales report for the selected period.
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