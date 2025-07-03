package Batch1_POSG4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import Batch1_POSG4.dao.LoginSessionDAO;
import Batch1_POSG4.util.Session;

// Main JavaFX application entry point. Handles startup, shutdown, and session management.
public class App extends Application {

    // Initializes the application and registers a shutdown hook for session cleanup.
    @Override
    public void init() throws Exception {
        super.init();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            closeCurrentSession();
        }));
    }

    // Starts the JavaFX application and loads the login view.
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("view/POSLogin.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Login");
        primaryStage.show();
    }

    // Stops the application and ensures the session is closed.
    @Override
    public void stop() throws Exception {
        super.stop();
        closeCurrentSession();
    }

    // Closes the current login session if one exists.
    private void closeCurrentSession() {
        long sid = Session.get().getSessionId();
        if (sid != 0) {
            try {
                new LoginSessionDAO().closeSession(sid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Launches the JavaFX application.
    public static void main(String[] args) {
        launch(args);
    }
}