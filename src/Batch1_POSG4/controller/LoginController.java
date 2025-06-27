package Batch1_POSG4.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Batch1_POSG4.util.Sha256Hasher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.Node;

public class LoginController {
    @FXML 
    private TextField usernameField;
    @FXML 
    private PasswordField passwordField;
    
    private Connection connect() {
        try {
            String url = "jdbc:sqlite:db/db_pos_g4.db";
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Database Error:\n" + e.getMessage()).showAndWait();
            return null;
        }
    }
    @FXML
    private void handleLogin(ActionEvent event) {
<<<<<<< Updated upstream
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        String user = usernameField.getText().strip();
=======
        String user  = usernameField.getText().trim();
>>>>>>> Stashed changes
        String pass1 = passwordField.getText();

        if (user .isBlank() || pass1.isBlank()) {
            alert.setContentText("Please do not leave any of the field in blank");
            return;
        }
        else{
            Sha256Hasher pass2 = new Sha256Hasher(pass1);
            String pass = pass2.getHashedOutput();
            //System.out.println(pass);
            try (Connection conn = connect()){
                if (conn == null) return;{
                    String sql = "SELECT * FROM tbl_User WHERE username = ? AND password_hash = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, user);
                    stmt.setString(2, pass);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        alert.setContentText("Login Success");
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Batch1_POSG4/view/POSMainMenu.fxml"));
                            Parent mainMenuRoot = loader.load();

<<<<<<< Updated upstream

                            Stage stageMenu = new Stage();
                            stageMenu.setTitle("Main Menu");
                            stageMenu.setScene(new Scene(mainMenuRoot));
                            stageMenu.show();

                            Stage loginStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            loginStage.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        alert.setContentText("Invalid username or password.");
                    }
=======
        String passHash = new Sha256Hasher(pass1).getHashedOutput();
        try (Connection conn = connect()) {
            String sql = "SELECT * FROM tbl_User WHERE username = ? AND password_hash = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user);
                stmt.setString(2, passHash);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        new Alert(Alert.AlertType.ERROR, "Invalid username or password.").showAndWait();
                        return;
                    }

                    // 1) Build model and store in session
                    User loggedIn = new User(
                        rs.getLong("user_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                    );
                    Session.get().setCurrentUser(loggedIn);

                    // 2) Decide which screen to load
                    String role = rs.getString("role");
                    String fxml  = "EMPLOYEE".equalsIgnoreCase(role)
                                ? "/Batch1_POSG4/view/POSSales.fxml"
                                : "/Batch1_POSG4/view/POSMainMenu.fxml";
                    String title = "EMPLOYEE".equalsIgnoreCase(role)
                                ? "Sales Screen"
                                : "Main Menu";

                    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
                    Parent root = loader.load();

                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle(title);
                    stage.show();

                    // 3) Close login
                    Stage loginStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    loginStage.close();
>>>>>>> Stashed changes
                }
            } catch (SQLException e) {
                alert.setContentText(e.getMessage() + "11");
                new Alert(Alert.AlertType.ERROR, "Database Error:\n" + e.getMessage()).showAndWait();
            }
<<<<<<< Updated upstream
=======
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Login error:\n" + e.getMessage()).showAndWait();
>>>>>>> Stashed changes
        }
        alert.showAndWait();
    }

<<<<<<< Updated upstream
    @FXML private void handleExit(){
=======

    @FXML private void handleExit() {
>>>>>>> Stashed changes
        System.exit(0);
    }
}
