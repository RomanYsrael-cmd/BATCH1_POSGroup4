package Batch1_POSG4.controller;

import java.io.IOException;
import java.sql.*;
import Batch1_POSG4.model.User;
import Batch1_POSG4.util.Sha256Hasher;
import Batch1_POSG4.util.Session;         
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
        String user  = usernameField.getText().trim();
        String pass1 = passwordField.getText();

        if (user.isBlank() || pass1.isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Please fill in both fields.").showAndWait();
            return;
        }

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
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Login error:\n" + e.getMessage()).showAndWait();
        }
    }
    @FXML private void handleExit() {
        System.exit(0);
    }
}
