package Batch1_POSG4.controller;

// Standard library imports
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Third-party packages
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

// Project-specific imports
import Batch1_POSG4.dao.LoginSessionDAO;
import Batch1_POSG4.model.LoginSession;
import Batch1_POSG4.model.User;
import Batch1_POSG4.util.DeviceInfoUtil;
import Batch1_POSG4.util.Session;
import Batch1_POSG4.util.Sha256Hasher;

// Handles user login, authentication, and navigation to the main application screens.
public class LoginController {

    // Instance fields (public)

    // Instance fields (private)
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    // Establishes a connection to the SQLite database.
    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:db/db_pos_g4.db");
    }

    // Handles the login button click, authenticates the user, and opens the appropriate screen.
    @FXML
    private void handleLogin(ActionEvent event) {
        String user  = usernameField.getText().trim();
        String pass1 = passwordField.getText();
        if (user.isBlank() || pass1.isBlank()) {
            new Alert(AlertType.WARNING, "Please fill in both fields.").showAndWait();
            return;
        }

        String passHash = new Sha256Hasher(pass1).getHashedOutput();
        User loggedIn;
        try (Connection conn = connect();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM tbl_User WHERE username=? AND password_hash=?")) {
            ps.setString(1, user);
            ps.setString(2, passHash);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    new Alert(AlertType.ERROR, "Invalid username or password.").showAndWait();
                    return;
                }
                loggedIn = new User(
                    rs.getLong("user_id"),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    rs.getString("role"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
                Session.get().setCurrentUser(loggedIn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(AlertType.ERROR, "Login error:\n" + e.getMessage()).showAndWait();
            return;
        }

        // Record the login session in the database.
        try {
            LoginSessionDAO dao = new LoginSessionDAO();
            String ip     = DeviceInfoUtil.getIpAddress();
            String device = DeviceInfoUtil.getDeviceInfo();
            long sid = dao.create(new LoginSession(loggedIn.getUserId(), ip, device));
            Session.get().setSessionId(sid);
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(AlertType.ERROR, "Failed to record session:\n" + e.getMessage()).showAndWait();
            return;
        }

        // Determine which FXML and title to use based on user role.
        String role = loggedIn.getRole();
        String fxml = "EMPLOYEE".equalsIgnoreCase(role)
                        ? "/Batch1_POSG4/view/POSSales.fxml"
                        : "/Batch1_POSG4/view/POSMainMenu.fxml";
        String title = "EMPLOYEE".equalsIgnoreCase(role) ? "Sales Screen" : "Main Menu";

        // Load the appropriate screen and close the login window.
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            new Alert(AlertType.ERROR, "Error loading screen:\n" + ioe.getMessage()).showAndWait();
        }
    }

    // Handles the exit button click, closing the application.
    @FXML
    private void handleExit() {
        Platform.exit();
    }
}