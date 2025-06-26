package Batch1_POSG4.controller;

import java.io.IOException;
import java.sql.*;
import Batch1_POSG4.model.User;
import Batch1_POSG4.util.Sha256Hasher;
import Batch1_POSG4.util.Session;          // ← import your Session singleton
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

public class LoginController {
    @FXML private TextField    usernameField;
    @FXML private PasswordField passwordField;

    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:db/db_pos_g4.db");
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String user = usernameField.getText().trim();
        String pass1 = passwordField.getText();

        if (user.isBlank() || pass1.isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Please fill in both fields.").showAndWait();
            return;
        }

        String pass = new Sha256Hasher(pass1).getHashedOutput();
        try (Connection conn = connect()) {
            String sql = "SELECT * FROM tbl_User WHERE username = ? AND password_hash = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user);
                stmt.setString(2, pass);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        new Alert(Alert.AlertType.ERROR, "Invalid username or password.").showAndWait();
                        return;
                    }

                    // 1) Build your User model
                    User loggedIn = new User(
                        rs.getLong("user_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                    );

                    // 2) Store it in the Session singleton
                    Session.get().setCurrentUser(loggedIn);

                    // 3) Now load Main Menu
                    FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/Batch1_POSG4/view/POSMainMenu.fxml")
                    );
                    Parent mainMenuRoot = loader.load();

                    Stage stageMenu = new Stage();
                    stageMenu.setScene(new Scene(mainMenuRoot));
                    stageMenu.setTitle("Main Menu");
                    stageMenu.show();

                    // 4) close login window
                    Stage loginStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    loginStage.close();
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Login error:\n" + e.getMessage())
                .showAndWait();
        }
    }

    @FXML private void handleExit() {
        System.exit(0);
    }
}
